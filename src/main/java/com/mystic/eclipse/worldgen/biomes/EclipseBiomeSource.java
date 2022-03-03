package com.mystic.eclipse.worldgen.biomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mystic.eclipse.utils.Reference;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.Map;
import java.util.stream.Collectors;

public class EclipseBiomeSource extends BiomeSource {
    public static final Codec<EclipseBiomeSource> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter((biomeSource) ->
                    biomeSource.BIOME_REGISTRY), Codec.intRange(1, 20).fieldOf("biome_size").orElse(2).forGetter((biomeSource) ->
                    biomeSource.biomeSize), Codec.LONG.fieldOf("seed").stable().forGetter((biomeSource) ->
                    biomeSource.seed)).apply(instance, instance.stable(EclipseBiomeSource::new)));

    public static final Identifier TWILIGHT_ZONE_BIOME = new Identifier(Reference.MODID, "twilight_zone_biome");
    public static final Identifier NUCLEAR_WASTE_BIOME = new Identifier(Reference.MODID, "nuclear_waste_biome");
    public static final Identifier DESOLATE_CITY_BIOME = new Identifier(Reference.MODID, "desolate_city_biome");
    public static final Identifier DARK_STORMY_BIOME = new Identifier(Reference.MODID, "dark_stormy_biome");
    public static final Identifier DARK_CAVE_BIOME = new Identifier(Reference.MODID, "dark_cave_biome");
    public static final Identifier SKY_CITY_BIOME = new Identifier(Reference.MODID, "sky_city_biome");
    public static final Identifier WHITE_OASIS_BIOME = new Identifier(Reference.MODID, "white_oasis_biome");
    public static final Identifier GARDEN_BIOME = new Identifier(Reference.MODID, "garden_biome");
    public static final Identifier CLOUD_BIOME = new Identifier(Reference.MODID, "cloud_biome");
    private final Registry<Biome> BIOME_REGISTRY;
    public static Registry<Biome> LAYERS_BIOME_REGISTRY;
    private final long seed;
    private final int biomeSize;

    protected EclipseBiomeSource(Registry<Biome> biomeRegistry, int biomeSize, long seed) {
        super(biomeRegistry.getEntries().stream()
                .filter(entry -> entry.getKey().getValue().getNamespace().equals(Reference.MODID))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList()));
        this.BIOME_REGISTRY = biomeRegistry;
        EclipseBiomeSource.LAYERS_BIOME_REGISTRY = biomeRegistry;
        this.biomeSize = biomeSize;
        this.seed = seed;
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return new EclipseBiomeSource(this.BIOME_REGISTRY, this.biomeSize, seed);
    }

    @Override
    public Biome getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        int blockPos = x * 15;
        if(blockPos <= -16) {
            if (noise.sample(x, y, z).weirdnessNoise() > 0.30) {
                return BIOME_REGISTRY.get(EclipseBiomeSource.DESOLATE_CITY_BIOME);
            } else if (noise.sample(x, y, z).weirdnessNoise() > 0.20 && noise.sample(x, y, z).weirdnessNoise() < 0.30) {
                return BIOME_REGISTRY.get(EclipseBiomeSource.DARK_STORMY_BIOME);
            } else if (noise.sample(x, y, z).weirdnessNoise() > 0.10 && noise.sample(x, y, z).weirdnessNoise() < 0.20) {
                return BIOME_REGISTRY.get(EclipseBiomeSource.NUCLEAR_WASTE_BIOME);
            } else {
                return BIOME_REGISTRY.get(EclipseBiomeSource.DARK_CAVE_BIOME);
            }
        } else if (blockPos > -16 && blockPos < 15) {
            return BIOME_REGISTRY.get(EclipseBiomeSource.TWILIGHT_ZONE_BIOME);
        } else if (blockPos >= 15) {
            if (noise.sample(x, y, z).weirdnessNoise() > 0.30) {
                return BIOME_REGISTRY.get(EclipseBiomeSource.SKY_CITY_BIOME);
            } else if (noise.sample(x, y, z).weirdnessNoise() > 0.20 && noise.sample(x, y, z).weirdnessNoise() < 0.30) {
                return BIOME_REGISTRY.get(EclipseBiomeSource.WHITE_OASIS_BIOME);
            } else if (noise.sample(x, y, z).weirdnessNoise() > 0.10 && noise.sample(x, y, z).weirdnessNoise() < 0.20) {
                return BIOME_REGISTRY.get(EclipseBiomeSource.GARDEN_BIOME);
            } else {
                return BIOME_REGISTRY.get(EclipseBiomeSource.CLOUD_BIOME);
            }
        } else {
            //Do nothing biome here!
            return BIOME_REGISTRY.get(EclipseBiomeSource.TWILIGHT_ZONE_BIOME);
        }
    }
}
