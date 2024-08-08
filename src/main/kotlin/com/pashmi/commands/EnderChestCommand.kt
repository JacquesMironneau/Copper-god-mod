package com.pashmi.commands


import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.pashmi.utils.logger
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text


private const val ENDERCHEST = "ec"

class EnderChestCommand {

    companion object {

        private val logger = logger()

        fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {

            dispatcher.register(CommandManager.literal(ENDERCHEST).executes { ctx ->
                enderchest(ctx.source)
            })
        }

        private fun enderchest(source: ServerCommandSource): Int {

            val player = source.player ?: return 0
            player.openHandledScreen(object : NamedScreenHandlerFactory {
                override fun getDisplayName(): Text = Text.literal("Yes it is your enderchest")
                override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler =
                    GenericContainerScreenHandler.createGeneric9x3(syncId, inv, player.enderChestInventory)
            })

            return Command.SINGLE_SUCCESS
        }
    }
}