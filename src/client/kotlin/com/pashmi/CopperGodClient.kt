package com.pashmi

import com.pashmi.achievements.CopperOreBreakCounter
import com.pashmi.achievements.CopperOreBreakCounter.Companion.INITIAL_SYNC
import com.pashmi.achievements.CopperOreBreakCounter.Companion.OPEN_CUSTOM_BOOK_SCREEN
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf


object CopperGodClient : ClientModInitializer {
    val copperRegistry = PlayerCopperRegistry()

    override fun onInitializeClient() {

        ClientPlayNetworking.registerGlobalReceiver(
            CopperOreBreakCounter.ID
        ) { client: MinecraftClient, _: ClientPlayNetworkHandler, buf: PacketByteBuf, _: PacketSender ->
            val playerToUpdate = buf.readUuid()
            val brokenCopper = buf.readInt()
            client.execute {
                val player = client.player
                player?.let {
                    copperRegistry.put(playerToUpdate, brokenCopper)
                }
            }
        }

        ClientPlayNetworking.registerGlobalReceiver(
            INITIAL_SYNC
        ) { client: MinecraftClient, handler: ClientPlayNetworkHandler?, buf: PacketByteBuf, responseSender: PacketSender? ->
            val mapPlayerCopper = buf.readMap(PacketByteBuf::readUuid, PacketByteBuf::readInt)
            client.execute {
                val player = client.player
                player?.let {
                    mapPlayerCopper.forEach { (k, v) ->
                        copperRegistry.put(k, v)
                    }
                }
            }
        }

        ClientPlayNetworking.registerGlobalReceiver(
            OPEN_CUSTOM_BOOK_SCREEN
        ) { client: MinecraftClient, handler: ClientPlayNetworkHandler, buf: PacketByteBuf, responseSender: PacketSender ->
            val target = buf.readItemStack()
            client.execute {
                client.setScreen(CustomBookScreen(target))
            }
        }
    }
}