package com.mystic.eclipse.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.sound.BlockSoundGroup;

public class DarkStoneBlock extends Block {
    public DarkStoneBlock(FabricBlockSettings properties) {
        super(properties
                .sounds(BlockSoundGroup.STONE)
                //.breakByTool(FabricToolTags.PICKAXES, 1)
                .requiresTool()
                .strength(3.5F, 5.5F));
    }
}
