package com.mystic.eclipse.worldgen.biomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.stream.Stream;

public class EclipseBiomeSource extends BiomeSource {
    public static final Codec<EclipseBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Biome.CODEC.fieldOf("nyx_umbra").forGetter(source -> source.dark),
            Biome.CODEC.fieldOf("lux_eclipso").forGetter(source -> source.twilight),
            Biome.CODEC.fieldOf("sol_lumi").forGetter(source -> source.light)
    ).apply(instance, EclipseBiomeSource::new));

    private final Holder<Biome> dark;
    private final Holder<Biome> twilight;
    private final Holder<Biome> light;

    public EclipseBiomeSource(Holder<Biome> dark, Holder<Biome> twilight, Holder<Biome> light) {
        this.dark = dark;
        this.twilight = twilight;
        this.light = light;
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.of(this.dark, this.twilight, this.light);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        int blockX = x << 2;
        int blockZ = z << 2;
        if (EclipseZones.isTwilightOasis(blockX, blockZ)) {
            return this.twilight;
        }
        return EclipseZones.isLightSide(blockX, blockZ) ? this.light : this.dark;
    }
}
