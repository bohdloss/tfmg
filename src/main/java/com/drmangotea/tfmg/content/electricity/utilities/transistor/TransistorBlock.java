package com.drmangotea.tfmg.content.electricity.utilities.transistor;

import com.drmangotea.tfmg.base.TFMGHorizontalDirectionalBlock;
import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TransistorBlock extends TFMGHorizontalDirectionalBlock implements IBE<TransistorBlockEntity> {
    public TransistorBlock(Properties p_54120_) {
        super(p_54120_);
    }
    @Override
    public void onPlace(BlockState pState, Level level, BlockPos pos, BlockState pOldState, boolean pIsMoving) {
        withBlockEntityDo(level,pos, IElectric::onPlaced);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
    }




        @Override
    public Class<TransistorBlockEntity> getBlockEntityClass() {
        return TransistorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TransistorBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.TRANSISTOR.get();
    }
}
