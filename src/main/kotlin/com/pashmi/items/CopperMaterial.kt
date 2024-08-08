package com.pashmi.items

import net.minecraft.item.Items
import net.minecraft.item.ToolMaterial
import net.minecraft.recipe.Ingredient

class CopperMaterial : ToolMaterial {

    companion object {

        val COPPERITE = CopperMaterial()

        fun initialize() {}

    }

    override fun getDurability() = 300

    override fun getMiningSpeedMultiplier() = 1.2f

    override fun getAttackDamage() = 2.5f

    override fun getMiningLevel() = 2

    override fun getEnchantability() = 1

    override fun getRepairIngredient(): Ingredient = Ingredient.ofItems(Items.COPPER_BLOCK)
}