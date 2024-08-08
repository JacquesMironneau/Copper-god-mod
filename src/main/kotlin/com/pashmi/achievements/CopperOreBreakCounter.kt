package com.pashmi.achievements

import com.pashmi.CopperGodMod.MOD_ID
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.block.Blocks
import net.minecraft.util.Identifier


class CopperOreBreakCounter {

    companion object {
        val ID = Identifier(MOD_ID, "copper_ore_break_counter")
        fun initializeCopperOreCounter() {
            PlayerBlockBreakEvents.AFTER.register { world, player, _, state, _ ->
                if (state.block == Blocks.COPPER_ORE) {


                    val data = PacketByteBufs.create()

                    data.writeInt( StateSaverAndLoader.getPlayerState(player)
                        .apply { copperOreBroken += 1 }
                        .copperOreBroken
                    )
                    val server = world.server
                    server?.execute {
                        ServerPlayNetworking.send(server.playerManager.getPlayer(player.uuid), ID, data)
                    }
                }
            }
        }
    }

}