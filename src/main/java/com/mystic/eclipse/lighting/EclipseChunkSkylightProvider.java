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
		int propagatedLevel = super.getPropagatedLevel(sourceId, targetId, level);

		if (propagatedLevel == 15) {
			return propagatedLevel;
		}

		return getLightLevel(BlockPos.unpackLongX(targetId));
	}

	public static int getLightLevel(int x) {
		if (x < -16) {
			return 15;
		} else if (x < -15) {
			return 12;
		} else if (x < -14) {
			return 9;
		} else if (x < 13) {
			return 8;
		} else if (x < 14) {
			return 6;
		} else if (x < 15) {
			return 3;
		} else {
			return 0;
		}
	}
}
