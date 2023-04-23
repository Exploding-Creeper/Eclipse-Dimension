package com.mystic.eclipse.worldgen.chunkgenerators;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mystic.eclipse.init.BlockInit;
import com.mystic.eclipse.utils.noise.FastNoiseLite;
import com.mystic.eclipse.worldgen.surfacebuilders.EclipseSurfaceBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.SimpleRandom;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SplitChunkGenerator extends ChunkGenerator {
    private final BlockState defaultBlock = BlockInit.DARK_STONE_BLOCK.getDefaultState();
    private final BlockState defaultBlockThree = BlockInit.TWILIGHT_STONE_BLOCK.getDefaultState();
    private final BlockState defaultBlockTwo = BlockInit.LIGHT_STONE_BLOCK.getDefaultState();
    private final long seed;
    protected final ChunkGeneratorSettings settings;
    private final SurfaceBuilder surfaceBuilder;
    private final NoiseRouter noiseRouter;
    private final Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry;
    private final Registry<StructureSet> structureSets;
    private FastNoiseLite noise;
    public static final GeneratorType eclipse = new GeneratorType("eclipse") {
        @Override
        protected ChunkGenerator getChunkGenerator(DynamicRegistryManager registryManager, long seed) {
            RegistryEntry<Biome> biomeRegistryEntry = RegistryEntry.of(registryManager.get(Registry.BIOME_KEY).get(BiomeKeys.BEACH));
            return new SplitChunkGenerator(registryManager.get(Registry.STRUCTURE_SET_KEY), registryManager.get(Registry.NOISE_WORLDGEN), new FixedBiomeSource(biomeRegistryEntry), seed, RegistryEntry.of(registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY).get(ChunkGeneratorSettings.OVERWORLD)));
        }
    };

    public static final Codec<SplitChunkGenerator> CODEC =
            RecordCodecBuilder.create(instance -> SplitChunkGenerator.method_41042(instance)
                    .and(instance.group(RegistryOps.createRegistryCodec(Registry.NOISE_WORLDGEN).forGetter(generator -> generator.noiseRegistry),
                            BiomeSource.CODEC.fieldOf("biome_source").forGetter(SplitChunkGenerator::getBiomeSource),
                            Codec.LONG.fieldOf("seed").forGetter(SplitChunkGenerator::getSeed),
                            ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(SplitChunkGenerator::getSettings))).apply((Applicative<NoiseChunkGenerator, ?>) instance,
                            instance.stable(SplitChunkGenerator::new)));

    public SplitChunkGenerator(Registry<StructureSet> noiseRegistry, Registry<DoublePerlinNoiseSampler.NoiseParameters> structuresRegistry, BiomeSource biomeSource, long seed, RegistryEntry<ChunkGeneratorSettings> settings) {
        this(noiseRegistry, structuresRegistry, biomeSource, biomeSource, seed, settings);
    }

    private SplitChunkGenerator(Registry<StructureSet> noiseRegistry, Registry<DoublePerlinNoiseSampler.NoiseParameters> structuresRegistry, BiomeSource populationSource, BiomeSource biomeSource, long seed, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(noiseRegistry, Optional.empty(), populationSource, biomeSource, seed);
        this.structureSets = noiseRegistry;
        this.noiseRegistry = structuresRegistry;
        this.seed = seed;
        this.settings = settings.value();
        ChunkGeneratorSettings chunkGeneratorSettings = this.settings;
        this.noiseRouter = chunkGeneratorSettings.method_41099(structuresRegistry, seed);
        int i = chunkGeneratorSettings.seaLevel();
        this.surfaceBuilder = new SurfaceBuilder(structuresRegistry, this.defaultBlock, i, seed, chunkGeneratorSettings.getRandomProvider());
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return new SplitChunkGenerator(structureSets, noiseRegistry, biomeSource, seed, RegistryEntry.of(settings));
    }

    @Override
    public MultiNoiseUtil.MultiNoiseSampler getMultiNoiseSampler() {
        return null;
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver generationStep) {

    }

    public int noise(int x, int z, double d) {
        return (int) this.settings.noiseRouter().depth().sample(new DensityFunction.NoisePos() {
            @Override
            public int blockX() {
                return (int) (x * d);
            }

            @Override
            public int blockY() {
                return 0;
            }

            @Override
            public int blockZ() {
                return (int) (z * d);
            }
        });
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;
        ChunkRandom chunkRandom = new ChunkRandom(new SimpleRandom(seed));
        Random random = new Random();
        chunkRandom.setCarverSeed(seed, chunkX, chunkZ);
        int baseX = chunkPos.getStartX();
        int baseZ = chunkPos.getStartZ();
        double d = 0.0625D;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int blockPos = chunk.getPos().x * 15;
        if(blockPos <= -16) {
            for (int xOffset = 0; xOffset < 16; ++xOffset) {
                for (int yOffset = 0; yOffset < 16; ++yOffset) {
                    int x = baseX + xOffset;
                    int z = baseZ + yOffset;
                    int surfaceHeight = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, xOffset, yOffset) + 1;
                    mutable.set(x, -64, z);
                    int noise = (int) (noise(x, z, d) * 15.0d);
                    Biome biome = region.getBiome(mutable.setY(surfaceHeight)).value();
                    EclipseSurfaceBuilder.buildSurface(random, chunk, biome, x, z, surfaceHeight, noise, this.defaultBlock, Blocks.WATER.getDefaultState(), BlockInit.DARK_GRASS_BLOCK.getDefaultState(), BlockInit.DARK_DIRT_BLOCK.getDefaultState(), BlockInit.DARK_DIRT_BLOCK.getDefaultState(), getSeaLevel());
                }
            }
        } else if (blockPos > -16 && blockPos < 15) {
            for (int xOffset = 0; xOffset < 16; ++xOffset) {
                for (int yOffset = 0; yOffset < 16; ++yOffset) {
                    int x = baseX + xOffset;
                    int z = baseZ + yOffset;
                    int surfaceHeight = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, xOffset, yOffset) + 1;
                    mutable.set(x, -64, z);
                    int noise = (int) (noise(x, z, d) * 15.0d);
                    Biome biome = region.getBiome(mutable.setY(surfaceHeight)).value();
                    EclipseSurfaceBuilder.buildSurface(random, chunk, biome, x, z, surfaceHeight, noise, this.defaultBlockThree, Blocks.WATER.getDefaultState(), BlockInit.TWILIGHT_GRASS_BLOCK.getDefaultState(), BlockInit.TWILIGHT_DIRT_BLOCK.getDefaultState(), BlockInit.TWILIGHT_DIRT_BLOCK.getDefaultState(), getSeaLevel());
                }
            }
        } else if (blockPos >= 15) {
            for (int xOffset = 0; xOffset < 16; ++xOffset) {
                for (int yOffset = 0; yOffset < 16; ++yOffset) {
                    int x = baseX + xOffset;
                    int z = baseZ + yOffset;
                    int surfaceHeight = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, xOffset, yOffset) + 1;
                    int noise = (int) (noise(x, z, d) * 15.0d);
                    mutable.set(x, -64, z);
                    Biome biome = region.getBiome(mutable.setY(surfaceHeight)).value();
                    EclipseSurfaceBuilder.buildSurface(random, chunk, biome, x, z, surfaceHeight, noise, this.defaultBlockTwo, Blocks.WATER.getDefaultState(), BlockInit.LIGHT_GRASS_BLOCK.getDefaultState(), BlockInit.LIGHT_DIRT_BLOCK.getDefaultState(), BlockInit.LIGHT_DIRT_BLOCK.getDefaultState(), getSeaLevel());
                }
            }
        }
    }

    @Override
    public void populateEntities(ChunkRegion region) {

    }

    @Override
    public int getWorldHeight() {
        return 384;
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
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinimumY() {
        return -64;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
        float noiseVal = ((noise.GetNoise(x, 65, z) + 1) / 2) * 30.0f;
        return (int) (noiseVal + 70);
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
        int k = Math.max((this.settings).generationShapeConfig().minimumY(), world.getBottomY());
        int l = Math.min((this.settings).generationShapeConfig().minimumY() + (this.settings).generationShapeConfig().height(), world.getTopY());
        int m = MathHelper.floorDiv(k, this.settings.generationShapeConfig().verticalBlockSize());
        int n = MathHelper.floorDiv(l - k, this.settings.generationShapeConfig().verticalBlockSize());

        if (n <= 0) {
            return new VerticalBlockSample(k, new BlockState[0]);
        }

        BlockState[] blocks = new BlockState[n * this.settings.generationShapeConfig().verticalBlockSize()];
        int height = getHeight(x, z, null, world);
        int y1;
        for (y1 = k; y1 < height; y1++) {
            blocks[y1 - k] = defaultBlock;
        }
        for (; y1 < l; y1++) {
            blocks[y1 - k] = defaultBlock;
        }
        return new VerticalBlockSample(k, blocks);
    }

    @Override
    public void getDebugHudText(List<String> text, BlockPos pos) {

    }

    public long getSeed() {
        return seed;
    }

    public RegistryEntry<ChunkGeneratorSettings> getSettings() {
        return RegistryEntry.of(settings);
    }
}
