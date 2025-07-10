package it.bohdloss.tfmg.content.machinery.misc.exhaust;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import it.bohdloss.tfmg.blocks.WallMountBlock;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class ExhaustBlock extends WallMountBlock implements IBE<ExhaustBlockEntity>, IWrenchable, ProperWaterloggedBlock {
    public ExhaustBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(WATERLOGGED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return withWater(super.getStateForPlacement(pContext), pContext);
    }
    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pDirection,
                                           @NotNull BlockState pNeighborState, @NotNull LevelAccessor pLevel,
                                           @NotNull BlockPos pCurrentPos, @NotNull BlockPos pNeighborPos) {
        updateWater(pLevel, pState, pCurrentPos);
        return pState;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter level,
                                        @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return TFMGShapes.CABLE_TUBE.get(blockState.getValue(FACING));
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockState pState) {
        return fluidState(pState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public Class<ExhaustBlockEntity> getBlockEntityClass() {
        return ExhaustBlockEntity.class;
    }

    @Override
    public BlockEntityType<ExhaustBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.EXHAUST.get();
    }
}
