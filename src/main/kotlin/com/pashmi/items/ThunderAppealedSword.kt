package com.pashmi.items

import com.pashmi.items.CopperGodMessages.Companion.copper_style_italics
import com.pashmi.utils.logger
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.EntityType
import net.minecraft.entity.LightningEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolMaterial
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import kotlin.random.Random


class ThunderAppealedSword(
    toolMaterial: ToolMaterial,
    attackDamage: Int,
    attackSpeed: Float,
    settings: Settings,
    private val copperToolService: CopperToolService
) : SwordItem(toolMaterial, attackDamage, attackSpeed, settings) {

    companion object {
        private const val MAX_CHARGE = 15
    }

    private val logger = logger()

    override fun appendTooltip(
        stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext
    ) {

        var style = copper_style_italics
        val charge = copperToolService.getCharge(stack)
        val txt = when (charge) {
            0 -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.no-charge")
            in 1..5 -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.low-charge")
            in 5..<MAX_CHARGE -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.medium-charge")
            MAX_CHARGE -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.full-charge")
                .also { style = style.withBold(true) }

            else -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.overcharge")
                .also { style = style.withBold(true) }
        }

        txt.style = style
        tooltip.add(txt)
        val secondText = Text.literal("Charges: $charge")
        secondText.style = Style.EMPTY.withColor(TextColor.fromRgb(6579300)).withItalic(true)
        tooltip.add(secondText)

    }


    override fun postHit(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean {
        super.postHit(stack, target, attacker)

        if (!copperToolService.isWorthy(attacker)) return true
        if (copperToolService.getCharge(stack) == 0) return true
        if (Random.nextInt(4) != 1) return true

        val world = attacker.world
        val thunderEntity = LightningEntity(EntityType.LIGHTNING_BOLT, world)
        thunderEntity.setPosition(target.pos)
        if (world.spawnEntity(thunderEntity)) {
            copperToolService.decreaseCharge(stack)
            if (attacker is ServerPlayerEntity) {
                attacker.sendMessage(CopperGodMessages.getRandomAllyMessage())
            }
        }
        return true
    }


    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        super.use(world, user, hand)

        val itemStack: ItemStack = user.getStackInHand(hand)
        val res = copperToolService.chargeItem(itemStack, MAX_CHARGE, user, world)
        if (res == 0) {
            return TypedActionResult.pass(itemStack)
        } else {
            user.sendMessage(CopperGodMessages.getRefillMessage(res))
            return TypedActionResult.success(itemStack, true)
        }
    }


}