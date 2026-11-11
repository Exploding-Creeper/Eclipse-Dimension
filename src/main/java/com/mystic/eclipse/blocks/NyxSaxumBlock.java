package com.mystic.eclipse.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class NyxSaxumBlock extends Block {
    public NyxSaxumBlock(BlockBehaviour.Properties properties) {
        super(properties
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops()
                .strength(3.5F, 5.5F));
    }
}
