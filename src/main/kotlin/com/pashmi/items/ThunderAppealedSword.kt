package com.pashmi.items

import com.pashmi.CopperGodMod.MOD_ID
import com.pashmi.achievements.StateSaverAndLoader
import com.pashmi.items.CopperGodMessages.Companion.copper_style_italics
import com.pashmi.utils.logger
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.EntityType
import net.minecraft.entity.LightningEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import kotlin.random.Random


class ThunderAppealedSword(
    toolMaterial: ToolMaterial, attackDamage: Int, attackSpeed: Float, settings: Settings
) : SwordItem(toolMaterial, attackDamage, attackSpeed, settings) {

    companion object {
        private const val ADVANCEMENT_PATH = "$MOD_ID/got_64_copper_ingot"
        private const val NBT_CHARGE_LEVEL = "${MOD_ID}_charge_level"
        private const val WORTHINESS_LEVEL = 5
        private const val MAX_CHARGE = 15
    }

    private val logger = logger()

    override fun appendTooltip(
        stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext
    ) {

        var style = copper_style_italics
        val charge = getCharge(stack)
        val txt = when (charge) {
            0 -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.no-charge")
            in 1..5 -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.low-charge")
            in 5..9 -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.medium-charge")
            MAX_CHARGE -> Text.translatable("tooltip.pashmi-copper-god.copper-sword.full-charge").also { style = style.withBold(true) }
            else -> Text.literal("feuuuuuuuur")
        }

        txt.style = style
        tooltip.add(txt)
        val secondText = Text.literal("Charges: $charge")
        secondText.style = Style.EMPTY.withColor(TextColor.fromRgb(6579300)).withItalic(true)
        tooltip.add(secondText)

    }

    private fun isWorthy(player: LivingEntity): Boolean {
        val copperOreBroken = StateSaverAndLoader.getPlayerState(player).copperOreBroken
        val hasMinedEnough = copperOreBroken >= WORTHINESS_LEVEL
        logger.info("${player.name} has mined $copperOreBroken, it is ${if (hasMinedEnough) "enough" else "not enough, needs $WORTHINESS_LEVEL"}")

        if (player is ServerPlayerEntity) {
            val advancements = player.server.advancementLoader.advancements
            val advancementEntry = advancements.find { it.id.path == ADVANCEMENT_PATH }
            if (advancementEntry == null) return hasMinedEnough

            val done = player.advancementTracker.getProgress(advancementEntry).isDone
            if (!done) {
                logger.info("${player.name} has not finished ${advancementEntry.id}")
                return false
            }
        }
        return hasMinedEnough
    }


    override fun postHit(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean {
        super.postHit(stack, target, attacker)

        if (!isWorthy(attacker)) return true
        if (getCharge(stack) == 0) return true
        if (Random.nextInt(4) != 1) return true

        val world = attacker.world
        val thunderEntity = LightningEntity(EntityType.LIGHTNING_BOLT, world)
        thunderEntity.setPosition(target.pos)
        if (world.spawnEntity(thunderEntity)) {
            decreaseCharge(stack)
            if (attacker is ServerPlayerEntity) {
                attacker.sendMessage(CopperGodMessages.getRandomAllyMessage())
            }
        }
        return true
    }


    private fun decreaseCharge(stack: ItemStack) {
        setCharge(stack, getCharge(stack) - 1)
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        super.use(world, user, hand)

        val itemStack: ItemStack = user.getStackInHand(hand)

        if (getCharge(itemStack) == MAX_CHARGE) return TypedActionResult.pass(itemStack)

        if (!isWorthy(user)) return TypedActionResult.pass(itemStack)

        val offhandItem = user.getStackInHand(Hand.OFF_HAND)

        if (offhandItem.item.isCopperBlock()) {
            val clamp = offhandItem.count.coerceIn(1, MAX_CHARGE)
            offhandItem.count -= getCharge(itemStack) - clamp
            val thunderEntity = LightningEntity(EntityType.LIGHTNING_BOLT, world)
            thunderEntity.setPosition(user.pos)
            if (world.spawnEntity(thunderEntity)) {
                user.sendMessage(CopperGodMessages.getRefillMessage(clamp))

                setCharge(itemStack, clamp)

                return TypedActionResult.success(itemStack, true)
            }
        }
        return TypedActionResult.pass(itemStack)
    }

    private fun getCharge(stack: ItemStack): Int {
        val chargeLevel = stack.orCreateNbt.getInt(NBT_CHARGE_LEVEL)
        return chargeLevel
    }

    private fun Item.isCopperBlock(): Boolean {
        return this in listOf(
            Items.COPPER_BLOCK,
            Items.CUT_COPPER,
            Items.EXPOSED_COPPER,
            Items.EXPOSED_CUT_COPPER,
            Items.WAXED_COPPER_BLOCK,
            Items.WAXED_CUT_COPPER
        )
    }

    private fun setCharge(stack: ItemStack, charge: Int) {
        if (charge >= 0) stack.orCreateNbt.putInt(NBT_CHARGE_LEVEL, charge)
    }
}