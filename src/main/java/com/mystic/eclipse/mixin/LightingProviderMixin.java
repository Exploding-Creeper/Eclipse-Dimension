package com.mystic.eclipse.mixin;

import com.mystic.eclipse.utils.lighting.LightingValueChanger;
import com.mystic.eclipse.worldgen.dimension.EclipseDimension;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightingProvider.class)
public class LightingProviderMixin {

    @Final @Shadow protected HeightLimitView world;
    @Final @Shadow private ChunkLightProvider skyLightProvider;
    @Final @Shadow private ChunkLightProvider blockLightProvider;

    @Inject(at = @At("HEAD"), method = "getLight", cancellable = true)
    public void getBaseLightLevel(BlockPos pos, int ambientDarkness, CallbackInfoReturnable<Integer> cir) {
        World world1 = getWorldFromHeightLimitView(world);
        if (EclipseDimension.isEclipseDimension(world1)) {
            int i = this.skyLightProvider == null ? 0 : LightingValueChanger.getLightLevel(pos.getX()) - ambientDarkness;
            int j = this.blockLightProvider == null ? 0 : this.blockLightProvider.getLightLevel(pos);
            cir.setReturnValue(Math.max(j, i));
        }
    }

    private static World getWorldFromHeightLimitView(HeightLimitView heightLimitView) {
        if(heightLimitView instanceof World) {
            return (World) heightLimitView;
        } else if (heightLimitView instanceof ChunkRendererRegion) {
            return ((ChunkRendererRegion) heightLimitView).world;
        } else if (heightLimitView instanceof StructureWorldAccess) {
            return ((StructureWorldAccess) heightLimitView).toServerWorld();
        } else {
            return null;
        }
    }
}
