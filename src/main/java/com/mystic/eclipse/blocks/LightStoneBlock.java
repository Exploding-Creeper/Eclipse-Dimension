package com.mystic.eclipse.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.sound.BlockSoundGroup;

public class LightStoneBlock extends Block {
    public LightStoneBlock(FabricBlockSettings properties) {
        super(properties
                .sounds(BlockSoundGroup.STONE)
                .breakByTool(FabricToolTags.PICKAXES, 1)
                .requiresTool()
                .strength(2.0F, 4.0F));

    }
}
