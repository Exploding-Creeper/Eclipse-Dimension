package com.mystic.eclipse.worldgen.biomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mystic.eclipse.EclipseMain;
import com.mystic.eclipse.mixin.LayerAccessor;
import com.mystic.eclipse.utils.Reference;
import net.minecraft.SharedConstants;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.*;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.Map;
import java.util.function.LongFunction;
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
    private final BiomeLayerSampler BIOME_SAMPLER;
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
        this.BIOME_SAMPLER = buildWorldProcedure(seed, biomeSize, biomeRegistry);
        this.biomeSize = biomeSize;
        this.seed = seed;
        EclipseBiomeLayer.setSeed(seed);
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    public static BiomeLayerSampler buildWorldProcedure(long seed, int biomeSize, Registry<Biome> biomeRegistry) {
        LayerFactory<CachingLayerSampler> layerFactory = build((salt) ->
                        new CachingLayerContext(25, seed, salt),
                biomeSize,
                seed,
                biomeRegistry);
        return new BiomeLayerSampler(layerFactory);
    }

    public static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(long seed, ParentedLayer parent, LayerFactory<T> incomingArea, int count, LongFunction<C> contextFactory) {
        LayerFactory<T> IAreaFactory = incomingArea;

        for (int i = 0; i < count; ++i) {
            IAreaFactory = parent.create(contextFactory.apply(seed + (long) i), IAreaFactory);
        }

        return IAreaFactory;
    }

    public static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(LongFunction<C> contextFactory, int  biomeSize, long seed, Registry<Biome> biomeRegistry) {
        LayerFactory<T> layer = (new EclipseBiomeLayer(seed, biomeRegistry)).create(contextFactory.apply(200L));
        for(int currentExtraZoom = 0; currentExtraZoom < biomeSize; currentExtraZoom++){
            if((currentExtraZoom + 2) % 3 != 0){
                layer = ScaleLayer.NORMAL.create(contextFactory.apply(2001L + currentExtraZoom), layer);
            }
            else{
                layer= ScaleLayer.FUZZY.create(contextFactory.apply(2000L + (currentExtraZoom * 31L)), layer);
            }
        }
        return layer;
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return new EclipseBiomeSource(this.BIOME_REGISTRY, this.biomeSize, seed);
    }

    //Old Version of the code.
    /*public Biome sample(Registry<Biome> registry, int i, int j) {
        int k = ((LayerAccessor) this.BIOME_SAMPLER).getSampler().sample(i, j);
        Biome biome = registry.get(k);
        if (biome == null) {
            if (SharedConstants.isDevelopment) {
                throw Util.throwOrPause(new IllegalStateException("Unknown biome id: " + k));
            } else {
                return registry.get(BuiltinBiomes.fromRawId(0));
            }
        } else {
            return biome;
        }
    }*/

    //New Version of the code.
    public Biome sample(Registry<Biome> dynamicBiomeRegistry, int x, int z) {
        int resultBiomeID = ((LayerAccessor)this.BIOME_SAMPLER).getSampler().sample(x, z);
        Biome biome = dynamicBiomeRegistry.get(resultBiomeID);
        if (biome == null) {
            if (SharedConstants.isDevelopment) {
                throw Util.throwOrPause(new IllegalStateException("Unknown biome id: " + resultBiomeID));
            } else {
                // Spawn ocean if we can't resolve the biome from the layers.
                RegistryKey<Biome> backupBiomeKey = BiomeKeys.OCEAN;
                EclipseMain.LOGGER.warn("Unknown biome id: ${}. Will spawn ${} instead.", resultBiomeID, backupBiomeKey.getValue());
                return dynamicBiomeRegistry.get(backupBiomeKey);
            }
        } else {
            return biome;
        }
    }

    @Override
    public Biome getBiomeForNoiseGen(int x, int y, int z) {
        return this.sample(this.BIOME_REGISTRY, x, z);
    }
}
