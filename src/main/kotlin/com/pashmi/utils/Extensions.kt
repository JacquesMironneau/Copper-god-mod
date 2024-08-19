package com.pashmi.utils

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.ToolItem
import net.minecraft.text.Text

fun String.toMinecraftRainbow(): String {
    val rainbow = "c6ea395".toCharArray()
    var colorIndex = 0

    return this.map { char ->
        val colorCode = rainbow[colorIndex % rainbow.size]
        colorIndex++
        "§$colorCode$char"
    }.joinToString("", postfix = "§r§f")
}

fun String.toMinecraftCopper(bold: Boolean = true): String = if (bold) "§l§6$this$§r§f" else "§6$this$§r§f"

fun String.toText(): Text = Text.literal(this)
fun repairItem(stack: ItemStack, item: ToolItem, percent: Float) {
    val repairAmount = item.material.durability
    stack.damage -= (percent * repairAmount).toInt()
}

fun Item.isCopperBlock(): Boolean {
    return this in listOf(
        Items.COPPER_BLOCK,
        Items.CUT_COPPER,
        Items.EXPOSED_COPPER,
        Items.EXPOSED_CUT_COPPER,
        Items.WAXED_COPPER_BLOCK,
        Items.WAXED_CUT_COPPER
    )
}

fun Item.isCopperItem() = this in listOf(Items.COPPER_INGOT, Items.RAW_COPPER)

fun Block.isOre() =
    this in listOf(Blocks.COPPER_ORE, Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.RAW_IRON_BLOCK, Blocks.GOLD_ORE)

fun Block.isMelonOrPumpkinOrLog() =
    this in listOf(
        Blocks.MELON, Blocks.PUMPKIN,
        Blocks.OAK_LOG, Blocks.BIRCH_LOG, Blocks.DARK_OAK_LOG,
        Blocks.ACACIA_LOG, Blocks.JUNGLE_LOG, Blocks.SPRUCE_LOG, Blocks.MANGROVE_LOG,
        Blocks.CHERRY_LOG
    )

fun Block.isSandOrClay() =
    this in listOf(Blocks.SAND, Blocks.RED_SAND, Blocks.SOUL_SAND, Blocks.CLAY)