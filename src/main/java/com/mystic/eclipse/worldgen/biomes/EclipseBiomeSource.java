package com.mystic.eclipse.worldgen.biomes;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mystic.eclipse.utils.Reference;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.stream.Stream;

public class EclipseBiomeSource extends BiomeSource {
    public static final Codec<EclipseBiomeSource> CODEC = Codec.unit(EclipseBiomeSource::new);

    public static final Identifier TWILIGHT_ZONE_BIOME = new Identifier(Reference.MODID, "twilight_zone_biome");
    public static final Identifier NUCLEAR_WASTE_BIOME = new Identifier(Reference.MODID, "nuclear_waste_biome");
    public static final Identifier DESOLATE_CITY_BIOME = new Identifier(Reference.MODID, "desolate_city_biome");
    public static final Identifier DARK_STORMY_BIOME = new Identifier(Reference.MODID, "dark_stormy_biome");
    public static final Identifier DARK_CAVE_BIOME = new Identifier(Reference.MODID, "dark_cave_biome");
    public static final Identifier SKY_CITY_BIOME = new Identifier(Reference.MODID, "sky_city_biome");
    public static final Identifier WHITE_OASIS_BIOME = new Identifier(Reference.MODID, "white_oasis_biome");
    public static final Identifier GARDEN_BIOME = new Identifier(Reference.MODID, "garden_biome");
    public static final Identifier CLOUD_BIOME = new Identifier(Reference.MODID, "cloud_biome");
    public static Registry<Biome> BIOME_REGISTRY;

    public EclipseBiomeSource() {
        super();
    }

    public static void setupBiomeRegistry(MinecraftServer server) {
        BIOME_REGISTRY = server.getRegistryManager().get(RegistryKeys.BIOME);
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    protected Stream<RegistryEntry<Biome>> biomeStream() {
        return Stream.of(RegistryEntry.of(BIOME_REGISTRY.stream().reduce(
                (resourceLocation, resourceLocation2) -> resourceLocation).orElse(BIOME_REGISTRY.get(BiomeKeys.THE_VOID))
        ));
    }

    private static RegistryEntry<Biome> getHolderBiome(Identifier resourceLocationBiome) {
        return RegistryEntry.of(BIOME_REGISTRY.get(resourceLocationBiome)); //return ocean if check fail
    }
    
    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler perlinNoise) {
        int blockPos = x * 15;
        if(blockPos <= -16) {
            if (perlinNoise.sample(x, y, z).temperatureNoise() > 0.30) {
                return getHolderBiome(EclipseBiomeSource.DESOLATE_CITY_BIOME);
            } else if (perlinNoise.sample(x, y, z).temperatureNoise() > 0.20 && perlinNoise.sample(x, y, z).temperatureNoise() < 0.30) {
                return getHolderBiome(EclipseBiomeSource.DARK_STORMY_BIOME);
            } else if (perlinNoise.sample(x, y, z).temperatureNoise() > 0.10 && perlinNoise.sample(x, y, z).temperatureNoise() < 0.20) {
                return getHolderBiome(EclipseBiomeSource.NUCLEAR_WASTE_BIOME);
            } else {
                return getHolderBiome(EclipseBiomeSource.DARK_CAVE_BIOME);
            }
        } else if (blockPos > -16 && blockPos < 15) {
            return getHolderBiome(EclipseBiomeSource.TWILIGHT_ZONE_BIOME);
        } else if (blockPos >= 15) {
            if (perlinNoise.sample(x, y, z).temperatureNoise() > 0.30) {
                return getHolderBiome(EclipseBiomeSource.SKY_CITY_BIOME);
            } else if (perlinNoise.sample(x, y, z).temperatureNoise() > 0.20 && perlinNoise.sample(x, y, z).temperatureNoise() < 0.30) {
                return getHolderBiome(EclipseBiomeSource.WHITE_OASIS_BIOME);
            } else if (perlinNoise.sample(x, y, z).temperatureNoise() > 0.10 && perlinNoise.sample(x, y, z).temperatureNoise() < 0.20) {
                return getHolderBiome(EclipseBiomeSource.GARDEN_BIOME);
            } else {
                return getHolderBiome(EclipseBiomeSource.CLOUD_BIOME);
            }
        } else {
            //Do nothing biome here!
            return getHolderBiome(EclipseBiomeSource.TWILIGHT_ZONE_BIOME);
        }
    }
}
