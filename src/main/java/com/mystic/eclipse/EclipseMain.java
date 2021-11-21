package com.mystic.eclipse;

import com.mystic.eclipse.creativetab.EclipseGroup;
import com.mystic.eclipse.init.BlockInit;
import com.mystic.eclipse.init.ItemInit;
import com.mystic.eclipse.mixin.GeneratorTypeAccessor;
import com.mystic.eclipse.utils.Reference;
import com.mystic.eclipse.worldgen.chunkgenerators.SplitChunkGenerator;
import com.mystic.eclipse.worldgen.dimension.EclipseDimension;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EclipseMain implements ModInitializer {

	public static Identifier id(String id) {
		return new Identifier(Reference.MODID, id);
	}

	@Override
	public void onInitialize() {
		BlockInit.init();
		ItemInit.init();
		EclipseGroup.init();
		Registry.register(Registry.CHUNK_GENERATOR, "eclipse:eclipse", SplitChunkGenerator.CODEC);
		GeneratorTypeAccessor.getValues().add(SplitChunkGenerator.eclipse);
		EclipseDimension.setupSurfaceBuilders();
		EclipseDimension.init();
	}
}
