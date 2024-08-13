package com.pashmi.blocks

import com.pashmi.CopperGodMod.MOD_ID
import com.pashmi.annotations.AutoRegister
import com.pashmi.annotations.AutoRegisterClass
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.MapColor
import net.minecraft.sound.BlockSoundGroup

@AutoRegisterClass(MOD_ID)
class CopperGodBlocks {
    companion object {
        @AutoRegister("cu_diamond")
        var cu_diamond = CopperDiamondBlock(
            FabricBlockSettings.create().sounds(BlockSoundGroup.COPPER).mapColor(MapColor.ORANGE).strength(3.0F, 6.0F)
        )
    }
}

