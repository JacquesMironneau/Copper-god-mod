package com.pashmi.items

import com.pashmi.effects.CopperEffect
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects.HASTE
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class CopperHastyMiningToolService(
    private val copperToolService: CopperToolService
) {
    private val MAX_CHARGE = 10

    fun appendTooltip(
        stack: ItemStack,
        tooltip: MutableList<Text>,
    ) {

        val charge = copperToolService.getCharge(stack)
        when (charge) {
            0 -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.no-charge")
            in 1..5 -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.low-charge")
            in 5..<MAX_CHARGE -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.medium-charge")
            MAX_CHARGE -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.full-charge")
                .apply { style.withBold(true) }

            else -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.overcharge")
                .apply { style.withBold(true) }
        }.apply { style = if (style.isEmpty) copper_style_italics else style }
            .also { tooltip.add(it) }

        val secondText = Text.literal("Charges: $charge")
            .apply { style = Style.EMPTY.withColor(TextColor.fromRgb(6579300)).withItalic(true) }
        tooltip.add(secondText)
    }

    fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack: ItemStack = user.getStackInHand(hand)
        val res = copperToolService.chargeItem(itemStack, MAX_CHARGE, user, world)
        if (res == 0) {
            return TypedActionResult.pass(itemStack)
        } else {
            user.sendMessage(CopperGodMessages.getToolRefillMessage(res))
            return TypedActionResult.success(itemStack, true)
        }
    }


    fun postMine(
        stack: ItemStack,
        state: BlockState,
        miner: LivingEntity,
        isRightMaterial: (Block) -> Boolean
    ): Boolean {

        val block = state.block

        if (!isRightMaterial(block)) return true

        if (!copperToolService.isWorthy(miner)) return true
        if (copperToolService.getCharge(stack) == 0) return true

        if (miner.hasStatusEffect(HASTE)) return true

        var amplifier = 1
        if (miner.hasStatusEffect(CopperEffect.COPPERIZED)) amplifier = 2

        val hasteBonus = StatusEffectInstance(HASTE, 200 * amplifier, amplifier)
        miner.addStatusEffect(hasteBonus)
        val txt = "${Text.translatable("pashmi-copper-god.copper-god-message.consuming_stack").string}§6${Text.translatable(stack.translationKey).string}"
        copperToolService.decreaseCharge(stack)
            .also { miner.sendMessage(Text.literal(txt)) }
        return true
    }

    fun getNameSuffix(itemStack: ItemStack): String {
        val charge = copperToolService.getCharge(itemStack)
        return if (charge == 0) "" else "($charge)"
    }
}