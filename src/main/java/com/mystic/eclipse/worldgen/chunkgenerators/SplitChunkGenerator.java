package com.mystic.eclipse.worldgen.chunkgenerators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.handler.ssl.JdkAlpnApplicationProtocolNegotiator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class SplitChunkGenerator extends ChunkGenerator{

    private final long seed;
    private final Supplier<ChunkGeneratorSettings> settings;
    private final int horizontalNoiseResolution, verticalNoiseResolution;
    private final BlockState defaultBlock = Blocks.BLACKSTONE.getDefaultState(); //TODO Replace with Dark Stone Blocks
    private final BlockState defaultBlockTwo = Blocks.BONE_BLOCK.getDefaultState(); //TODO Replace with Light Stone Blocks
    private final GenerationShapeConfig generationShapeConfig;
    private final double densityFactor, densityOffset;
    private final NoiseSampler surfaceDepthNoise;
    public static final GeneratorType eclipse = new GeneratorType("eclipse") {
        @Override
        protected ChunkGenerator getChunkGenerator(Registry<Biome> biomeRegistry, Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry, long seed) {
            BiomeSource removeLater = new VanillaLayeredBiomeSource(seed, false, false, biomeRegistry);
            return new SplitChunkGenerator(removeLater, seed, () -> chunkGeneratorSettingsRegistry.get(ChunkGeneratorSettings.OVERWORLD));
        }
    };

    private final DoublePerlinNoiseSampler field_34344 = DoublePerlinNoiseSampler.create(new ChunkRandom(42L), -16, new double[] { 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D });

    public static final Codec<SplitChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(SplitChunkGenerator::getBiomeSource),
                    Codec.LONG.fieldOf("seed").forGetter(SplitChunkGenerator::getSeed),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(SplitChunkGenerator::getSettings)
            ).apply(instance, instance.stable(SplitChunkGenerator::new))
    );

    public SplitChunkGenerator(BiomeSource biomeSource, long seed, Supplier<ChunkGeneratorSettings> settings){
        super(biomeSource, biomeSource, settings.get().getStructuresConfig(), seed);

        this.seed = seed;
        this.settings = settings;

        ChunkGeneratorSettings settingsInstance = settings.get();
        generationShapeConfig = settingsInstance.getGenerationShapeConfig();

        horizontalNoiseResolution = BiomeCoords.toBlock(generationShapeConfig.getSizeVertical());
        verticalNoiseResolution = BiomeCoords.toBlock(generationShapeConfig.getSizeHorizontal());

        densityFactor = generationShapeConfig.getDensityFactor();
        densityOffset = generationShapeConfig.getDensityOffset();

        ChunkRandom chunkRandom = new ChunkRandom(seed);

        this.surfaceDepthNoise = (NoiseSampler)(generationShapeConfig.hasSimplexSurfaceNoise() ? new OctaveSimplexNoiseSampler(chunkRandom, IntStream.rangeClosed(-3, 0)) : new OctavePerlinNoiseSampler(chunkRandom, IntStream.rangeClosed(-3, 0)));
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return new NoiseChunkGenerator(biomeSource.withSeed(seed), seed, this.settings);
    }

    @Override
    public void buildSurface(ChunkRegion region, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;
        ChunkRandom chunkRandom = new ChunkRandom();
        chunkRandom.setTerrainSeed(chunkX, chunkZ);
        int baseX = chunkPos.getStartX();
        int baseZ = chunkPos.getStartZ();
        double d = 0.0625D;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int xOffset = 0; xOffset < 16; ++xOffset) {
            for (int yOffset = 0; yOffset < 16; ++yOffset) {
                int x = baseX + xOffset;
                int z = baseZ + yOffset;
                int surfaceHeight = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, xOffset, yOffset) + 1;
                double noise = this.surfaceDepthNoise.sample((double) x * d, (double) z * d, d, (double) xOffset * d) * 15.0D;
                mutable.set(x, -64, z);
                int height = getHeight(mutable.getX(), mutable.getZ(), Heightmap.Type.OCEAN_FLOOR_WG, region);
                int s = height - 16;
                Biome biome = region.getBiome(mutable.setY(surfaceHeight));
                biome.buildSurface(chunkRandom, chunk, x, z, surfaceHeight, noise, this.defaultBlock, Blocks.WATER.getDefaultState(), this.getSeaLevel(), s, region.getSeed());
            }
        }
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk) {
        if ((chunk.getPos().x) < 0) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            Heightmap heightmapOne = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
            Heightmap heightmapTwo = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);

            int baseX = chunk.getPos().getStartX();
            int baseZ = chunk.getPos().getStartZ();

            for(int xOffset = 0; xOffset < 16; xOffset++){
                mutable.setX(xOffset);
                for(int zOffset = 0; zOffset < 16; zOffset++){
                    mutable.setZ(zOffset);
                    int height = getHeight(baseX + xOffset, baseZ + zOffset, null, accessor.world);
                    for(int y = chunk.getBottomY(); y < height; y++){
                        mutable.setY(y);
                        chunk.setBlockState(mutable, defaultBlock, false);
                    }
                }
            }

            return CompletableFuture.completedFuture(chunk);
        }  else {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            Heightmap heightmapOne = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
            Heightmap heightmapTwo = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);

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
        }
    }

    @Override
    public int getHeight(int actualX, int actualZ, Heightmap.Type heightmap, HeightLimitView world) {
        return 70; //TODO make in noisy!!!
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
        int k = Math.max((this.settings.get()).getGenerationShapeConfig().getMinimumY(), world.getBottomY());
        int l = Math.min((this.settings.get()).getGenerationShapeConfig().getMinimumY() + (this.settings.get()).getGenerationShapeConfig().getHeight(), world.getTopY());
        int m = MathHelper.floorDiv(k, this.verticalNoiseResolution);
        int n = MathHelper.floorDiv(l - k, this.verticalNoiseResolution);

        if(n <= 0){
            return new VerticalBlockSample(k, new BlockState[0]);
        }

        BlockState[] blocks = new BlockState[n * this.verticalNoiseResolution];
        int height = getHeight(x, z, null, world);
        int y1;
        for(y1 = k; y1 < height; y1++){
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
