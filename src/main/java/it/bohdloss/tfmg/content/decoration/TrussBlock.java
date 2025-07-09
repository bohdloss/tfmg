package it.bohdloss.tfmg.content.decoration;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public class TrussBlock extends RotatedPillarBlock implements ProperWaterloggedBlock, IWrenchable {
    public TrussBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Y).setValue(WATERLOGGED, false));
    }


    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, AXIS);
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockState pState) {
        return fluidState(pState);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pDirection, @NotNull BlockState pNeighborState,
                                           @NotNull LevelAccessor pLevel, @NotNull BlockPos pCurrentPos, @NotNull BlockPos pNeighborPos) {
        updateWater(pLevel, pState, pCurrentPos);
        return pState;
    }

    @Override
    public @NotNull BlockState getStateForPlacement(@NotNull BlockPlaceContext pContext) {
        return withWater(super.getStateForPlacement(pContext), pContext);
    }
}
