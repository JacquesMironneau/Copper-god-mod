package com.pashmi.items

import com.pashmi.CopperGodMod.MOD_ID
import com.pashmi.annotations.AutoRegisterItem
import com.pashmi.annotations.AutoRegisterItemClass
import com.pashmi.items.CopperMaterial.Companion.COPPERITE
import net.minecraft.item.Item

@AutoRegisterItemClass(MOD_ID)
class CopperItems {

    companion object {
        @AutoRegisterItem("copper_sword")
        var copper_sword = ThunderAppealedSword(COPPERITE, 3, -2.3f, Item.Settings(), CopperToolService)

        @AutoRegisterItem("copper_pickaxe")
        var copper_pickaxe = CopperHastyPickaxe(COPPERITE, 1, -2.8f, Item.Settings(), CopperToolService)
    }
}