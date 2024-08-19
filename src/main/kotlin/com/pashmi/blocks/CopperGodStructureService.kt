package com.pashmi.blocks

import com.pashmi.blocks.CopperDiamondBlock.Companion.ORATORY_BREAKING
import com.pashmi.blocks.CopperDiamondBlock.Companion.ORATORY_LISTENING
import com.pashmi.effects.CopperEffect.Companion.COPPERIZED
import com.pashmi.items.CopperGodMessages
import com.pashmi.items.CopperGodMessages.Companion.getMoreCopper
import com.pashmi.items.CopperGodMessages.Companion.getOratoryCreationMessage
import com.pashmi.items.CopperGodMessages.Companion.getOratoryDestructionMessage
import com.pashmi.items.CopperGodMessages.Companion.getRefillMessage
import com.pashmi.items.CopperGodMessages.Companion.needCopper
import com.pashmi.items.CopperItems.Companion.isCopperItem
import com.pashmi.items.CopperToolService
import com.pashmi.items.CopperToolService.setCharge
import com.pashmi.utils.repairItem
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
import net.minecraft.item.ToolItem
import net.minecraft.predicate.block.BlockStatePredicate
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Direction.*
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

        private fun Block.isCuDiamond(): Boolean = this == CopperGodBlocks.cu_diamond

        private fun processDirection(world: World, pos: BlockPos): Direction {
            return listOf(NORTH, SOUTH, EAST, WEST)
                .firstOrNull { direction -> world.getBlockState(pos.add(direction.vector)).block.isCuDiamond() } ?: DOWN
        }

        private fun breakOnlyOratory(world: World, pos: BlockPos): Boolean {
            val cuDiamondState = CopperGodBlocks.cu_diamond.defaultState.with(
                ORATORY_LISTENING, true
            )

            val state = world.getBlockState(pos)
            if (state in setOf(cuDiamondState, Blocks.LIGHTNING_ROD.defaultState)) {

                world.setBlockState(
                    pos,
                    CopperGodBlocks.cu_diamond.defaultState.with(ORATORY_BREAKING, true),
                    Block.NOTIFY_LISTENERS
                )
                world.setBlockState(
                    pos,
                    Blocks.AIR.defaultState,
                    Block.NOTIFY_LISTENERS
                )
                world.syncWorldEvent(
                    WorldEvents.BLOCK_BROKEN,
                    pos,
                    getRawIdFromState(state)
                )
                dropStack(world, pos, state.block.asItem().defaultStack)
                return true
            }
            return false
        }

        private fun breakFromSide(world: World, pos: BlockPos, directionToMiddle: Direction): Boolean {
            return breakOnlyOratory(world, pos.add(directionToMiddle.vector)) &&
                    breakOnlyOratory(world, pos.add(directionToMiddle.vector.add(UP.vector))) &&
                    breakOnlyOratory(world, pos.add(directionToMiddle.vector.add(DOWN.vector))) &&
                    breakOnlyOratory(world, pos.add(directionToMiddle.vector.multiply(2)))

        }

        private fun breakFromCenter(world: World, pos: BlockPos, direction: Direction): Boolean {

            val up = breakOnlyOratory(world, pos.add(UP.vector))
            val down = breakOnlyOratory(world, pos.add(DOWN.vector))

            if (direction == NORTH || direction == SOUTH) {
                return up && down && breakOnlyOratory(world, pos.add(NORTH.vector)) &&
                        breakOnlyOratory(world, pos.add(SOUTH.vector))
            } else if (direction == WEST || direction == EAST) {
                return up && down && breakOnlyOratory(world, pos.add(WEST.vector)) &&
                        breakOnlyOratory(world, pos.add(EAST.vector))
            }
            return false
        }

        private fun breakFromTop(world: World, pos: BlockPos, direction: Direction): Boolean {
            val down = breakOnlyOratory(world, pos.add(DOWN.vector))
            val down2 = breakOnlyOratory(world, pos.add(DOWN.vector.multiply(2)))

            if (direction == NORTH || direction == SOUTH) {
                return down2 && down && breakOnlyOratory(world, pos.add(NORTH.vector).add(DOWN.vector)) &&
                        breakOnlyOratory(world, pos.add(SOUTH.vector).add(DOWN.vector))
            } else if (direction == WEST || direction == EAST) {
                return down2 && down && breakOnlyOratory(world, pos.add(WEST.vector).add(DOWN.vector)) &&
                        breakOnlyOratory(world, pos.add(EAST.vector).add(DOWN.vector))
            }
            return false

        }


        private fun breakFromBottom(world: World, pos: BlockPos, direction: Direction): Boolean {
            val up = breakOnlyOratory(world, pos.add(UP.vector))
            val up2 = breakOnlyOratory(world, pos.add(UP.vector.multiply(2)))

            if (direction == NORTH || direction == SOUTH) {
                return up && up2 && breakOnlyOratory(world, pos.add(NORTH.vector.add(UP.vector))) &&
                        breakOnlyOratory(world, pos.add(SOUTH.vector.add(UP.vector)))
            } else if (direction == WEST || direction == EAST) {
                return up && up2 && breakOnlyOratory(world, pos.add(WEST.vector).add(UP.vector)) &&
                        breakOnlyOratory(world, pos.add(EAST.vector).add(UP.vector))
            }

            return false
        }

        private fun discoverAndBreakOratory(world: World, pos: BlockPos): Boolean {

            val block = world.getBlockState(pos).block
            if (block == Blocks.LIGHTNING_ROD) {
                val directionToMiddle = processDirection(world, pos.add(DOWN.vector))
                return breakFromTop(world, pos, directionToMiddle)
            }

            val above = world.getBlockState(pos.add(UP.vector))

            return when (above.block) {
                CopperGodBlocks.cu_diamond -> {
                    breakFromBottom(world, pos, processDirection(world, pos.add(UP.vector)))
                }

                Blocks.LIGHTNING_ROD -> {
                    breakFromCenter(world, pos, processDirection(world, pos))
                }

                else -> {
                    val directionToMiddle = processDirection(world, pos)
                    breakFromSide(world, pos, directionToMiddle)
                }
            }
        }


        fun destroyOratory(world: World, pos: BlockPos) {
            if (discoverAndBreakOratory(world, pos)) {
                if (world.isClient.not()) {
                    world.server?.let {
                        it.playerManager.playerList
                            .filter { player -> player.pos.isInRange(pos.toCenterPos(), 50.0) }
                            .forEach { player -> player.sendMessage(getOratoryDestructionMessage()) }
                    }
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
                player.addStatusEffect(StatusEffectInstance(COPPERIZED, 5000, 2, false, true))
                player.inventory.main.stream().filter { itemStack -> isCopperItem(itemStack.item) }
                    .forEach { itemStack ->
                        println("charging $itemStack")
                        setCharge(itemStack, 30)
                        val item = itemStack.item
                        if (item is ToolItem) repairItem(itemStack, item, 0.4f)
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