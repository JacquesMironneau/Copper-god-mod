package com.pashmi.blocks

import com.pashmi.blocks.CopperGodStructureService.Companion.createOratory
import com.pashmi.blocks.CopperGodStructureService.Companion.destroyOratory
import com.pashmi.blocks.CopperGodStructureService.Companion.getCopperGodOratory
import com.pashmi.blocks.CopperGodStructureService.Companion.getCopperGodOratoryListening
import com.pashmi.blocks.CopperGodStructureService.Companion.offer
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class CopperDiamondBlock(settings: Settings) : Block(settings) {

    init {
        defaultState = defaultState.with(ORATORY_LISTENING, false)
    }

    companion object {
        val ORATORY_LISTENING: BooleanProperty = BooleanProperty.of("listening")
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(ORATORY_LISTENING)
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (hand == Hand.OFF_HAND) return ActionResult.PASS

        val stack = player.getStackInHand(hand)
        return offer(player, stack, world, getCopperGodOratoryListening().searchAround(world, pos))
    }

    override fun onBlockAdded(
        state: BlockState,
        world: World,
        pos: BlockPos,
        oldState: BlockState,
        notify: Boolean
    ) {
        val res = getCopperGodOratory().searchAround(world, pos) ?: return
        createOratory(world, res)
    }


    override fun onStateReplaced(
        state: BlockState,
        world: World,
        pos: BlockPos,
        newState: BlockState,
        moved: Boolean
    ) {


        if (state.block == CopperGodBlocks.cu_diamond && state.get(ORATORY_LISTENING)) {

            if (!world.isClient)
                destroyOratory(world, pos)
        }
    }

}