package com.pashmi.items

import com.pashmi.CopperGodMod.MOD_ID
import com.pashmi.achievements.StateSaverAndLoader
import com.pashmi.utils.logger
import net.minecraft.entity.EntityType
import net.minecraft.entity.LightningEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.ToolItem
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.world.World
import kotlin.math.abs

private const val NBT_CHARGE_LEVEL = "${MOD_ID}_charge_level"
private const val ADVANCEMENT_PATH = "$MOD_ID/got_64_copper_ingot"
private const val WORTHINESS_LEVEL = 5

object CopperToolService {

    private val logger = logger()

    fun getCharge(stack: ItemStack): Int {
        val chargeLevel = stack.orCreateNbt.getInt(NBT_CHARGE_LEVEL)
        return chargeLevel
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

    fun setCharge(stack: ItemStack, charge: Int) {
        if (charge >= 0) stack.orCreateNbt.putInt(NBT_CHARGE_LEVEL, charge)
    }

    fun isWorthy(player: LivingEntity): Boolean {
        val copperOreBroken = StateSaverAndLoader.getPlayerState(player).copperOreBroken
        val hasMinedEnough = copperOreBroken >= WORTHINESS_LEVEL
        logger.info("${player.name} ${player is ServerPlayerEntity } has mined $copperOreBroken, it is ${if (hasMinedEnough) "enough" else "not enough, needs $WORTHINESS_LEVEL"}")

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


    fun decreaseCharge(stack: ItemStack) {
        setCharge(stack, getCharge(stack) - 1)
    }

    fun chargeItem(stack: ItemStack, maxCharge: Int, player: LivingEntity, world: World): Int {

        if (getCharge(stack) == maxCharge) return 0

        if (!isWorthy(player)) {
            logger.debug("Player is not worthy")
            return 0
        }

        val offhandItem = player.getStackInHand(Hand.OFF_HAND)

        if (offhandItem.item.isCopperBlock()) {
            val clamp = offhandItem.count.coerceIn(1, maxCharge)
            offhandItem.count -= abs(getCharge(stack) - clamp)
            val thunderEntity = LightningEntity(EntityType.LIGHTNING_BOLT, world)
                .apply { setPosition(player.pos) }

            if (world.spawnEntity(thunderEntity)) {
                setCharge(stack, clamp)
                val item = stack.item
                if (item is ToolItem) {
                    val repairAmount = item.material.durability
                    stack.damage -= (0.2 * repairAmount).toInt()
                }
                return clamp
            }
        } else {
            logger.debug("Player must have a copper block in offhand")
        }
        return 0
    }
}