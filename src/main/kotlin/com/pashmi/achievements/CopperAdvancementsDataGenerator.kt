package com.pashmi.achievements

import com.pashmi.CopperGodMod.MOD_ID
import com.pashmi.CopperGodMod.toModId
import com.pashmi.items.CopperItems
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.advancement.criterion.RecipeCraftedCriterion
import net.minecraft.item.Items
import net.minecraft.predicate.NumberRange
import net.minecraft.predicate.item.ItemPredicate
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.function.Consumer


class CopperAdvancementsDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        val pack = generator.createPack()

        pack.addProvider { dataGenerator: FabricDataOutput -> AdvancementsProvider(dataGenerator) }
    }

    class AdvancementsProvider(dataGenerator: FabricDataOutput) : FabricAdvancementProvider(dataGenerator) {
        override fun generateAdvancement(consumer: Consumer<AdvancementEntry>) {
            val oneCopperAdvancement = Advancement.Builder.create()
                .display(
                    Items.RAW_COPPER,
                    Text.translatable("advancement.pashmi-copper-god.got-copper.title"),
                    Text.translatable("advancement.pashmi-copper-god.got-copper.description"),
                    Identifier("textures/gui/advancements/backgrounds/adventure.png"), // Background image used
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
                ).criterion(
                    "got_copper", InventoryChangedCriterion.Conditions.items(Items.RAW_COPPER)
                )
                .rewards(
                    AdvancementRewards.Builder
                        .experience(50)
                        .addRecipe("copper_sword".toModId())
                        .addRecipe("copper_pickaxe".toModId())
                        .addRecipe("copper_axe".toModId())
                        .addRecipe("copper_shovel".toModId())
                        .addRecipe("copper_hoe".toModId())
                        .addRecipe("cu_diamond".toModId())
                        .build()
                )
                .build(consumer, "$MOD_ID/root")


            val oneStackOfCopperAdvancement = Advancement.Builder.create()
                .display(
                    Items.COPPER_INGOT,
                    Text.translatable("advancement.pashmi-copper-god.got-copper-stack.title"),
                    Text.translatable("advancement.pashmi-copper-god.got-copper-stack.description"),
                    Identifier("textures/gui/advancements/backgrounds/adventure.png"), // Background image used
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
                )
                .criterion(
                    "got_copper_stack", InventoryChangedCriterion.Conditions.items(
                        ItemPredicate.Builder.create()
                            .items(Items.COPPER_INGOT)
                            .count(NumberRange.IntRange.atLeast(64))
                            .build()
                    )
                )
                .rewards(
                    AdvancementRewards.Builder
                        .experience(100).build()
                )
                .parent(oneCopperAdvancement)
                .build(consumer, "$MOD_ID/got_64_copper_ingot")

            val oneStackOfCopperBlockAdvancement = Advancement.Builder.create()
                .display(
                    Items.COPPER_BLOCK,
                    Text.translatable("advancement.pashmi-copper-god.got-copper-block-stack.title"),
                    Text.translatable("advancement.pashmi-copper-god.got-copper-block-stack.description"),
                    Identifier("textures/gui/advancements/backgrounds/adventure.png"), // Background image used
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
                )
                .criterion(
                    "got_64_copper_block", InventoryChangedCriterion.Conditions.items(
                        ItemPredicate.Builder.create()
                            .items(Items.COPPER_BLOCK)
                            .count(NumberRange.IntRange.atLeast(64))
                            .build()
                    )
                )
                .rewards(
                    AdvancementRewards.Builder
                        .loot(Identifier("minecraft", "copper_ingot"))
                )
                .parent(oneStackOfCopperAdvancement)
                .build(consumer, "$MOD_ID/got_64_copper_block")


            val swordAdvancement = Advancement.Builder.create()
                .display(
                    CopperItems.copper_sword,
                    Text.translatable("advancement.pashmi-copper-god.got-copper-sword.title"),
                    Text.translatable("advancement.pashmi-copper-god.got-copper-sword.description"),
                    Identifier("textures/gui/advancements/backgrounds/adventure.png"), // Background image used
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
                ).criterion(
                    "craft_copper_sword",
                    RecipeCraftedCriterion.Conditions.create("copper_sword".toModId())
                )
                .parent(oneCopperAdvancement)
                .build(consumer, "$MOD_ID/copper_sword")

            val pickaxeAdvancement = Advancement.Builder.create()
                .display(
                    CopperItems.copper_pickaxe,
                    Text.translatable("advancement.pashmi-copper-god.got-copper-pickaxe.title"),
                    Text.translatable("advancement.pashmi-copper-god.got-copper-pickaxe.description"),
                    Identifier("textures/gui/advancements/backgrounds/adventure.png"), // Background image used
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
                ).criterion(
                    "craft_copper_pickaxe",
                    RecipeCraftedCriterion.Conditions.create("copper_pickaxe".toModId())
                )
                .parent(oneCopperAdvancement)
                .build(consumer, "$MOD_ID/copper_pickaxe")

        }
    }
}
