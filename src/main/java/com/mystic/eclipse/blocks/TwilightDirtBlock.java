package com.mystic.eclipse.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.SpreadableBlock;
import net.minecraft.sound.BlockSoundGroup;

public class TwilightDirtBlock extends SpreadableBlock {
    public TwilightDirtBlock(FabricBlockSettings properties) {
        super(properties
                .sounds(BlockSoundGroup.ROOTED_DIRT)
                //.breakByTool(FabricToolTags.SHOVELS, 1)
                .requiresTool()
                .strength(2.0F, 3.5F));
    }
}
