package com.pashmi.mixin.client;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/Collection;size()I"), require = 0)
    private int pashmi_styledPlayerList$replaceWithZero(Collection instance) {
        return 999;
    }
}
