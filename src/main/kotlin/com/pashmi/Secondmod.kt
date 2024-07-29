package com.pashmi

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.pashmi.HomeCommand
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.item.ItemGroups
import org.slf4j.LoggerFactory
import CustomItems

object Secondmod : ModInitializer {
    private val logger = LoggerFactory.getLogger("second-mod")

    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        logger.info("Hello Fabric world!")
        logger.info("salut")

        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            dispatcher.register(
                    CommandManager.literal("pongy").executes { context ->
                        context.getSource().sendFeedback({ Text.literal("hello pongy uwu") }, true)
                        1
                    }
            )
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            TeleportCommand.register(dispatcher)
            HomeCommand.register(dispatcher)
            EnderChestCommand.register(dispatcher)
            LocateClosestCommand.register(dispatcher)
        }

        CustomItems.initialize()

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
        .register { it.add(CustomItems.COPPER_SWORD) }
        

        // CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
        //     ModCommand(dispatcher).add("pongo").add("ponga").add("pongou")
        // }
    }
}

// class ModCommand(val dispatcher: CommandDispatcher<ServerCommandSource>) {
//     private val logger = LoggerFactory.getLogger("${ModCommand::class}")

//     fun add(commandName: String): ModCommand {
// 		add<CommandContext<ServerCommandSource!>!, Int!>(commandName, { _ ->
// 			logger.info("Executing $commandName")
// 			Command.SINGLE_SUCCESS
// 		})
//         return this
//     }

// 		fun <T,R> add(
// 				commandName: String,
// 				executor: (command: R) -> T
// 		): ModCommand {
// 			dispatcher.register(
// 				CommandManager.literal(commandName).executes(executor))
// 			return this
// 		}
// }

class TeleportCommand {

    companion object {
        val logger = LoggerFactory.getLogger("${TeleportCommand::class}")

        fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
            dispatcher.register(
                    CommandManager.literal("tpall").executes { ctx -> teleportAll(ctx.getSource()) }
            )

			dispatcher.register(
				CommandManager.literal("feed")
                .executes { ctx -> feedMe(ctx.getSource())}
                    .then(CommandManager.argument("entity", EntityArgumentType.entity())
                    .then(CommandManager.argument("foodAmount", IntegerArgumentType.integer())
                        .executes { ctx -> 
                            val entity = EntityArgumentType.getEntity(ctx, "entity")
                            val foodAmount = IntegerArgumentType.getInteger(ctx, "foodAmount")
                            feedMe(ctx.getSource(), entity, foodAmount)
                        }))
			)
			
			dispatcher.register(
				CommandManager.literal("unfeed").executes { ctx -> unfeedMe(ctx.getSource())}
			)
        
        }

		fun feedMe(source: ServerCommandSource, entity: Entity, foodAmount: Int = 20): Int {

                if (entity is PlayerEntity) {
                    feed(entity)
                logger.info("$entity is a player!")

                } else {

                    val playerEntity = source.getPlayer()
                    if (playerEntity == null) return 0
        
                    
                    feed(playerEntity)
                    
                }

			return Command.SINGLE_SUCCESS
			
		}

		fun feedMe(source: ServerCommandSource): Int {
			
            val playerEntity = source.getPlayer()

            if (playerEntity == null) return 0
			feed(playerEntity)
			return Command.SINGLE_SUCCESS
		}

		private fun feed(player: PlayerEntity, food: Int = 20, saturation : Float = 20f) {

            player.sendMessage(Text.literal("You have been fed"))
			player.getHungerManager().add(food, saturation)
		}

		fun unfeedMe(source: ServerCommandSource): Int {
            val playerEntity = source.getPlayer()

            if (playerEntity == null) return 0
			feed(playerEntity, -20, 1f)

			return Command.SINGLE_SUCCESS
		}

        fun teleportAll(source: ServerCommandSource): Int {

            val players = source.getServer().getPlayerManager().getPlayerList()

            val playerEntity = source.getPlayer()

            if (playerEntity == null) return 0

            val text = Text.literal("teleporting everyone to ${playerEntity.getGameProfile().getName()}")

            // playerEntity!!.requestTeleportOffset(0.0, 30.0, 0.0)

            logger.info("players are $players")
            players
                    .filter { player ->
                        (player.getGameProfile().getName() !=
                                        playerEntity.getGameProfile()?.getName())
                                .also { isNotSource ->
                                    logger.info(
                                            if (isNotSource) player.getGameProfile().getName()
                                            else "c mario"
                                    )
                                }
                    }
                    .forEach {
                        it.requestTeleport(
                                playerEntity.getX(),
                                playerEntity.getY(),
                                playerEntity.getZ()
                        )
                    }
            source.sendMessage(text)
            return Command.SINGLE_SUCCESS
        }
    }
}
