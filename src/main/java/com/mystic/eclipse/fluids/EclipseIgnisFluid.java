package com.mystic.eclipse.fluids;

import com.mystic.eclipse.blocks.EclipseArdorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Supplier;

public final class EclipseIgnisFluid {
    private EclipseIgnisFluid() {
    }

    public static class Source extends ForgeFlowingFluid.Source {
        private final Supplier<? extends Block> ardor;

        public Source(Properties properties, Supplier<? extends Block> ardor) {
            super(properties);
            this.ardor = ardor;
        }

        @Override
        public boolean isRandomlyTicking() {
            return true;
        }

        @Override
        public void randomTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
            ignite(level, pos, random, this.ardor);
        }
    }

    public static class Flowing extends ForgeFlowingFluid.Flowing {
        private final Supplier<? extends Block> ardor;

        public Flowing(Properties properties, Supplier<? extends Block> ardor) {
            super(properties);
            this.ardor = ardor;
        }

        @Override
        public boolean isRandomlyTicking() {
            return true;
        }

        @Override
        public void randomTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
            ignite(level, pos, random, this.ardor);
        }
    }

    private static void ignite(Level level, BlockPos pos, RandomSource random, Supplier<? extends Block> ardor) {
        if (!level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            return;
        }
        Block block = ardor.get();
        if (!(block instanceof EclipseArdorBlock ardorBlock)) {
            return;
        }
        for (Direction direction : Direction.values()) {
            if (random.nextInt(12) != 0) {
                continue;
            }
            BlockPos adjacent = pos.relative(direction);
            if (!level.getBlockState(adjacent).isAir()) {
                continue;
            }
            if (!ardorBlock.canSurvive(ardorBlock.defaultBlockState(), level, adjacent)) {
                continue;
            }
            level.setBlock(adjacent, ardorBlock.shaped(level, adjacent), 3);
        }
    }
}
