package com.mystic.eclipse.lighting;

import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.LightingProvider;

public class ClientLighting extends LightingProvider {

    public ClientLighting(ChunkProvider chunkProvider, boolean hasBlockLight, boolean hasSkyLight) {
        super(chunkProvider, hasBlockLight, hasSkyLight);
    }



}
