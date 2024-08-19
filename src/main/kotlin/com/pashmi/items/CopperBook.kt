package com.pashmi.items

import com.pashmi.achievements.CopperOreBreakCounter.Companion.OPEN_CUSTOM_BOOK_SCREEN
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.WrittenBookItem
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World


class CopperBook(settings: Settings) : WrittenBookItem(settings) {

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (world.isClient) {
            super.use(world, user, hand)
            return TypedActionResult.success(user.getStackInHand(hand))
        }

        if (user is ServerPlayerEntity) {
            val data = PacketByteBufs.create().apply {
                writeItemStack(user.getStackInHand(hand))
            }
            ServerPlayNetworking.send(user, OPEN_CUSTOM_BOOK_SCREEN, data)
        }
        return TypedActionResult.success(user.getStackInHand(hand))
    }
}