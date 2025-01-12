package com.drmangotea.tfmg.content.machinery.misc.exhaust;


import com.drmangotea.tfmg.base.WallMountBlock;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RodBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.Random;

public class ExhaustBlock extends WallMountBlock implements IBE<ExhaustBlockEntity>, IWrenchable, ProperWaterloggedBlock {

    public ExhaustBlock(Properties p_55926_) {
        super(p_55926_);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(WATERLOGGED, false));
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return withWater(super.getStateForPlacement(pContext), pContext);
    }
    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
                                  LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        updateWater(pLevel, pState, pCurrentPos);
        return pState;
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return fluidState(pState);
    }




    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153746_) {
        p_153746_.add(FACING, WATERLOGGED);
    }
    @Override
    public Class<ExhaustBlockEntity> getBlockEntityClass() {
        return ExhaustBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ExhaustBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.EXHAUST.get();
    }
}
