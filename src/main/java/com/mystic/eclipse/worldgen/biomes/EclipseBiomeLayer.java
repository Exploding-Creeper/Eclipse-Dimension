package com.mystic.eclipse.worldgen.biomes;

import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.gen.ChunkRandom;

import java.util.stream.IntStream;

public class EclipseBiomeLayer implements InitLayer {

    private final Registry<Biome> dynamicRegistry;
    private static OctaveSimplexNoiseSampler perlinGen;

    public EclipseBiomeLayer(long seed, Registry<Biome> dynamicRegistry){
        this.dynamicRegistry = dynamicRegistry;

        if (perlinGen == null)
        {
            ChunkRandom sharedseedrandom = new ChunkRandom(seed);
            perlinGen = new OctaveSimplexNoiseSampler(sharedseedrandom, IntStream.rangeClosed(0, 0));
        }
    }

    public int sample(LayerRandomnessSource noise, int x, int z) {
        double perlinNoise = perlinGen.sample(x * 0.055D, z * 0.055D, false);
        int blockPos = x * 15;
        if(blockPos <= -16) {
            if (perlinNoise > 0.30) {
                return this.dynamicRegistry.getRawId(this.dynamicRegistry.get(EclipseBiomeSource.DESOLATE_CITY_BIOME));
            } else if (perlinNoise > 0.20 && perlinNoise < 0.30) {
                return this.dynamicRegistry.getRawId(this.dynamicRegistry.get(EclipseBiomeSource.DARK_STORMY_BIOME));
            } else if (perlinNoise > 0.10 && perlinNoise < 0.20) {
                return this.dynamicRegistry.getRawId(this.dynamicRegistry.get(EclipseBiomeSource.NUCLEAR_WASTE_BIOME));
            } else {
                return this.dynamicRegistry.getRawId(this.dynamicRegistry.get(EclipseBiomeSource.DARK_CAVE_BIOME));
            }
        } else if (blockPos > -16 && blockPos < 15) {
            return this.dynamicRegistry.getRawId(this.dynamicRegistry.get(EclipseBiomeSource.TWILIGHT_ZONE_BIOME));
        } else if (blockPos >= 15) {
            if (perlinNoise > 0.30) {
                return this.dynamicRegistry.getRawId(this.dynamicRegistry.get(EclipseBiomeSource.SKY_CITY_BIOME));
            } else if (perlinNoise > 0.20 && perlinNoise < 0.30) {
                return this.dynamicRegistry.getRawId(this.dynamicRegistry.get(EclipseBiomeSource.WHITE_OASIS_BIOME));
            } else if (perlinNoise > 0.10 && perlinNoise < 0.20) {
                return this.dynamicRegistry.getRawId(this.dynamicRegistry.get(EclipseBiomeSource.GARDEN_BIOME));
            } else {
                return this.dynamicRegistry.getRawId(this.dynamicRegistry.get(EclipseBiomeSource.CLOUD_BIOME));
            }
        } else {
            //Do nothing biome here!
            return this.dynamicRegistry.getRawId(this.dynamicRegistry.get(EclipseBiomeSource.TWILIGHT_ZONE_BIOME));
        }
    }

    public static void setSeed(long seed) {
        ChunkRandom sharedseedrandom = new ChunkRandom(seed);
    }
}