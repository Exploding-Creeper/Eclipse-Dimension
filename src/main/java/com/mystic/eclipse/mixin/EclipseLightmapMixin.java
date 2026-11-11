package com.mystic.eclipse.mixin;

import com.mystic.eclipse.worldgen.dimension.EclipseDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightTexture.class)
public class EclipseLightmapMixin {
    @Redirect(
            method = "updateLightTexture",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getSkyDarken(F)F")
    )
    private float eclipse$daylightCurve(ClientLevel level, float partialTick) {
        if (EclipseDimension.isEclipseDimension(level)) {
            return 1.0F;
        }
        return level.getSkyDarken(partialTick);
    }

    @Redirect(
            method = "updateLightTexture",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LightTexture;getBrightness(Lnet/minecraft/world/level/dimension/DimensionType;I)F")
    )
    private float eclipse$keepDarkGroundVisible(DimensionType type, int light) {
        float brightness = LightTexture.getBrightness(type, light);
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null && EclipseDimension.isEclipseDimension(level) && light == 0) {
            return 0.07F;
        }
        return brightness;
    }
}
