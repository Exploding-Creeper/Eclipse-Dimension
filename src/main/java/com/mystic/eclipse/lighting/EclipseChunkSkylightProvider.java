package com.mystic.eclipse.lighting;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.ChunkSkyLightProvider;

public class EclipseChunkSkylightProvider extends ChunkSkyLightProvider {
	private static ChunkProvider chunkProvider2;

	public EclipseChunkSkylightProvider(ChunkProvider chunkProvider) {
		super(chunkProvider);
		chunkProvider2 = chunkProvider;
	}

	public int getLightLevel(BlockPos blockPos) {
		int x = blockPos.getX();
			if (blockPos.getY() >= chunkProvider2.getWorld().getBottomY()) {
				if (x < -16) {
					return 0;
				} else if (x < -15) {
					return 3;
				} else if (x < -14) {
					return 6;
				} else if (x < 13) {
					return 8;
				} else if (x < 14) {
					return 9;
				} else if (x < 15) {
					return 12;
				} else {
					return 15;
				}
			}
		return 0;
	}

	@Override
	protected int getPropagatedLevel(long sourceId, long targetId, int level) {
		int propagatedLevel = super.getPropagatedLevel(sourceId, targetId, level);

		if (propagatedLevel == 15) {
			return propagatedLevel;
		}

		return getLightLevel(BlockPos.fromLong(targetId));
	}
}
