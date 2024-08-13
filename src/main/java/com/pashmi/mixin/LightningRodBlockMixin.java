package com.pashmi.mixin;

import com.pashmi.blocks.CopperGodStructureService;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningRodBlock.class)
public abstract class LightningRodBlockMixin extends AbstractBlock{

    public LightningRodBlockMixin(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (hand ==  Hand.OFF_HAND) return ActionResult.PASS;

        ItemStack stack = player.getStackInHand(hand);
        BlockPattern.Result result = CopperGodStructureService.Companion.getCopperGodOratoryListening().searchAround(world, pos);
        return CopperGodStructureService.Companion.offer(player, stack, world, result);
    }

    @Inject(method
            = "Lnet/minecraft/block/LightningRodBlock;onBlockAdded(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)V",
            at = @At("TAIL"))
    public void pashmi_onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        System.out.println("HELLO IT IS MIXINNN");

        BlockPattern.Result result = CopperGodStructureService.Companion.getCopperGodOratory().searchAround(world, pos);
        if (result != null) {
            CopperGodStructureService.Companion.createOratory(world, result);
        }
    }




}