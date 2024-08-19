package com.pashmi.items

import com.pashmi.utils.isSandOrClay
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ShovelItem
import net.minecraft.item.ToolMaterial
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CopperHastyShovel(
    material: ToolMaterial,
    attackDamage: Float,
    attackSpeed: Float,
    settings: Settings,
    private val service: CopperHastyMiningToolService,
) : ShovelItem(material, attackDamage, attackSpeed, settings) {

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        service.appendTooltip(stack, tooltip)
    }

    override fun getName(stack: ItemStack): Text {
        val txt =  super.getName(stack).string
        return Text.literal("$txt ${service.getNameSuffix(stack)}")
    }

    override fun postMine(
        stack: ItemStack,
        world: World?,
        state: BlockState,
        pos: BlockPos,
        miner: LivingEntity
    ): Boolean {
        super.postMine(stack, world, state, pos, miner)
        return service.postMine(stack, state, miner, Block::isSandOrClay)
    }
}

