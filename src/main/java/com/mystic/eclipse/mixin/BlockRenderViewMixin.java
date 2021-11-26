package com.mystic.eclipse.mixin;

import com.mystic.eclipse.utils.lighting.LightingValueChanger;
import com.mystic.eclipse.worldgen.dimension.EclipseDimension;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.LightType;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRenderView.class)
public interface BlockRenderViewMixin {

    @Inject(at = @At("HEAD"), method = "getLightLevel", cancellable = true)
    default void getBaseLightLevel(LightType type, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        World world = getWorldFromBlockLimitView((BlockRenderView) this);
        if (EclipseDimension.isEclipseDimension(world)) {
           cir.setReturnValue(LightingValueChanger.getLightLevel(pos.getX()));
        }
    }

    private static World getWorldFromBlockLimitView(BlockRenderView blockRenderView) {
        if(blockRenderView instanceof World) {
            return (World) blockRenderView;
        } else if (blockRenderView instanceof ChunkRendererRegion) {
            return ((ChunkRendererRegion) blockRenderView).world;
        } else if (blockRenderView instanceof StructureWorldAccess) {
            return ((StructureWorldAccess) blockRenderView).toServerWorld();
        } else {
            return null;
        }
    }
}
