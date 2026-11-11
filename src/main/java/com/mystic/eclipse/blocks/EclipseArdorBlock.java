package com.mystic.eclipse.blocks;

import com.mystic.eclipse.world.EclipsePlacement;
import com.mystic.eclipse.worldgen.dimension.EclipseDimension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class EclipseArdorBlock extends BaseFireBlock {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;

    public EclipseArdorBlock(int light) {
        super(BlockBehaviourProperties(light), 1.0F);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false));
    }

    private static Properties BlockBehaviourProperties(int light) {
        return Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .replaceable()
                .noCollission()
                .instabreak()
                .lightLevel(state -> light)
                .sound(SoundType.WOOL)
                .noLootTable()
                .pushReaction(PushReaction.DESTROY)
                .randomTicks();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!EclipseDimension.isEclipseDimension(context.getLevel())) {
            EclipsePlacement.voidParticles(context.getLevel(), context.getClickedPos());
            return null;
        }
        return this.shaped(context.getLevel(), context.getClickedPos());
    }

    public BlockState shaped(BlockGetter level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState floor = level.getBlockState(below);
        boolean supported = floor.isFaceSturdy(level, below, Direction.UP);
        return this.defaultBlockState()
                .setValue(NORTH, !supported && this.canBurn(level.getBlockState(pos.north())))
                .setValue(EAST, !supported && this.canBurn(level.getBlockState(pos.east())))
                .setValue(SOUTH, !supported && this.canBurn(level.getBlockState(pos.south())))
                .setValue(WEST, !supported && this.canBurn(level.getBlockState(pos.west())))
                .setValue(UP, !supported && this.canBurn(level.getBlockState(pos.above())));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (level instanceof Level world && !EclipseDimension.isEclipseDimension(world)) {
            return false;
        }
        BlockPos below = pos.below();
        BlockState floor = level.getBlockState(below);
        return floor.isFaceSturdy(level, below, Direction.UP) || this.touchesFuel(level, pos);
    }

    @Override
    public void randomTick(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos) || (level.isRainingAt(pos) && random.nextBoolean())) {
            level.removeBlock(pos, false);
            return;
        }
        boolean fuel = this.touchesFuel(level, pos);
        if (random.nextInt(fuel ? 14 : 4) == 0) {
            level.removeBlock(pos, false);
            return;
        }
        if (!fuel || random.nextInt(4) != 0) {
            return;
        }
        for (int attempt = 0; attempt < 4; attempt++) {
            BlockPos next = pos.offset(random.nextInt(3) - 1, random.nextInt(2), random.nextInt(3) - 1);
            if (level.getBlockState(next).isAir() && this.canSurvive(this.defaultBlockState(), level, next) && this.touchesFuel(level, next)) {
                level.setBlock(next, this.shaped(level, next), 3);
                return;
            }
        }
    }

    @Override
    protected boolean canBurn(BlockState state) {
        return state.ignitedByLava();
    }

    private boolean touchesFuel(LevelReader level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (this.canBurn(level.getBlockState(pos.relative(direction)))) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP);
    }
}
