package com.mystic.eclipse.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LuxSaxumBlock extends Block {
    public LuxSaxumBlock(BlockBehaviour.Properties properties) {
        super(properties
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops()
                .strength(2.0F, 5.0F));
    }
}
