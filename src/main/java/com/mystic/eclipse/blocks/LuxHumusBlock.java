package com.mystic.eclipse.blocks;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LuxHumusBlock extends SpreadingSnowyDirtBlock {
    public LuxHumusBlock(BlockBehaviour.Properties properties) {
        super(properties
                .sound(SoundType.ROOTED_DIRT)
                .requiresCorrectToolForDrops()
                .strength(2.0F, 3.5F));
    }
}
