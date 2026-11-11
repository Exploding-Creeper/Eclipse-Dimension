package com.mystic.eclipse.mixin;

import com.mystic.eclipse.worldgen.biomes.EclipseZones;
import com.mystic.eclipse.worldgen.dimension.EclipseDimension;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
public class EclipseSkyBrightnessMixin {
    @Inject(
            method = "getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void eclipse$regionalLight(BlockAndTintGetter level, BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (!(level instanceof Level world) || !EclipseDimension.isEclipseDimension(world) || state.emissiveRendering(level, pos)) {
            return;
        }
        int block = level.getBrightness(LightLayer.BLOCK, pos);
        int emission = state.getLightEmission(level, pos);
        if (block < emission) {
            block = emission;
        }
        int sky = EclipseZones.lightLevel(world, pos);
        cir.setReturnValue((sky << 20) | (block << 4));
    }
}
