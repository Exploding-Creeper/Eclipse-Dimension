package com.mystic.eclipse.worldgen.dimension;

import com.mystic.eclipse.utils.Reference;
import com.mystic.eclipse.worldgen.biomes.EclipseBiomeSource;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class EclipseDimension {
    public static final RegistryKey<World> ECLIPSE_WORLD = RegistryKey.of(RegistryKeys.WORLD, new Identifier("eclipse:eclipsed"));
    public static final RegistryKey<DimensionType> ECLIPSE_DIMENSION_TYPE_KEY = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier("eclipse:eclipsed"));


    public static DimensionType ECLIPSE_TYPE;
    public static ServerWorld ECLIPSE_DIMENSION;

    public static boolean isEclipseDimension(World world) {
        return world != null && world.getRegistryKey().equals(ECLIPSE_WORLD);
    }

    public static boolean isEclipseDimension(RegistryKey<World> worldRegistryKey) {
        return worldRegistryKey != null && worldRegistryKey.equals(ECLIPSE_WORLD);
    }

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            EclipseDimension.ECLIPSE_TYPE = server.getRegistryManager().get(RegistryKeys.DIMENSION_TYPE).get(ECLIPSE_DIMENSION_TYPE_KEY);
            EclipseDimension.ECLIPSE_DIMENSION = server.getWorld(ECLIPSE_WORLD);
        });
    }

    public static void registerBiomeSources() {
        Registry.register(Registries.BIOME_SOURCE, new Identifier(Reference.MODID, "eclipse_biome_source"), EclipseBiomeSource.CODEC);
    }
}
