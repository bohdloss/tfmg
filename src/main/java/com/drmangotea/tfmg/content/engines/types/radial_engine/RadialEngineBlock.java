package com.drmangotea.tfmg.content.engines.types.radial_engine;

import com.drmangotea.tfmg.content.engines.types.regular_engine.RegularEngineBlock;
import com.drmangotea.tfmg.content.engines.types.regular_engine.RegularEngineBlockEntity;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RadialEngineBlock extends RegularEngineBlock {
    public RadialEngineBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(ENGINE_STATE, EngineState.SINGLE));
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {

        //if(state.getValue(ENGINE_STATE)==EngineState.NORMAL)
        //    return false;
        if(state.getValue(ENGINE_STATE)==EngineState.SINGLE)
            return face.getAxis() == state.getValue(SHAFT_FACING).getAxis();

        return face==state.getValue(SHAFT_FACING);
    }



    @Override
    public BlockEntityType<? extends RegularEngineBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.RADIAL_ENGINE.get();
    }
}
