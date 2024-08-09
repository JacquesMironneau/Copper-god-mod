package com.pashmi.effects

import com.pashmi.CopperGodMod.MOD_ID
import com.pashmi.annotations.AutoRegister
import com.pashmi.annotations.AutoRegisterClass
import net.minecraft.entity.effect.StatusEffectCategory

@AutoRegisterClass(MOD_ID)
class CopperEffect {

    companion object {

        @AutoRegister("fast-falling")
        val FAST_FALLING: FastFallingEffect = FastFallingEffect()

        @AutoRegister("copperized")
        val COPPERIZED = CopperizedEffect(StatusEffectCategory.BENEFICIAL, 13395456)

    }
}