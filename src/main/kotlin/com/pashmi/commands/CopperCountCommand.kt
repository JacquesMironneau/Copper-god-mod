package com.pashmi.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.pashmi.achievements.StateSaverAndLoader
import com.pashmi.utils.logger
import com.pashmi.utils.toMinecraftCopper
import com.pashmi.utils.toText
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class CopperCountCommand {

    companion object {
        private const val COPPER_COUNT = "coppercount"

        private val logger = logger()

        fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {

            val copperCount = dispatcher.register(CommandManager.literal(COPPER_COUNT)
                .executes { ctx ->
                    copperScoreBoard(ctx.source)
                })
            dispatcher.register(CommandManager.literal("csb").redirect(copperCount))
        }

        private fun copperScoreBoard(source: ServerCommandSource): Int {

            val styledPrefix = "Copper scoreboard".toMinecraftCopper()
            val playerCopper = StateSaverAndLoader.getPlayerCopper(source.server)

            val top5 = playerCopper.values
                .filter { it.playerName.isNotEmpty() }
                .map { Pair(it.playerName, it.copperOreBroken) }
                .sortedBy { it.second }
                .reversed()
                .take(5)
                .withIndex()
                .joinToString(
                    prefix = "${styledPrefix}\n\n",
                    separator = "\n"
                ) { (index, value) ->
                    if (index == 0) {
                        "§l1. ${value.first}: ${value.second}\$§r"
                    } else {
                        "${index + 1}. ${value.first}: ${value.second}"
                    }

                }

            source.player?.sendMessage(top5.toText())
            return Command.SINGLE_SUCCESS
        }
    }


}


