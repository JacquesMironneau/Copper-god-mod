package com.pashmi

import com.mojang.brigadier.builder.LiteralArgumentBuilder


import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.ScreenHandler
import org.slf4j.LoggerFactory


class EnderChestCommand {

    companion object {

        private val logger = LoggerFactory.getLogger("${EnderChestCommand::class}")

        fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {

            dispatcher.register(
                CommandManager.literal("ec")
                .executes { ctx -> 
                    enderchest(ctx.getSource())
                })
        }

        fun enderchest(source: ServerCommandSource): Int {

            val player = source.getPlayer()
            if (player == null) return 0

            logger.info("Player will set home: $player")
            player.openHandledScreen(object: NamedScreenHandlerFactory {
                override fun getDisplayName(): Text = Text.literal("feur")
                override fun createMenu(syncId: Int, inv: PlayerInventory, player:  PlayerEntity): ScreenHandler = GenericContainerScreenHandler.createGeneric9x3(syncId, inv, player.getEnderChestInventory())       
            })

            return Command.SINGLE_SUCCESS
        }
    }
}