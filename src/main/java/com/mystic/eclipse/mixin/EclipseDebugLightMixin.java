package com.mystic.eclipse.mixin;

import com.mystic.eclipse.worldgen.biomes.EclipseZones;
import com.mystic.eclipse.worldgen.dimension.EclipseDimension;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DebugScreenOverlay.class)
public class EclipseDebugLightMixin {
    @Redirect(
            method = "getGameInformation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBrightness(Lnet/minecraft/world/level/LightLayer;Lnet/minecraft/core/BlockPos;)I"
            )
    )
    private int eclipse$debugLight(ClientLevel level, LightLayer layer, BlockPos pos) {
        if (layer == LightLayer.SKY && EclipseDimension.isEclipseDimension(level)) {
            return EclipseZones.lightLevel(level, pos);
        }
        return level.getBrightness(layer, pos);
    }
}
