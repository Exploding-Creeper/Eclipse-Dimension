package com.mystic.eclipse.worldgen.chunkgenerators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mystic.eclipse.init.BlockInit;
import com.mystic.eclipse.utils.noise.FastNoiseLite;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomSeed;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class SplitChunkGenerator extends ChunkGenerator {

    private final long seed;
    private final Supplier<ChunkGeneratorSettings> settings;
    private final BlockState defaultBlock = BlockInit.DARK_STONE_BLOCK.getDefaultState();
    private final BlockState defaultBlockThree = BlockInit.TWILIGHT_STONE_BLOCK.getDefaultState();
    private final BlockState defaultBlockTwo = BlockInit.LIGHT_STONE_BLOCK.getDefaultState();
    private final GenerationShapeConfig generationShapeConfig;
    public static final Codec<NoiseChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(RegistryLookupCodec.of(Registry.NOISE_WORLDGEN).forGetter((splitChunkGenerator) ->
                    splitChunkGenerator.noiseRegistry), BiomeSource.CODEC.fieldOf("biome_source").forGetter((splitChunkGenerator) ->
                    splitChunkGenerator.populationSource), Codec.LONG.fieldOf("seed").stable().forGetter((spiltChunkGenerator) ->
                    spiltChunkGenerator.seed), ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter((spiltChunkGenerator) ->
                    spiltChunkGenerator.settings)).apply(instance, instance.stable(NoiseChunkGenerator::new)));
    public final NoiseColumnSampler noiseColumnSampler;
    private final SurfaceBuilder surfaceBuilder;
    private FastNoiseLite noise;
    ChunkNoiseSampler.ColumnSampler columnSampler;
    private final Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry;
    private final AquiferSampler.FluidLevelSampler fluidLevelSampler;

    public SplitChunkGenerator(Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry, BiomeSource populationSource, BiomeSource biomeSource, long seed, Supplier<ChunkGeneratorSettings> settings) {
        super(biomeSource, biomeSource, settings.get().getStructuresConfig(), seed);

        this.noiseRegistry = noiseRegistry;
        this.seed = seed;
        this.settings = settings;

        ChunkGeneratorSettings chunkGeneratorSettings = this.settings.get();
        generationShapeConfig = chunkGeneratorSettings.getGenerationShapeConfig();
        columnSampler = (x, y, z) -> noise.GetNoise(x, z);
        AquiferSampler.FluidLevel fluidLevel = new AquiferSampler.FluidLevel(-54, Blocks.LAVA.getDefaultState());
        int seaLevel = chunkGeneratorSettings.getSeaLevel();
        AquiferSampler.FluidLevel fluidLevel2 = new AquiferSampler.FluidLevel(seaLevel, chunkGeneratorSettings.getDefaultFluid());
        this.fluidLevelSampler = (j, k, l) -> {
            return k < Math.min(-54, seaLevel) ? fluidLevel : fluidLevel2;
        };
        this.noiseColumnSampler = new NoiseColumnSampler(generationShapeConfig, chunkGeneratorSettings.hasNoiseCaves(), seed, noiseRegistry, chunkGeneratorSettings.getRandomProvider());

        noise = new FastNoiseLite(); // Create a FastNoise object
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2); // Set the desired noise type
        noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        noise.SetFractalOctaves(6);

        this.surfaceBuilder = new SurfaceBuilder(noiseRegistry, this.defaultBlock, seaLevel, seed, chunkGeneratorSettings.getRandomProvider());
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, Chunk chunk) {
        ChunkGeneratorSettings chunkGeneratorSettings = this.settings.get();
        HeightContext heightContext = new HeightContext(this, region);
        ChunkPos chunkPos = chunk.getPos();
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;
        Random random = new Random();
        int baseX = chunkPos.getStartX();
        int baseZ = chunkPos.getStartZ();
        double d = 0.0625D;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(this.noiseColumnSampler, () -> columnSampler, chunkGeneratorSettings, this.fluidLevelSampler, Blender.getBlender(region));
        int blockPos = chunk.getPos().x * 15;
        if (blockPos <= -16) {
            for (int xOffset = 0; xOffset < 16; ++xOffset) {
                for (int yOffset = 0; yOffset < 16; ++yOffset) {
                    int x = baseX + xOffset;
                    int z = baseZ + yOffset;
                    int surfaceHeight = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, xOffset, yOffset) + 1;
                    MultiNoiseUtil.NoiseValuePoint noise = this.noiseColumnSampler.sample((int) (x * d), (int) (z * d), (int) (xOffset * d));
                    mutable.set(x, -64, z);
                    int height = getHeight(mutable.getX(), mutable.getZ(), Heightmap.Type.OCEAN_FLOOR_WG, region);
                    int s = height - 16;
                    Biome biome = region.getBiome(mutable.setY(surfaceHeight));
                    surfaceBuilder.buildSurface(region.getBiomeAccess(), region.getRegistryManager().get(Registry.BIOME_KEY), chunkGeneratorSettings.usesLegacyRandom(), heightContext, chunk, chunkNoiseSampler, chunkGeneratorSettings.getSurfaceRule());
                }
            }
        } else if (blockPos > -16 && blockPos < 15) {
            for (int xOffset = 0; xOffset < 16; ++xOffset) {
                for (int yOffset = 0; yOffset < 16; ++yOffset) {
                    int x = baseX + xOffset;
                    int z = baseZ + yOffset;
                    int surfaceHeight = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, xOffset, yOffset) + 1;
                    MultiNoiseUtil.NoiseValuePoint noise = this.noiseColumnSampler.sample((int) (x * d), (int) (z * d), (int) (xOffset * d));
                    mutable.set(x, -64, z);
                    int height = getHeight(mutable.getX(), mutable.getZ(), Heightmap.Type.OCEAN_FLOOR_WG, region);
                    int s = height - 16;
                    Biome biome = region.getBiome(mutable.setY(surfaceHeight));
                    surfaceBuilder.buildSurface(region.getBiomeAccess(), region.getRegistryManager().get(Registry.BIOME_KEY), chunkGeneratorSettings.usesLegacyRandom(), heightContext, chunk, chunkNoiseSampler, chunkGeneratorSettings.getSurfaceRule());
                }
            }
        } else if (blockPos >= 15) {
            for (int xOffset = 0; xOffset < 16; ++xOffset) {
                for (int yOffset = 0; yOffset < 16; ++yOffset) {
                    int x = baseX + xOffset;
                    int z = baseZ + yOffset;
                    int surfaceHeight = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, xOffset, yOffset) + 1;
                    MultiNoiseUtil.NoiseValuePoint noise = this.noiseColumnSampler.sample((int) (x * d), (int) (z * d), (int) (xOffset * d));
                    mutable.set(x, -64, z);
                    int height = getHeight(mutable.getX(), mutable.getZ(), Heightmap.Type.OCEAN_FLOOR_WG, region);
                    int s = height - 16;
                    Biome biome = region.getBiome(mutable.setY(surfaceHeight));
                    surfaceBuilder.buildSurface(region.getBiomeAccess(), region.getRegistryManager().get(Registry.BIOME_KEY), chunkGeneratorSettings.usesLegacyRandom(), heightContext, chunk, chunkNoiseSampler, chunkGeneratorSettings.getSurfaceRule());
                }
            }
        }
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, StructureAccessor accessor, Chunk chunk) {
        int blockPos = chunk.getPos().x * 15;
        if (blockPos <= -16) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            int baseX = chunk.getPos().getStartX();
            int baseZ = chunk.getPos().getStartZ();

            for (int xOffset = 0; xOffset < 16; xOffset++) {
                mutable.setX(xOffset);
                for (int zOffset = 0; zOffset < 16; zOffset++) {
                    mutable.setZ(zOffset);
                    int height = getHeight(baseX + xOffset, baseZ + zOffset, null, accessor.world);
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
                    int height = getHeight(baseX + xOffset, baseZ + zOffset, null, accessor.world);
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
                    int height = getHeight(baseX + xOffset, baseZ + zOffset, null, accessor.world);
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
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    public ChunkGenerator withSeed(long seed) {
        return new NoiseChunkGenerator(this.noiseRegistry, this.populationSource.withSeed(seed), seed, this.settings);
    }

    @Override
    public MultiNoiseUtil.MultiNoiseSampler getMultiNoiseSampler() {
        return this.noiseColumnSampler;
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver generationStep) {
    }

    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
        float noiseVal = ((noise.GetNoise(x, 65, z) + 1) / 2) * 30.0f;
        return (int) (noiseVal + 70);
    }

    public int getWorldHeight() {
        return this.settings.get().getGenerationShapeConfig().height();
    }

    public int getSeaLevel() {
        return this.settings.get().getSeaLevel();
    }

    public int getMinimumY() {
        return this.settings.get().getGenerationShapeConfig().minimumY();
    }

    public void populateEntities(ChunkRegion region) {
        ChunkPos chunkPos = region.getCenterPos();
        Biome biome = region.getBiome(chunkPos.getStartPos().withY(region.getTopY() - 1));
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed()));
        chunkRandom.setPopulationSeed(region.getSeed(), chunkPos.getStartX(), chunkPos.getStartZ());
        SpawnHelper.populateEntities(region, biome, chunkPos, chunkRandom);
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
        int k = Math.max((this.settings.get()).getGenerationShapeConfig().minimumY(), world.getBottomY());
        int l = Math.min((this.settings.get()).getGenerationShapeConfig().minimumY() + (this.settings.get()).getGenerationShapeConfig().height(), world.getTopY());
        int m = MathHelper.floorDiv(k, generationShapeConfig.verticalBlockSize());
        int n = MathHelper.floorDiv(l - k, generationShapeConfig.verticalBlockSize());
        if (n <= 0) {
            return new VerticalBlockSample(k, new BlockState[0]);
        }

        BlockState[] blocks = new BlockState[n * this.generationShapeConfig.verticalBlockSize()];
        int height = getHeight(x, z, null, world);
        int y1;
        for (y1 = k; y1 < height; y1++) {
            blocks[y1 - k] = defaultBlock;
        }
        for(; y1 < l; y1++){
            blocks[y1 - k] = defaultBlock;
        }
        return new VerticalBlockSample(k, blocks);
    }

    public long getSeed() {
        return seed;
    }

    public Supplier<ChunkGeneratorSettings> getSettings() {
        return settings;
    }
}
