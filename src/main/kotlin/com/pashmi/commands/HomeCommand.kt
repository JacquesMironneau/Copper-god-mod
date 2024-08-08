package com.pashmi.commands


import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.pashmi.achievements.StateSaverAndLoader
import com.pashmi.achievements.isDefault
import com.pashmi.utils.logger
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text


class HomeCommand {

    companion object {

        private val logger = logger()

        fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {

            dispatcher.register(CommandManager.literal("sethome").executes { ctx ->
                setHome(ctx.source)
            })


            dispatcher.register(CommandManager.literal("home").executes { ctx ->
                home(ctx.source)
            })

        }

        private fun setHome(source: ServerCommandSource): Int {

            val player = source.player ?: return 0
            val home = StateSaverAndLoader.getPlayerHome(player)

            home.x = player.x
            home.y = player.y
            home.z = player.z

            player.sendMessage(Text.literal("Home set at ${player.pos}"))

            return Command.SINGLE_SUCCESS
        }

        private fun home(source: ServerCommandSource): Int {
            val player = source.player ?: return 0

            val home = StateSaverAndLoader.getPlayerHome(player)

            if (home.isDefault()) {
                player.sendMessage(Text.literal("No home has been set, use /sethome to the desired location"))
            } else {
                player.requestTeleport(home.x, home.y, home.z)

            }

            return Command.SINGLE_SUCCESS
        }


        fun globalHome(source: ServerCommandSource): Int {

            return Command.SINGLE_SUCCESS

        }

        fun setGlobalHome(source: ServerCommandSource): Int {
            return Command.SINGLE_SUCCESS
        }


    }
}