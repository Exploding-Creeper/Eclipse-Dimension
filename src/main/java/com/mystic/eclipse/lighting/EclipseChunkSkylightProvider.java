package com.mystic.eclipse.lighting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.ChunkSkyLightProvider;

public class EclipseChunkSkylightProvider extends ChunkSkyLightProvider {

    public EclipseChunkSkylightProvider(ChunkProvider chunkProvider) {
        super(chunkProvider);
    }

    @Override
    protected int getPropagatedLevel(long sourceId, long targetId, int level) {
        return getLightLevel(BlockPos.unpackLongX(targetId));
    }

    public static int getLightLevel(int x) {
        if (x <= -16) {
            return 15;
        } else if (x < 16) {
            return 7;
        } else {
            return 0;
        }
    }
}
