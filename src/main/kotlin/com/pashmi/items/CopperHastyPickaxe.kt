package com.pashmi.items

import com.pashmi.items.CopperGodMessages.Companion.copper_style_italics
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects.HASTE
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.PickaxeItem
import net.minecraft.item.ToolMaterial
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CopperHastyPickaxe(
    material: ToolMaterial?,
    attackDamage: Int,
    attackSpeed: Float,
    settings: Settings?,
    private val copperToolService: CopperToolService
) :
    PickaxeItem(material, attackDamage, attackSpeed, settings) {

    private val MAX_CHARGE = 10

    override fun appendTooltip(
        stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext
    ) {


        val charge = copperToolService.getCharge(stack)
        when (charge) {
            0 -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.no-charge")
            in 1..5 -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.low-charge")
            in 5..<MAX_CHARGE -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.medium-charge")
            MAX_CHARGE -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.full-charge")
                .apply { style.withBold(true) }

            else -> Text.literal("feuuuuuuuur")
        }.apply { style = if (style.isEmpty) copper_style_italics else style }
            .also { tooltip.add(it) }

        val secondText = Text.literal("Charges: $charge")
            .apply { style = Style.EMPTY.withColor(TextColor.fromRgb(6579300)).withItalic(true) }
        tooltip.add(secondText)
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        super.use(world, user, hand)
        val itemStack: ItemStack = user.getStackInHand(hand)
        val res = copperToolService.chargeItem(itemStack, MAX_CHARGE, user, world)
        if (res == 0) {
            return TypedActionResult.pass(itemStack)
        } else {
            user.sendMessage(CopperGodMessages.getToolRefillMessage(res))
            return TypedActionResult.success(itemStack, true)
        }
    }

    override fun postMine(
        stack: ItemStack,
        world: World,
        state: BlockState,
        pos: BlockPos,
        miner: LivingEntity
    ): Boolean {
        super.postMine(stack, world, state, pos, miner)

        val block = state.block

        if (!block.isOre()) return true

        if (!copperToolService.isWorthy(miner)) return true
        if (copperToolService.getCharge(stack) == 0) return true

        if (miner.hasStatusEffect(HASTE)) return true

        val hasteBonus = StatusEffectInstance(HASTE, 700, 1)
        miner.addStatusEffect(hasteBonus)
        copperToolService.decreaseCharge(stack)

        return true
    }

    private fun Block.isOre() =
        this in listOf(Blocks.COPPER_ORE, Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.RAW_IRON_BLOCK, Blocks.GOLD_ORE)
}