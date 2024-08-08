package com.pashmi.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.pashmi.utils.logger
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

private const val TPALL = "tpall"


class TeleportCommand {

    companion object {
        private val logger = logger()

        fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
            dispatcher.register(CommandManager.literal(TPALL).executes { ctx -> teleportAll(ctx.source) })
        }

        private fun teleportAll(source: ServerCommandSource): Int {

            val players = source.server.playerManager.playerList
            val playerEntity = source.player ?: return 0
            val text = Text.literal("teleporting everyone to ${playerEntity.gameProfile.name}")

            logger.info("players are $players")
            players.filter { player ->
                (player.gameProfile.name != playerEntity.gameProfile?.name).also { isNotSource ->
                    logger.info(
                        if (isNotSource) player.gameProfile.name
                        else "c mario"
                    )
                }
            }.forEach {
                it.requestTeleport(playerEntity.x, playerEntity.y, playerEntity.z)
            }
            source.sendMessage(text)
            return Command.SINGLE_SUCCESS
        }
    }
}