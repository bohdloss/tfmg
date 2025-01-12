package com.drmangotea.tfmg.content.engines.regular_engine;

import com.drmangotea.tfmg.content.engines.EngineBlock;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RegularEngineBlock extends EngineBlock implements IBE<RegularEngineBlockEntity> {
    public RegularEngineBlock(Properties properties) {
        super(properties);
    }


    @Override
    public Class<RegularEngineBlockEntity> getBlockEntityClass() {
        return RegularEngineBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RegularEngineBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.REGULAR_ENGINE.get();
    }
}
