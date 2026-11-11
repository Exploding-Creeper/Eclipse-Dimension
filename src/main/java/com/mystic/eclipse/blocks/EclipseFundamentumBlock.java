package com.mystic.eclipse.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class EclipseFundamentumBlock extends Block {
    public EclipseFundamentumBlock(BlockBehaviour.Properties properties) {
        super(properties.sound(SoundType.STONE).strength(-1.0F, 3600000.0F));
    }
}
