package com.pashmi.mixin;

import com.pashmi.effects.CopperEffect;
import com.pashmi.items.CopperGodMessages;
import com.pashmi.items.CopperToolService;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningRodBlock.class)
public abstract class LightningRodBlockMixin extends AbstractBlock{

    @Unique
    private final CopperToolService service;

    public LightningRodBlockMixin(AbstractBlock.Settings settings) {
        super(settings);
        service = CopperToolService.INSTANCE;
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        ItemStack stack = player.getStackInHand(hand);
        BlockPattern.Result result = this.getCopperGodOratory().searchAround(world, pos);

        if (world.isClient) {

            if (stack.getItem() == Items.COPPER_INGOT && result != null) {
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.PASS;
            }
        }

        if (result == null) {
            return ActionResult.PASS;
        } else {
            if (stack.getItem() == Items.COPPER_INGOT) {
                stack.setCount(0);
                player.addStatusEffect(new StatusEffectInstance(CopperEffect.Companion.getCOPPERIZED(), 5000, 3, true, true));
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.PASS;
            }
        }
    }

    @Unique
    private BlockPattern getCopperGodOratory() {
        return BlockPatternBuilder.start()
                    .aisle("~^~", "###", "~#~")
                    .where('^', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.LIGHTNING_ROD)))
                    .where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.WAXED_COPPER_BLOCK)))
                    .where('~', pos -> pos.getBlockState().isAir())
                    .build();
    }

    @Unique
    private BlockPattern getCopperGodOratoryDirty() {
        return BlockPatternBuilder.start()
                .aisle("~^~", "###", "~#~")
                .where('^', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.LIGHTNING_ROD)))
                .where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.COPPER_BLOCK)))
                .where('~', pos -> pos.getBlockState().isAir())
                .build();
    }

    @Inject(method
            = "Lnet/minecraft/block/LightningRodBlock;onBlockAdded(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)V",
            at = @At("TAIL"))
    public void pashmi_onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        System.out.println("HELLO IT IS MIXINNN");

        BlockPattern.Result result = this.getCopperGodOratory().searchAround(world, pos);
        if (result != null) {
            LightningEntity thunderEntity = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
            thunderEntity.setPos(pos.getX(), pos.getY(), pos.getZ());
            if (world.spawnEntity(thunderEntity)) {

                if (!world.isClient && world.getServer() != null) {
                    world.getServer().getPlayerManager().getPlayerList().stream()
                            .filter((player) -> player.getPos().isInRange(pos.toCenterPos(), 50))
                            .forEach((player) -> player.sendMessage(CopperGodMessages.Companion.getOratoryCreationMessage()));
                }
            }
        }
    }



}