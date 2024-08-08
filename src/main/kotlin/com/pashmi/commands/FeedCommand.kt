package com.pashmi.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.pashmi.utils.logger
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text


class FeedCommand {

    companion object {
        val logger = logger()
        private const val FEED = "feed"
        private const val ENTITY = "entity"
        private const val FOOD_AMOUNT = "foodAmount"

        fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
            dispatcher.register(
                CommandManager.literal(FEED).executes { ctx -> feedMe(ctx.source) }.then(
                    CommandManager.argument(ENTITY, EntityArgumentType.entity())
                        .executes { ctx ->
                            val entity = EntityArgumentType.getEntity(ctx, ENTITY)
                            feedMe(ctx.source, entity)
                        }
                        .then(CommandManager.argument(FOOD_AMOUNT, IntegerArgumentType.integer()).executes { ctx ->
                            val entity = EntityArgumentType.getEntity(ctx, ENTITY)
                            val foodAmount = IntegerArgumentType.getInteger(ctx, FOOD_AMOUNT)
                            feedMe(ctx.source, entity, foodAmount)
                        })
                )
            )

            dispatcher.register(CommandManager.literal("unfeed").executes { ctx -> unfedMe(ctx.source) })
        }

        private fun feedMe(source: ServerCommandSource, entity: Entity, foodAmount: Int = 20): Int {

            if (entity is PlayerEntity) {
                feed(entity)
                logger.info("$entity is a player!")

            } else {
                val playerEntity = source.player ?: return 0
                feed(playerEntity, foodAmount)
            }
            return Command.SINGLE_SUCCESS
        }

        private fun feedMe(source: ServerCommandSource): Int {

            val playerEntity = source.player ?: return 0
            feed(playerEntity)
            return Command.SINGLE_SUCCESS
        }

        private fun feed(player: PlayerEntity, food: Int = 20, saturation: Float = 20f) {
            with(player) {
                sendMessage(Text.literal("You have been fed"))
                hungerManager.add(food, saturation)
            }
        }

        private fun unfedMe(source: ServerCommandSource): Int {
            val playerEntity = source.player ?: return 0
            feed(playerEntity, -20, 1f)
            return Command.SINGLE_SUCCESS
        }
    }

}