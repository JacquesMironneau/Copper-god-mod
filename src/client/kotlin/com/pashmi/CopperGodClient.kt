package com.pashmi

import com.pashmi.achievements.CopperOreBreakCounter
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.Text


object CopperGodClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(
            CopperOreBreakCounter.ID
        ) { client: MinecraftClient, _: ClientPlayNetworkHandler, buf: PacketByteBuf, _: PacketSender ->
            val totalDirtBlocksBroken = buf.readInt()
            client.execute {
                client.player!!.sendMessage(Text.literal("Total copper ore mined: $totalDirtBlocksBroken"))
            }
        }
    }
}