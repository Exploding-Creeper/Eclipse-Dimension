package com.mystic.eclipse.worldgen.surfacebuilders;

import com.mystic.eclipse.utils.Reference;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class EclipseSurfaceBuilderRegistery {
    public static final SurfaceBuilder<TernarySurfaceConfig> ECLIPSE_SURFACE_BUILDER = new EclipseSurfaceBuilder(TernarySurfaceConfig.CODEC);

    public static void registerSurfaceBuilders() {
        Registry.register(Registry.SURFACE_BUILDER, new Identifier(Reference.MODID, "eclipse_surface_builder"), ECLIPSE_SURFACE_BUILDER);
    }
}
