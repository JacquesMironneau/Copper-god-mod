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
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.command.argument.EntityArgumentType
import org.slf4j.LoggerFactory


data class Pos(val x: Double,val y: Double, val z: Double)

class HomeCommand {

    companion object {

        private val logger = LoggerFactory.getLogger("${HomeCommand::class}")

        var currentPos: Pos = Pos(0.0, 0.0, 0.0)
        fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {

            dispatcher.register(
                CommandManager.literal("sethome")
                .executes { ctx -> 
                    setHome(ctx.getSource())
                })

            
                dispatcher.register(
                    CommandManager.literal("home")
                    .executes { ctx -> 
                        home(ctx.getSource())
                    })

            // setHomeLiteral.executes {
            //     ctx -> 
                
            //     logger.info("executing sethome")
            //     setHome(ctx.getSource())
            // }.then(CommandManager.argument("homeName", StringArgumentType.string()))
            // .executes { ctx ->
            //     val homeName = StringArgumentType.getString(ctx, "homeName")
            //     logger.info("executing sethome $homeName")

            //     setHome(ctx.getSource(), homeName)
            // }

            // val homeLiteral = CommandManager.literal("home")
            // homeLiteral.executes {
            //     ctx -> home(ctx.getSource())
            // }.bui

            // dispatcher.register(setHomeLiteral)
            // dispatcher.register(homeLiteral)

        }

        fun setHome(source: ServerCommandSource): Int {

            val player = source.getPlayer()
            if (player == null) return 0

            logger.info("Player will set home: $player, current pso is $currentPos")

            currentPos = Pos(player.getX(), player.getY(), player.getZ())


            return Command.SINGLE_SUCCESS
        }

        fun home(source: ServerCommandSource): Int {
            val player = source.getPlayer()
            if (player == null) return 0

            player.requestTeleport(currentPos.x, currentPos.y, currentPos.z)
            return Command.SINGLE_SUCCESS
        }


        fun setHome(source: ServerCommandSource, homeName: String): Int {

            return Command.SINGLE_SUCCESS

        }

            

    }
}