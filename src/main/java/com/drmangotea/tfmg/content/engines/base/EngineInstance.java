package com.drmangotea.tfmg.content.engines.base;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static com.drmangotea.tfmg.content.engines.base.EngineBlock.ENGINE_STATE;

public class EngineInstance<T extends KineticBlockEntity> extends SingleRotatingInstance<T> {

    public EngineInstance(MaterialManager materialManager, T blockEntity) {
        super(materialManager, blockEntity);
    }




    @Override
    protected Instancer<RotatingData> getModel() {

        if(blockState.getValue(ENGINE_STATE)!= EngineBlock.EngineState.SHAFT)
            return getRotatingMaterial().getModel(Blocks.AIR.defaultBlockState());

        Direction dir = getShaftDirection();
        return getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, blockState, dir);
    }

    protected Direction getShaftDirection() {
        return blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
    }
}