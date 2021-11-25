package com.mystic.eclipse.mixin;

import com.mystic.eclipse.lighting.EclipseChunkSkylightProvider;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightingProvider.class)
public class LightingMixin {

    @Mutable @Shadow @Final private ChunkLightProvider<?, ?> skyLightProvider;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(ChunkProvider chunkProvider, boolean hasBlockLight, boolean hasSkyLight, CallbackInfo ci){
        this.skyLightProvider = hasSkyLight ? new EclipseChunkSkylightProvider(chunkProvider) : null;
    }
}
