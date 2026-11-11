package com.mystic.eclipse.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SolHumusBlock extends Block {
    public SolHumusBlock(BlockBehaviour.Properties properties) {
        super(properties
                .sound(SoundType.ROOTED_DIRT)
                .requiresCorrectToolForDrops()
                .strength(2.0F, 3.5F));
    }
}
