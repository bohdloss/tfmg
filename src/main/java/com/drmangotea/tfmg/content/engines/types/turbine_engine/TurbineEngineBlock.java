package com.drmangotea.tfmg.content.engines.types.turbine_engine;

import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.drmangotea.tfmg.content.engines.base.EngineBlock;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TurbineEngineBlock extends EngineBlock implements IBE<TurbineEngineBlockEntity> {
    public TurbineEngineBlock(Properties properties) {
        super(properties);
    }
    @Override
    public void onPlace(BlockState pState, Level level, BlockPos pos, BlockState pOldState, boolean pIsMoving) {
        withBlockEntityDo(level, pos, IElectric::onPlaced);
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
    }
    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face==state.getValue(SHAFT_FACING)&&state.getValue(ENGINE_STATE)==EngineState.BACK;
    }
    @Override
    public Class<TurbineEngineBlockEntity> getBlockEntityClass() {
        return TurbineEngineBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TurbineEngineBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.TURBINE_ENGINE.get();
    }
}
