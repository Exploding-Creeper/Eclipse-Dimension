package com.mystic.eclipse.worldgen.dimension;

import com.mystic.eclipse.utils.Reference;
import com.mystic.eclipse.worldgen.biomes.EclipseBiomeSource;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class EclipseDimension {
    public static final RegistryKey<World> ECLIPSE_WORLD = RegistryKey.of(Registry.WORLD_KEY, new Identifier("eclipse:eclipsed"));
    public static final RegistryKey<DimensionType> ECLIPSE_DIMENSION_TYPE_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("eclipse:eclipsed"));


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
            EclipseDimension.ECLIPSE_TYPE = server.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).get(ECLIPSE_DIMENSION_TYPE_KEY);
            EclipseDimension.ECLIPSE_DIMENSION = server.getWorld(ECLIPSE_WORLD);
        });
    }

    public static void registerBiomeSources() {
        Registry.register(Registry.BIOME_SOURCE, new Identifier(Reference.MODID, "eclipse_biome_source"), EclipseBiomeSource.CODEC);
    }
}
