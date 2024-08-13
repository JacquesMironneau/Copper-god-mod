package com.pashmi.blocks

import com.pashmi.blocks.CopperDiamondBlock.Companion.ORATORY_LISTENING
import com.pashmi.effects.CopperEffect.Companion.COPPERIZED
import com.pashmi.items.CopperGodMessages
import com.pashmi.items.CopperGodMessages.Companion.getMoreCopper
import com.pashmi.items.CopperGodMessages.Companion.getOratoryCreationMessage
import com.pashmi.items.CopperGodMessages.Companion.getRefillMessage
import com.pashmi.items.CopperGodMessages.Companion.needCopper
import com.pashmi.items.CopperItems.Companion.isCopperItem
import com.pashmi.items.CopperToolService
import com.pashmi.items.CopperToolService.setCharge
import net.minecraft.block.Block
import net.minecraft.block.Block.dropStack
import net.minecraft.block.Block.getRawIdFromState
import net.minecraft.block.Blocks
import net.minecraft.block.pattern.BlockPattern
import net.minecraft.block.pattern.BlockPatternBuilder
import net.minecraft.block.pattern.CachedBlockPosition
import net.minecraft.entity.EntityType
import net.minecraft.entity.LightningEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.predicate.block.BlockStatePredicate
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import java.util.function.Predicate

class CopperGodStructureService {

    companion object {

        fun getCopperGodOratory(): BlockPattern {
            return BlockPatternBuilder.start()
                .aisle("~^~", "###", "~#~")
                .where('^', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.LIGHTNING_ROD)))
                .where('#', CachedBlockPosition.matchesBlockState(
                        BlockStatePredicate.forBlock(CopperGodBlocks.cu_diamond).with(
                            ORATORY_LISTENING, Predicate.isEqual(false)
                        )
                    )
                )
                .where('~') { pos: CachedBlockPosition ->
                    pos.blockState.isAir
                }
                .build()
        }

        fun getCopperGodOratoryListening(): BlockPattern {
            return BlockPatternBuilder.start()
                .aisle("~^~", "###", "~#~")
                .where('^', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.LIGHTNING_ROD)))
                .where(
                    '#',
                    CachedBlockPosition.matchesBlockState(
                        BlockStatePredicate.forBlock(CopperGodBlocks.cu_diamond).with(
                            ORATORY_LISTENING, Predicate.isEqual(true)
                        )
                    )
                )
                .where('~') { pos: CachedBlockPosition ->
                    pos.blockState.isAir
                }
                .build()
        }

        private data class Vec2d(val x: Int, val y: Int)

        private val setToSkip = setOf(Vec2d(0, 0), Vec2d(1, 0), Vec2d(0, 2), Vec2d(2, 0), Vec2d(2, 2))


        fun createOratory(world: World, patternResult: BlockPattern.Result) {

            //TODO: break if not worthy ?
            for (i in 0 until patternResult.width) {
                for (j in 0 until patternResult.height) {
                    if (Vec2d(i, j) in setToSkip) continue

                    val cachedBlockPosition = patternResult.translate(i, j, 0)
                    world.setBlockState(
                        cachedBlockPosition.blockPos,
                        cachedBlockPosition.blockState.with(ORATORY_LISTENING, true),
                        2
                    )
                    world.syncWorldEvent(
                        2001,
                        cachedBlockPosition.blockPos,
                        getRawIdFromState(cachedBlockPosition.blockState)
                    )
                }
            }
            val lightningBoltPos = patternResult.translate(1, 0, 0).blockPos
            val thunderEntity = LightningEntity(EntityType.LIGHTNING_BOLT, world)
            thunderEntity.setPos(
                lightningBoltPos.x.toDouble(),
                lightningBoltPos.y.toDouble(),
                lightningBoltPos.z.toDouble()
            )
            if (world.spawnEntity(thunderEntity)) {
                if (!world.isClient) {
                    world.server?.let {
                        it.playerManager.playerList
                            .filter { player -> player.pos.isInRange(lightningBoltPos.toCenterPos(), 50.0) }
                            .forEach { player -> player.sendMessage(getOratoryCreationMessage()) }
                    }
                }
            }
        }

        fun destroyOratory(world: World, pos: BlockPos) {
            val range = (-2..2)
            for (x in range) {
                for (y in range) {
                    for (z in range) {
                        val currentBlockPos = BlockPos(pos.x + x, pos.y + y, pos.z + z)
                        val state = CopperGodBlocks.cu_diamond.defaultState.with(
                            ORATORY_LISTENING, true)
                        if (world.getBlockState(currentBlockPos) == state) {

                            world.setBlockState(
                                currentBlockPos,
                                Blocks.AIR.defaultState,
                                Block.NOTIFY_ALL
                            )
                            world.syncWorldEvent(
                                WorldEvents.BLOCK_BROKEN,
                                currentBlockPos,
                                getRawIdFromState(state)
                            )
                            dropStack(world, currentBlockPos, state.block.asItem().defaultStack)
                        }
                    }
                }
            }

        }


        private fun updatePatternBlocks(world: World, patternResult: BlockPattern.Result) {
            for (i in 0 until patternResult.width) {
                for (j in 0 until patternResult.height) {
                    val cachedBlockPosition = patternResult.translate(i, j, 0)
                    world.updateNeighbors(cachedBlockPosition.blockPos, Blocks.AIR)
                }
            }
        }


        fun offer(
            player: PlayerEntity,
            stack: ItemStack,
            world: World,
            result: BlockPattern.Result?
        ): ActionResult {


            if (world.isClient) {
                return if (stack.item === Items.COPPER_INGOT && result != null) {
                    ActionResult.SUCCESS
                } else {
                    ActionResult.FAIL
                }
            }
            result ?: return ActionResult.PASS
            if (!CopperToolService.isWorthy(player)) {
                player.sendMessage(CopperGodMessages.notWorthy())
                return ActionResult.FAIL
            }



            if (stack.item === Items.COPPER_INGOT && stack.count == 64) {
                stack.count = 0
                player.addStatusEffect(StatusEffectInstance(COPPERIZED, 5000, 1, false, true))
                player.inventory.main.stream().filter { itemStack -> isCopperItem(itemStack.item) }
                    .forEach { itemStack ->
                        println("charging $itemStack")
                        setCharge(itemStack, 30)
                    }

                player.sendMessage(getRefillMessage(30))
                return ActionResult.SUCCESS

            } else if (stack.item === Items.COPPER_INGOT) {
                player.sendMessage(getMoreCopper(stack.count))
                return ActionResult.PASS
            } else {
                player.sendMessage(needCopper())
                val thunderEntity = LightningEntity(EntityType.LIGHTNING_BOLT, world)
                thunderEntity.setPos(player.x, player.y, player.z)
                world.spawnEntity(thunderEntity)
                return ActionResult.PASS
            }
        }
    }
}