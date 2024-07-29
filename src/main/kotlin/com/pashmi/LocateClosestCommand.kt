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
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.ChickenEntity
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.ScreenHandler
import org.slf4j.LoggerFactory


class LocateClosestCommand {

    companion object {

        private val logger = LoggerFactory.getLogger("${LocateClosestCommand::class}")

        fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {

            dispatcher.register(
                CommandManager.literal("to")
                
                .then(CommandManager.argument("entity", EntityArgumentType.entities())
                .executes { ctx -> 
                    val targetEntity = EntityArgumentType.getEntities(ctx, "entity")
                    locationTo(ctx.getSource(), targetEntity)
                }))
        }

        private fun processDistance(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double = Math.sqrt(Math.pow(x2 - x1, 2.0) + Math.pow(y2 - y1, 2.0) + Math.pow(z2 - z1, 2.0))

        private fun processLivingEntitiesDistance(player: PlayerEntity, l: LivingEntity ): Double = processDistance(player.getX(), player.getY(), player.getZ(), l.getX(), l.getY(), l.getZ())
        

        fun locationTo(source: ServerCommandSource, target: Collection<Entity>): Int {

            val player = source.getPlayer()
            if (player == null) return 0

            val closestEntity = target.first()


            val chickenComparator = Comparator { c1: ChickenEntity, c2: ChickenEntity -> processLivingEntitiesDistance(player, c1).toInt() - processLivingEntitiesDistance(player, c2).toInt()}
            val poulets = target.filterIsInstance<ChickenEntity>()
            .sortedWith(chickenComparator)
            

            poulets.take(5)
            .forEach {
                it.customName = Text.literal("poulayyy")
            }
            
            val firstPoulet = poulets.first()

            val lastPoulet = poulets.last()

            val distance = processLivingEntitiesDistance(player, firstPoulet)
            source.sendMessage(Text.literal("$firstPoulet : ${distance.toInt()} blocks away"))
            val distanceLast = processLivingEntitiesDistance(player, lastPoulet)

            source.sendMessage(Text.literal("$lastPoulet : ${distanceLast.toInt()} blocks away"))

            logger.info("targetted: $poulets")

            

            // if (closestEntity is PlayerEntity) {
                
            //     val distance = processDistance(player.getX(), player.getY(), player.getZ(), closestEntity.getX(), closestEntity.getY(), closestEntity.getZ())
            //     logger.info("$closestEntity is $distance blocks away")
            // } else if (closestEntity is ChickenEntity) {
                
            //     val distance = processDistance(player.getX(), player.getY(), player.getZ(), closestEntity.getX(), closestEntity.getY(), closestEntity.getZ())
            //     logger.info("$closestEntity is $distance blocks away")
            // }
            // else {
            //     logger.info("$player tried to locate a non player entity")
            //     return 0
            // }


            return Command.SINGLE_SUCCESS
        }
    }
}