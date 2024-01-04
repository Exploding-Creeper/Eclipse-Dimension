package com.mystic.eclipse;

import com.mystic.eclipse.creativetab.EclipseGroup;
import com.mystic.eclipse.init.BlockInit;
import com.mystic.eclipse.init.ItemInit;
import com.mystic.eclipse.utils.Reference;
import com.mystic.eclipse.worldgen.biomes.EclipseBiomeSource;
import com.mystic.eclipse.worldgen.chunkgenerators.SplitChunkGenerator;
import com.mystic.eclipse.worldgen.dimension.EclipseDimension;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EclipseMain implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger(Reference.MODID);

    public static Identifier id(String id) {
		return new Identifier(Reference.MODID, id);
	}

	@Override
	public void onInitialize() {
		BlockInit.init();
		ItemInit.init();
		Registry.register(Registries.ITEM_GROUP, id("eclipse"), EclipseGroup.owo);
		EclipseDimension.registerBiomeSources();
		Registry.register(Registries.CHUNK_GENERATOR, "eclipse:eclipse", SplitChunkGenerator.CODEC);
		EclipseDimension.init();
		ServerLifecycleEvents.SERVER_STARTING.register(EclipseBiomeSource::setupBiomeRegistry);
	}
}
