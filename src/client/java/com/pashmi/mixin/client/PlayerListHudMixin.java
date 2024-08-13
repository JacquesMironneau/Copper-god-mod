package com.pashmi.mixin.client;

import com.pashmi.CopperGodClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {

    @Unique
    private static final Identifier COPPER_INGOT = new Identifier("textures/item/copper_ingot.png");

    @Accessor("ENTRY_ORDERING")
    public static Comparator<PlayerListEntry> getEntryOrdering() {
        return Comparator.comparingInt((PlayerListEntry entry) -> entry.getGameMode() == GameMode.SPECTATOR ? 1 : 0)
                .thenComparing((entry) -> CopperGodClient.INSTANCE.getCopperRegistry().getRegistry().get(entry.getProfile().getId()))
                .thenComparing((entry) -> entry.getProfile().getName(), String::compareToIgnoreCase);
    }


    @Unique
    protected void renderCopperIcon(DrawContext context, int width, int x, int y, PlayerListEntry entry) {
        context.getMatrices().push();
        context.getMatrices().translate(0.0F, 0.0F, 100.0F);
        context.drawTexture(COPPER_INGOT, x + width - 20, y, 0, 0, 8, 8, 8, 8);
        context.getMatrices().pop();
    }

    @Inject(method = "getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Lnet/minecraft/text/Text;", at = @At("RETURN"), cancellable = true)
    private void pashmi_injectGetPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        String playerName = cir.getReturnValue().getString();
        Integer copperCount = CopperGodClient.INSTANCE.getCopperRegistry().getRegistry().get(entry.getProfile().getId());
        if (playerName != null && copperCount != null) {
            String result = playerName + ": " + copperCount;
            cir.setReturnValue(Text.literal(result));
        }
    }

    @Inject(method = "renderLatencyIcon(Lnet/minecraft/client/gui/DrawContext;IIILnet/minecraft/client/network/PlayerListEntry;)V", at = @At("HEAD"))
    private void pashmi_renderLatencyIcon(DrawContext context, int width, int x, int y, PlayerListEntry entry, CallbackInfo ci) {
        boolean hasMinedCopper = CopperGodClient.INSTANCE.getCopperRegistry().getRegistry().containsKey(entry.getProfile().getId());
        if (hasMinedCopper) {
            renderCopperIcon(context, width, x, y, entry);
        }
        x = x + 10;
    }

    @ModifyArg(method = "render(Lnet/minecraft/client/gui/DrawContext;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;renderLatencyIcon(Lnet/minecraft/client/gui/DrawContext;IIILnet/minecraft/client/network/PlayerListEntry;)V"), index = 2)
    private int pashmi_injected(int x) {
        return x + 10;
    }
}
