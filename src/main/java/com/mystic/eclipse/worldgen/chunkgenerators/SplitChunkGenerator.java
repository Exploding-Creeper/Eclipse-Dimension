package com.mystic.eclipse.worldgen.chunkgenerators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mystic.eclipse.init.BlockInit;
import com.mystic.eclipse.worldgen.surfacebuilders.EclipseSurfaceBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightSampler;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

public class SplitChunkGenerator extends ChunkGenerator {
    private final BlockState defaultBlock = BlockInit.DARK_STONE_BLOCK.getDefaultState();
    private final BlockState defaultBlockThree = BlockInit.TWILIGHT_STONE_BLOCK.getDefaultState();
    private final BlockState defaultBlockTwo = BlockInit.LIGHT_STONE_BLOCK.getDefaultState();
    protected final RegistryEntry<ChunkGeneratorSettings> settings;

    public static final Codec<SplitChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> {
            return generator.biomeSource;
        }), ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter((generator) -> {
            return generator.settings;
        })).apply(instance, instance.stable(SplitChunkGenerator::new));
    });

    public SplitChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource);
        this.settings = settings;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {}

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        if (!SharedConstants.isOutsideGenerationArea(chunk.getPos())) {
            HeightContext heightContext = new HeightContext(this, region);
            this.buildSurface(chunk, heightContext, noiseConfig, structures, region.getBiomeAccess(), region.getRegistryManager().get(RegistryKeys.BIOME), Blender.getBlender(region));
        }
    }

    public void buildSurface(Chunk chunk, HeightContext heightContext, NoiseConfig noiseConfig, StructureAccessor structureAccessor, BiomeAccess biomeAccess, Registry<Biome> biomeRegistry, Blender blender) {
        ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler((chunkx) -> this.createChunkNoiseSampler(chunkx, structureAccessor, blender, noiseConfig));
        ChunkGeneratorSettings chunkGeneratorSettings = this.settings.value();
        new EclipseSurfaceBuilder(noiseConfig, Blocks.PUMPKIN.getDefaultState(), 0, new CheckedRandom(RandomSeed.getSeed()).nextSplitter()).buildSurface(noiseConfig, biomeAccess, biomeRegistry, chunkGeneratorSettings.usesLegacyRandom(), heightContext, chunk, chunkNoiseSampler, chunkGeneratorSettings.surfaceRule());
    }

    private ChunkNoiseSampler createChunkNoiseSampler(Chunk chunk, StructureAccessor world, Blender blender, NoiseConfig noiseConfig) {
        return ChunkNoiseSampler.create(chunk, noiseConfig, StructureWeightSampler.createStructureWeightSampler(world, chunk.getPos()), this.settings.value(), (x, y, z) -> new AquiferSampler.FluidLevel(y, Blocks.WATER.getDefaultState()), blender);
    }

    @Override
    public void populateEntities(ChunkRegion region) {}

    @Override
    public int getWorldHeight() {
        return 384;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor accessor, Chunk chunk) {
        int blockPos = chunk.getPos().x * 15;
        if (blockPos <= -16) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            int baseX = chunk.getPos().getStartX();
            int baseZ = chunk.getPos().getStartZ();

            for (int xOffset = 0; xOffset < 16; xOffset++) {
                mutable.setX(xOffset);
                for (int zOffset = 0; zOffset < 16; zOffset++) {
                    mutable.setZ(zOffset);
                    int height = getHeight(baseX + xOffset, baseZ + zOffset, Heightmap.Type.WORLD_SURFACE_WG, accessor.world, noiseConfig);
                    for(int y = chunk.getBottomY(); y < height; y++){
                        mutable.setY(y);
                        chunk.setBlockState(mutable, defaultBlock, false);
                    }
                }
            }

            return CompletableFuture.completedFuture(chunk);
        } else if (blockPos > -16 && blockPos < 15) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            int baseX = chunk.getPos().getStartX();
            int baseZ = chunk.getPos().getStartZ();

            for(int xOffset = 0; xOffset < 16; xOffset++){
                mutable.setX(xOffset);
                for(int zOffset = 0; zOffset < 16; zOffset++){
                    mutable.setZ(zOffset);
                    int height = getHeight(baseX + xOffset, baseZ + zOffset, Heightmap.Type.WORLD_SURFACE_WG, accessor.world, noiseConfig);
                    for(int y = chunk.getBottomY(); y < height; y++){
                        mutable.setY(y);
                        chunk.setBlockState(mutable, defaultBlockThree, false);
                    }
                }
            }

            return CompletableFuture.completedFuture(chunk);
        } else if (blockPos >= 15) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            int baseX = chunk.getPos().getStartX();
            int baseZ = chunk.getPos().getStartZ();

            for(int xOffset = 0; xOffset < 16; xOffset++){
                mutable.setX(xOffset);
                for(int zOffset = 0; zOffset < 16; zOffset++){
                    mutable.setZ(zOffset);
                    int height = getHeight(baseX + xOffset, baseZ + zOffset, Heightmap.Type.WORLD_SURFACE_WG, accessor.world, noiseConfig);
                    for(int y = chunk.getBottomY(); y < height; y++){
                        mutable.setY(y);
                        chunk.setBlockState(mutable, defaultBlockTwo, false);
                    }
                }
            }

            return CompletableFuture.completedFuture(chunk);
        } else {
            //Do nothing really here!
            return CompletableFuture.completedFuture(chunk);
        }
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinimumY() {
        return -64;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return this.sampleHeightmap(world, noiseConfig, x, z, null, heightmap.getBlockPredicate()).orElse(world.getBottomY());
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        MutableObject<VerticalBlockSample> mutableObject = new MutableObject<>();
        this.sampleHeightmap(world, noiseConfig, x, z, mutableObject, null);
        return mutableObject.getValue();
    }

    private OptionalInt sampleHeightmap(HeightLimitView world, NoiseConfig noiseConfig, int x, int z, @Nullable MutableObject<VerticalBlockSample> columnSample, @Nullable Predicate<BlockState> stopPredicate) {
        GenerationShapeConfig generationShapeConfig = this.settings.value().generationShapeConfig().trimHeight(world);
        int i = generationShapeConfig.verticalSize();
        int j = generationShapeConfig.minimumY();
        int k = MathHelper.floorDiv(j, i);
        int l = MathHelper.floorDiv(generationShapeConfig.height(), i);
        if (l <= 0) {
            return OptionalInt.empty();
        } else {
            BlockState[] blockStates;
            if (columnSample == null) {
                blockStates = null;
            } else {
                blockStates = new BlockState[generationShapeConfig.height()];
                columnSample.setValue(new VerticalBlockSample(j, blockStates));
            }

            int m = generationShapeConfig.horizontalSize();
            int n = Math.floorDiv(x, m);
            int o = Math.floorDiv(z, m);
            int p = Math.floorMod(x, m);
            int q = Math.floorMod(z, m);
            int r = n * m;
            int s = o * m;
            double d = (double)p / (double)m;
            double e = (double)q / (double)m;
            ChunkNoiseSampler chunkNoiseSampler = new ChunkNoiseSampler(1, noiseConfig, r, s, generationShapeConfig, DensityFunctionTypes.Beardifier.INSTANCE, this.settings.value(), (x1, y, z1) -> new AquiferSampler.FluidLevel(y, Blocks.WATER.getDefaultState()), Blender.getNoBlending());
            chunkNoiseSampler.sampleStartDensity();
            chunkNoiseSampler.sampleEndDensity(0);

            for(int t = l - 1; t >= 0; --t) {
                chunkNoiseSampler.onSampledCellCorners(t, 0);

                for(int u = i - 1; u >= 0; --u) {
                    int v = (k + t) * i + u;
                    double f = (double)u / (double)i;
                    chunkNoiseSampler.interpolateY(v, f);
                    chunkNoiseSampler.interpolateX(x, d);
                    chunkNoiseSampler.interpolateZ(z, e);
                    BlockState blockState = chunkNoiseSampler.sampleBlockState();
                    BlockState blockState2 = blockState == null ? this.defaultBlock : blockState;
                    if (blockStates != null) {
                        int w = t * i + u;
                        blockStates[w] = blockState2;
                    }

                    if (stopPredicate != null && stopPredicate.test(blockState2)) {
                        chunkNoiseSampler.stopInterpolation();
                        return OptionalInt.of(v + 1);
                    }
                }
            }

            chunkNoiseSampler.stopInterpolation();
            return OptionalInt.empty();
        }
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {}
}
