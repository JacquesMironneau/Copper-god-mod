package com.pashmi.effects

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory

class CopperizedEffect(category: StatusEffectCategory, color: Int) : StatusEffect(category, color) {

    override fun canApplyUpdateEffect(duration: Int, amplifier: Int) = true

    override fun applyUpdateEffect(entity: LivingEntity, amplifier: Int) {

        addAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3",
            0.2, EntityAttributeModifier.Operation.MULTIPLY_TOTAL
        )
        addAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635",
            0.2, EntityAttributeModifier.Operation.MULTIPLY_TOTAL
        )

        super.applyUpdateEffect(entity, amplifier)
    }
}