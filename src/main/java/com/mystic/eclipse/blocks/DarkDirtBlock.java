package com.mystic.eclipse.blocks;

import com.mystic.eclipse.init.BlockInit;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.FlowerFeature;

import java.util.List;
import java.util.Random;

public class DarkDirtBlock extends Block {
    public DarkDirtBlock(FabricBlockSettings properties) {
        super(properties
                .sounds(BlockSoundGroup.ROOTED_DIRT)
                .breakByTool(FabricToolTags.SHOVELS, 1)
                .requiresTool()
                .strength(2.0F, 3.5F));
    }
}
