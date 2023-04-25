package com.mystic.eclipse.worldgen.surfacebuilders;

import com.mystic.eclipse.init.BlockInit;
import com.mystic.eclipse.utils.noise.FastNoiseLite;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.BlockColumn;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.noise.NoiseRouter;
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
                int o = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, k, l) + 1;
                mutable.setX(m).setZ(n);

                int p = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, k, l) + 1;
                int r = Integer.MIN_VALUE;
                int t = chunk.getBottomY();

                for(int u = p; u >= t; --u) {
                    BlockState blockState = blockColumn.getState(u);
                    if (blockState.isAir()) {
                        r = Integer.MIN_VALUE;
                    } else if (!blockState.getFluidState().isEmpty()) {
                        if (r == Integer.MIN_VALUE) {
                            r = u + 1;
                        }
                    } else {
                        FastNoiseLite noiseLite = new FastNoiseLite();
                        int scaledNoise = (int) (noiseLite.GetNoise(k, u, l) / 3.0D + 3.0D + random.split(k, u, l).nextDouble() * 0.25D);
                        if (blockColumn.getState(u) != Blocks.BEDROCK.getDefaultState()) {
                            // -1 depth means we are switching from air to solid land. Place the surface block now
                            if (o <= scaledNoise) {
                                // The typical normal dry surface of the biome.
                                if (o >= u - 1) {
                                    chunk.setBlockState(mutable, getTopMaterial(chunk), false);
                                }
                                // Places middle block when starting to go under sealevel.
                                // Think of this as the top block of the bottom of shallow lakes in your biome.
                                else if (o >= u - 7) {
                                    chunk.setBlockState(mutable, getMidMaterial(chunk), false);
                                }
                                // Places the underwater block when really deep under sealevel instead.
                                // This is like the top block of the sea floor.
                                else {
                                    chunk.setBlockState(mutable, getBottomMaterial(chunk), false);
                                }
                            } else {
                                chunk.setBlockState(mutable, getBottomMaterial(chunk), false);
                            }
                        }
                    }
                }
            }
        }
    }
}
