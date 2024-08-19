package com.pashmi.items

import com.pashmi.CopperGodMod.MOD_ID
import com.pashmi.annotations.AutoRegister
import com.pashmi.annotations.AutoRegisterClass
import com.pashmi.items.CopperMaterial.Companion.COPPERITE
import net.minecraft.item.Item

@AutoRegisterClass(MOD_ID)
class CopperItems {

    companion object {
        @AutoRegister("copper_sword")
        var copper_sword = ThunderAppealedSword(COPPERITE, 3, -2.3f, Item.Settings(), CopperToolService)

        @AutoRegister("copper_pickaxe")
        var copper_pickaxe = CopperHastyPickaxe(COPPERITE, 1, -2.8f, Item.Settings(), CopperHastyMiningToolService(CopperToolService))

        @AutoRegister("copper_shovel")
        var copper_shovel = CopperHastyShovel(COPPERITE, 1f, -2.8f, Item.Settings(), CopperHastyMiningToolService(CopperToolService))

        @AutoRegister("copper_axe")
        var copper_axe = CopperHastyAxe(COPPERITE, 1f, -2.8f, Item.Settings(), CopperHastyMiningToolService(CopperToolService))

        @AutoRegister("copper_hoe")
        var copper_hoe = CopperHastyHoe(COPPERITE, 1, -2.8f, Item.Settings())

        @AutoRegister("copper_book")
        var copper_book = CopperBook(Item.Settings().maxCount(1))

        fun isCopperItem(item: Item): Boolean {
            return item is ThunderAppealedSword || item is CopperHastyPickaxe || item is CopperHastyShovel || item is CopperHastyAxe
        }
    }
}