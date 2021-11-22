package com.mystic.eclipse;

import com.mystic.eclipse.creativetab.EclipseGroup;
import com.mystic.eclipse.init.BlockInit;
import com.mystic.eclipse.init.ItemInit;
import com.mystic.eclipse.mixin.GeneratorTypeAccessor;
import com.mystic.eclipse.utils.Reference;
import com.mystic.eclipse.worldgen.biomes.EclipseBiomeSource;
import com.mystic.eclipse.worldgen.chunkgenerators.SplitChunkGenerator;
import com.mystic.eclipse.worldgen.dimension.EclipseDimension;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
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
		EclipseGroup.init();
		EclipseDimension.registerBiomeSources();
		Registry.register(Registry.CHUNK_GENERATOR, "eclipse:eclipse", SplitChunkGenerator.CODEC);
		GeneratorTypeAccessor.getValues().add(SplitChunkGenerator.eclipse);
		EclipseDimension.setupSurfaceBuilders();
		EclipseDimension.init();
	}
}
