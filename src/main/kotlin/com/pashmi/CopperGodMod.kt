package com.pashmi

import com.pashmi.CopperGodMod.MOD_ID
import com.pashmi.achievements.CopperOreBreakCounter
import com.pashmi.annotations.AutoRegisterClass
import com.pashmi.annotations.RegisterManager
import com.pashmi.blocks.CopperGodBlocks
import com.pashmi.commands.*
import com.pashmi.effects.CopperEffect
import com.pashmi.items.CopperItems
import com.pashmi.items.CopperMaterial
import com.pashmi.utils.logger
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries
import net.minecraft.item.ItemGroups
import net.minecraft.item.Items
import net.minecraft.server.command.CommandManager
import net.minecraft.text.Text
import net.minecraft.util.Identifier

@AutoRegisterClass(MOD_ID)
object CopperGodMod : ModInitializer {
    private val logger = logger()

    const val MOD_ID = "pashmi-copper-god"

    override fun onInitialize() {
        logger.info("Hello this is $MOD_ID !")

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(CommandManager.literal("pong").executes { context ->
                context.source.sendFeedback({ Text.literal("pong pong") }, true)
                1
            })
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            CopperCountCommand.register(dispatcher)
        }

        RegisterManager.processAutoRegisterItems(listOf(CopperItems::class, CopperGodMod::class, CopperEffect::class, CopperGodBlocks::class))

        CopperMaterial.initialize()

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
            .register { it.addAfter(Items.IRON_SWORD, CopperItems.copper_sword) }

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
            .register { it.addAfter(Items.IRON_HOE, CopperItems.copper_pickaxe, CopperItems.copper_axe, CopperItems.copper_shovel, CopperItems.copper_hoe)}


        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS)
            .register(ModifyEntries { it.addAfter(Items.COPPER_BLOCK, CopperGodBlocks.cu_diamond) })
        CopperOreBreakCounter.initializeCopperOreCounter()

    }

    fun String.toModId(modId: String = MOD_ID): Identifier? = Identifier.of(modId, this)
}

