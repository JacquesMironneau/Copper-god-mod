package com.pashmi.commands


import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.pashmi.utils.logger
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.ChickenEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import kotlin.math.pow
import kotlin.math.sqrt


class LocateClosestCommand {

    companion object {

        private val logger = logger()

        fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {

            dispatcher.register(
                CommandManager.literal("to")

                    .then(CommandManager.argument("entity", EntityArgumentType.entities()).executes { ctx ->
                            val targetEntity = EntityArgumentType.getEntities(ctx, "entity")
                            locationTo(ctx.source, targetEntity)
                        })
            )
        }

        private fun processDistance(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double =
            sqrt(
                (x2 - x1).pow(2.0) + (y2 - y1).pow(2.0) + (z2 - z1).pow(2.0)
            )

        private fun processLivingEntitiesDistance(player: PlayerEntity, l: LivingEntity): Double =
            processDistance(player.x, player.y, player.z, l.x, l.y, l.z)


        private fun locationTo(source: ServerCommandSource, target: Collection<Entity>): Int {

            val player = source.player ?: return 0

            val closestEntity = target.first()
            val chickenComparator = Comparator { c1: ChickenEntity, c2: ChickenEntity ->
                processLivingEntitiesDistance(
                    player, c1
                ).toInt() - processLivingEntitiesDistance(player, c2).toInt()
            }
            val poulets = target.filterIsInstance<ChickenEntity>().sortedWith(chickenComparator)


            poulets.take(5).forEach {
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