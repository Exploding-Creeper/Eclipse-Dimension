package com.mystic.eclipse.mixin;

import com.mystic.eclipse.worldgen.biomes.EclipseZones;
import com.mystic.eclipse.worldgen.dimension.EclipseDimension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LevelLightEngine.class)
public class EclipseRawBrightnessMixin {
    @Shadow
    @Nullable
    private LightEngine<?, ?> blockEngine;

    @Shadow
    protected LevelHeightAccessor levelHeightAccessor;

    @Inject(method = "getRawBrightness", at = @At("HEAD"), cancellable = true)
    private void eclipse$regionalRaw(BlockPos pos, int skyDarken, CallbackInfoReturnable<Integer> cir) {
        if (!(this.levelHeightAccessor instanceof Level level) || !EclipseDimension.isEclipseDimension(level)) {
            return;
        }
        int block = this.blockEngine == null ? 0 : this.blockEngine.getLightValue(pos);
        cir.setReturnValue(Math.max(block, EclipseZones.lightLevel(level, pos)));
    }
}
