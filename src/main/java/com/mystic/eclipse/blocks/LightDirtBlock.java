package com.mystic.eclipse.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.sound.BlockSoundGroup;

public class LightDirtBlock extends Block {
    public LightDirtBlock(FabricBlockSettings properties) {
        super(properties
                .sounds(BlockSoundGroup.ROOTED_DIRT)
               // .breakByTool(FabricToolTags.SHOVELS, 1)
                .requiresTool()
                .strength(2.0F, 3.5F));
    }
}
