package com.mystic.eclipse.worldgen.dimension;

import com.mystic.eclipse.EclipseMain;
import com.mystic.eclipse.utils.Reference;
import com.mystic.eclipse.worldgen.biomes.EclipseBiomeSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EclipseDimension {
    public static final ResourceKey<Level> ECLIPSE_WORLD = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("eclipse", "eclipsed"));
    public static final ResourceKey<DimensionType> ECLIPSE_DIMENSION_TYPE_KEY = ResourceKey.create(Registries.DIMENSION_TYPE, ResourceLocation.fromNamespaceAndPath("eclipse", "eclipsed"));

    public static final DeferredRegister<com.mojang.serialization.Codec<? extends BiomeSource>> BIOME_SOURCES = DeferredRegister.create(Registries.BIOME_SOURCE, Reference.MODID);
    public static final RegistryObject<com.mojang.serialization.Codec<EclipseBiomeSource>> ECLIPSE_BIOME_SOURCE = BIOME_SOURCES.register("eclipse_biome_source", () -> EclipseBiomeSource.CODEC);

    public static DimensionType ECLIPSE_TYPE;
    public static ServerLevel ECLIPSE_DIMENSION;

    public static boolean isEclipseDimension(Level world) {
        return world != null && world.dimension().equals(ECLIPSE_WORLD);
    }

    public static boolean isEclipseDimension(ResourceKey<Level> worldRegistryKey) {
        return worldRegistryKey != null && worldRegistryKey.equals(ECLIPSE_WORLD);
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        EclipseDimension.ECLIPSE_TYPE = server.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE).get(ECLIPSE_DIMENSION_TYPE_KEY);
        EclipseDimension.ECLIPSE_DIMENSION = server.getLevel(ECLIPSE_WORLD);
        if (ECLIPSE_DIMENSION != null) {
            EclipseMain.LOGGER.info("Eclipse dimension chunk generator: {}", ECLIPSE_DIMENSION.getChunkSource().getGenerator().getClass().getName());
        }
    }
}
