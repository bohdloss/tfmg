package it.bohdloss.tfmg.content.machinery.metallurgy.blast_furnace.reinforcement;

import com.mojang.serialization.MapCodec;
import it.bohdloss.tfmg.registry.TFMGBlocks;
import it.bohdloss.tfmg.registry.TFMGShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BlastFurnaceReinforcementWallBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<BlastFurnaceReinforcementWallBlock> CODEC = simpleCodec(BlastFurnaceReinforcementWallBlock::new);

    public BlastFurnaceReinforcementWallBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public void onPlace(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState blockState1, boolean b) {
        changeFireproofBricks(level, pos, blockState.getValue(FACING).getOpposite(), true);
        super.onPlace(blockState, level, pos, blockState1, b);
    }

    @Override
    public void onRemove(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState blockState1, boolean b) {
        changeFireproofBricks(level, pos, blockState.getValue(FACING).getOpposite(), false);
        super.onRemove(blockState, level, pos, blockState1, b);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter level, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return TFMGShapes.BLAST_FURNACE_REINFORCEMENT_WALL.get(blockState.getValue(FACING));
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos neighbor, boolean b) {
        BlockState stateBehind = level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()));

        if(stateBehind.is(TFMGBlocks.FIREPROOF_BRICKS.get())) {
            changeFireproofBricks(level, pos, state.getValue(FACING).getOpposite(), true);
        }

        super.neighborChanged(state, level, pos, block, neighbor, b);
    }

    @Override
    public void onNeighborChange(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockPos neighbor) {
        BlockState stateBehind = level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()));

        if(stateBehind.is(TFMGBlocks.FIREPROOF_BRICKS.get())) {
            changeFireproofBricks((Level) level, pos, state.getValue(FACING).getOpposite(), false);
        }

        super.onNeighborChange(state, level, pos, neighbor);
    }

    public static void changeFireproofBricks(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction, boolean reinforce) {
        BlockState state = level.getBlockState(pos.relative(direction));
        if(reinforce && state.is(TFMGBlocks.FIREPROOF_BRICKS.get())) {
            level.setBlock(pos.relative(direction), TFMGBlocks.REINFORCED_FIREPROOF_BRICKS.getDefaultState(), 2);
        }
        if(!reinforce && state.is(TFMGBlocks.REINFORCED_FIREPROOF_BRICKS.get())) {
            level.setBlock(pos.relative(direction), TFMGBlocks.FIREPROOF_BRICKS.getDefaultState(), 2);
        }

    }
}
