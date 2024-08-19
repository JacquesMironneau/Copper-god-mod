package com.pashmi.achievements

import com.pashmi.CopperGodMod.MOD_ID
import com.pashmi.CopperGodMod.toModId
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.block.Blocks
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier


class CopperOreBreakCounter {

    companion object {
        val INITIAL_SYNC = "initial_sync".toModId()
        val OPEN_CUSTOM_BOOK_SCREEN = "copper_book".toModId()
        val ID = Identifier(MOD_ID, "copper_ore_break_counter")

        fun initializeCopperOreCounter() {
            ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->

                val data = PacketByteBufs.create().apply {
                    writeMap(
                        StateSaverAndLoader.getPlayerCopper(server).mapValues { (_, value) -> value.copperOreBroken },
                        PacketByteBuf::writeUuid,
                        PacketByteBuf::writeInt
                    )
                }
                server.execute { ServerPlayNetworking.send(handler.player, INITIAL_SYNC, data) }
            }


            PlayerBlockBreakEvents.AFTER.register { world, player, _, state, _ ->
                if (state.block == Blocks.COPPER_ORE) {
                    world.server?.let { server ->

                        val playerState = StateSaverAndLoader.getPlayerState(player)
                        playerState.copperOreBroken += 1

                        val data = PacketByteBufs.create().apply {
                            writeUuid(player.uuid)
                            writeInt(playerState.copperOreBroken)
                        }
                        server.execute {
                            server.playerManager.playerList.forEach { ServerPlayNetworking.send(it, ID, data) }
                        }
                    }
                }
            }

        }
    }

}