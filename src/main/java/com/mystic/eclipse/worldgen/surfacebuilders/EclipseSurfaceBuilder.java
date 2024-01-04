package com.mystic.eclipse.worldgen.surfacebuilders;

import com.mystic.eclipse.init.BlockInit;
import com.mystic.eclipse.utils.noise.FastNoiseLite;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.BlockColumn;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

import java.util.Objects;

public class EclipseSurfaceBuilder extends SurfaceBuilder {

    RandomSplitter random;
    int seaLevel;
    long seed;
    BlockState defaultBlock;
    NoiseConfig noiseRegistry;

    public EclipseSurfaceBuilder(NoiseConfig noiseRegistry1, BlockState defaultState, int seaLevel1, RandomSplitter randomProvider) {
        super(noiseRegistry1, defaultState, seaLevel1, randomProvider);
        random = randomProvider;
        seaLevel = seaLevel1;
        defaultBlock = defaultState;
        noiseRegistry = noiseRegistry1;
    }


    public static BlockState getTopMaterial(Chunk chunk) {
        int blockPos = chunk.getPos().x * 15;
        if (blockPos <= -16) {
            return BlockInit.DARK_GRASS_BLOCK.getDefaultState();
        } else if (blockPos > -16 && blockPos < 15) {
            return BlockInit.TWILIGHT_GRASS_BLOCK.getDefaultState();
        } else if (blockPos >= 15) {
            return BlockInit.LIGHT_GRASS_BLOCK.getDefaultState();
        }
        return Blocks.PUMPKIN.getDefaultState();
    }

    public static BlockState getMidMaterial(Chunk chunk) {
        int blockPos = chunk.getPos().x * 15;
        if (blockPos <= -16) {
            return BlockInit.DARK_DIRT_BLOCK.getDefaultState();
        } else if (blockPos > -16 && blockPos < 15) {
            return BlockInit.TWILIGHT_DIRT_BLOCK.getDefaultState();
        } else if (blockPos >= 15) {
            return BlockInit.LIGHT_DIRT_BLOCK.getDefaultState();
        }
        return Blocks.PUMPKIN.getDefaultState();
    }

    public static BlockState getBottomMaterial(Chunk chunk) {
        int blockPos = chunk.getPos().x * 15;
        if (blockPos <= -16) {
            return BlockInit.DARK_STONE_BLOCK.getDefaultState();
        } else if (blockPos > -16 && blockPos < 15) {
            return BlockInit.TWILIGHT_STONE_BLOCK.getDefaultState();
        } else if (blockPos >= 15) {
            return BlockInit.LIGHT_STONE_BLOCK.getDefaultState();
        }
        return Blocks.PUMPKIN.getDefaultState();
    }

    @Override
    public void buildSurface(NoiseConfig noiseConfig, BiomeAccess biomeAccess, Registry<Biome> biomeRegistry, boolean useLegacyRandom, HeightContext heightContext, final Chunk chunk, ChunkNoiseSampler chunkNoiseSampler, MaterialRules.MaterialRule materialRule) {
        final BlockPos.Mutable mutable = new BlockPos.Mutable();
        final ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.getStartX();
        int j = chunkPos.getStartZ();
        BlockColumn blockColumn = new BlockColumn() {
            public BlockState getState(int y) {
                return chunk.getBlockState(mutable.setY(y));
            }

            public void setState(int y, BlockState state) {
                HeightLimitView heightLimitView = chunk.getHeightLimitView();
                if (y >= heightLimitView.getBottomY() && y < heightLimitView.getTopY()) {
                    chunk.setBlockState(mutable.setY(y), state, false);
                    if (!state.getFluidState().isEmpty()) {
                        chunk.markBlockForPostProcessing(mutable);
                    }
                }

            }

            public String toString() {
                return "ChunkBlockColumn " + chunkPos;
            }
        };
        Objects.requireNonNull(biomeAccess);

        for(int k = 0; k < 16; ++k) {
            for(int l = 0; l < 16; ++l) {
                int m = i + k;
                int n = j + l;
                mutable.setX(m).setZ(n);

                int r = Integer.MIN_VALUE;
                int t = chunk.getBottomY();

                for (int u = 320; u >= t; --u) {
                    BlockState blockState = blockColumn.getState(u);
                    if (blockState.isAir()) {
                        r = Integer.MIN_VALUE;
                    }

                    if (!blockState.getFluidState().isEmpty()) {
                        if (r == Integer.MIN_VALUE) {
                            r = u + 1;
                        }
                    }
                    if (r == Integer.MIN_VALUE) {
                        r = u;
                    }

                    FastNoiseLite noiseLite = new FastNoiseLite();
                    int xRan = Math.round(((float) random.split(k, chunkNoiseSampler.blockY() - 3, l).nextGaussian() * 3.0f) + 7);
                    int zRan = Math.round(((float) random.split(k, chunkNoiseSampler.blockY() - 3, l).nextGaussian() * 3.0f) + 7);
                    int scaledNoise = Math.round(
                            ((noiseLite.GetNoise(chunkPos.getBlockPos(k, 0, l).getX() + chunkNoiseSampler.blockX() + (xRan / 4.0f), chunkNoiseSampler.blockY() - 3, chunkPos.getBlockPos(k, 0, l).getZ() + chunkNoiseSampler.blockZ() + (zRan / 4.0f) * 3) + 7) *
                                    ((noiseLite.GetNoise(chunkPos.getBlockPos(k, 0, l).getX() + chunkNoiseSampler.blockX() + (xRan / 4.0f), chunkNoiseSampler.blockY() - 3, chunkPos.getBlockPos(k, 0, l).getZ() + chunkNoiseSampler.blockZ() + (zRan / 4.0f)) * 3) + 7)
                            ));

                    if (r == (80 + scaledNoise) - 1) {
                        chunk.setBlockState(mutable, getTopMaterial(chunk), false);
                    } else if (r >= (80 + scaledNoise) - 7 && r < (80 + scaledNoise) - 1) {
                        chunk.setBlockState(mutable, getMidMaterial(chunk), false);
                    } else if (r >= -59 + scaledNoise && r < 80 + scaledNoise - 7) {
                        chunk.setBlockState(mutable, getBottomMaterial(chunk), false);
                    } else if (r < -59 + scaledNoise) {
                        chunk.setBlockState(mutable, Blocks.BEDROCK.getDefaultState(), false);
                    }
                }
            }
        }
    }
}
