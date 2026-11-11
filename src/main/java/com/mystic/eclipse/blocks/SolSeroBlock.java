package com.mystic.eclipse.blocks;

import com.mystic.eclipse.init.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;

public class SolSeroBlock extends GrassBlock {
    public SolSeroBlock(BlockBehaviour.Properties properties) {
        super(properties
                .sound(SoundType.GRASS)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 3.5F)
                .randomTicks());
    }

    private static boolean canSeroSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos blockPos = pos.above();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.is(Blocks.SNOW) && blockState.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        } else if (blockState.getFluidState().getAmount() == 8) {
            return false;
        } else {
            int i = LightEngine.getLightBlockInto(world, state, pos, blockState, blockPos, Direction.UP, blockState.getLightBlock(world, blockPos));
            return i < world.getMaxLightLevel();
        }
    }

    private static boolean canSpread(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos blockPos = pos.above();
        return canSeroSurvive(state, world, pos) && !world.getFluidState(blockPos).is(FluidTags.WATER);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!canSeroSurvive(state, world, pos)) {
            world.setBlockAndUpdate(pos, BlockInit.SOL_HUMUS.get().defaultBlockState());
        } else {
            if (world.getMaxLocalRawBrightness(pos.above()) >= 0) {
                BlockState blockState = this.defaultBlockState();

                for (int i = 0; i < 4; ++i) {
                    BlockPos blockPos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                    if (world.getBlockState(blockPos).is(BlockInit.SOL_HUMUS.get()) && canSpread(blockState, world, blockPos)) {
                        world.setBlockAndUpdate(blockPos, BlockInit.SOL_SERO.get().defaultBlockState());
                    }
                }
            }
        }
    }
}
