package com.mystic.eclipse.worldgen.trees;

import com.mystic.eclipse.utils.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class EclipseTreeGrower extends AbstractTreeGrower {
    public static final ResourceKey<ConfiguredFeature<?, ?>> NYX_TREE = feature("nyx_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SOL_TREE = feature("sol_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LUX_TREE = feature("lux_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NYX_GREAT_TREE = feature("nyx_great_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SOL_GREAT_TREE = feature("sol_great_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LUX_GREAT_TREE = feature("lux_great_tree");

    public static final EclipseTreeGrower NYX_UMBRA = new EclipseTreeGrower(NYX_TREE, NYX_GREAT_TREE);
    public static final EclipseTreeGrower SOL_LUMI = new EclipseTreeGrower(SOL_TREE, SOL_GREAT_TREE);
    public static final EclipseTreeGrower LUX_ECLIPSO = new EclipseTreeGrower(LUX_TREE, LUX_GREAT_TREE);

    private final ResourceKey<ConfiguredFeature<?, ?>> feature;
    private final ResourceKey<ConfiguredFeature<?, ?>> great;

    private EclipseTreeGrower(ResourceKey<ConfiguredFeature<?, ?>> feature, ResourceKey<ConfiguredFeature<?, ?>> great) {
        this.feature = feature;
        this.great = great;
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> feature(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Reference.MODID, name));
    }

    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
        if (random.nextInt(3) == 0) {
            return this.great;
        }
        return this.feature;
    }
}
