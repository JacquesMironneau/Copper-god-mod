package com.pashmi.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract boolean damage(DamageSource source, float amount);

    @Inject(method
            = "onStruckByLightning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LightningEntity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), cancellable = true)
    private void pashmiCopperGod_onStruckByLightning(ServerWorld world, LightningEntity lightning, CallbackInfo ci) {
        // Thunderstruck are way more OP !!!
        this.damage(world.getDamageSources().lightningBolt(), 5F);
        ci.cancel();
    }
}