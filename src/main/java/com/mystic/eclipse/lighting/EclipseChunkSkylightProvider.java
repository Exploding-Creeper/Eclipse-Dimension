package com.mystic.eclipse.lighting;

import com.mystic.eclipse.worldgen.dimension.EclipseDimension;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.ChunkSkyLightProvider;

public class EclipseChunkSkylightProvider extends ChunkSkyLightProvider {

    public EclipseChunkSkylightProvider(ChunkProvider chunkProvider) {
        super(chunkProvider);
    }

    @Override
    protected int getPropagatedLevel(long sourceId, long targetId, int level) {
        World world = (World) chunkProvider.getWorld();
        if (sourceId != field_31708 && targetId != field_31708) {
            if (EclipseDimension.isEclipseDimension(world)) {
                return super.getPropagatedLevel(sourceId, targetId, getLightLevel(BlockPos.unpackLongX(targetId)));
            } else {
                return super.getPropagatedLevel(sourceId, targetId, level);
            }
        }
        return super.getPropagatedLevel(sourceId, targetId, level);
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
