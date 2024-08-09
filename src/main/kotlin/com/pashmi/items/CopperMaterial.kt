package com.pashmi.items

import net.fabricmc.yarn.constants.MiningLevels
import net.minecraft.item.Items
import net.minecraft.item.ToolMaterial
import net.minecraft.recipe.Ingredient

class CopperMaterial : ToolMaterial {

    companion object {

        val COPPERITE = CopperMaterial()

        fun initialize() {}

    }

    override fun getDurability() = 300

    override fun getMiningSpeedMultiplier() = 6.0f

    override fun getAttackDamage() = 2.5f

    override fun getMiningLevel() = MiningLevels.IRON

    override fun getEnchantability() = 14

    override fun getRepairIngredient(): Ingredient = Ingredient.ofItems(Items.COPPER_BLOCK)
}