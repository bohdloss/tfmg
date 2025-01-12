package com.drmangotea.tfmg.content.electricity.utilities.potentiometer;

import com.drmangotea.tfmg.base.TFMGDirectionalBlock;
import com.drmangotea.tfmg.base.TFMGShapes;
import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.drmangotea.tfmg.content.electricity.base.IVoltageChanger;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PotentiometerBlock extends TFMGDirectionalBlock implements IBE<PotentiometerBlockEntity>, IVoltageChanger {
    public PotentiometerBlock(Properties p_54120_) {
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
    public VoxelShape getShape(BlockState pState, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return pState.getValue(FACING).getAxis().isVertical() ? TFMGShapes.RESISTOR_VERTICAL.get(pState.getValue(FACING)) : TFMGShapes.RESISTOR.get(pState.getValue(FACING));
    }


        @Override
    public Class<PotentiometerBlockEntity> getBlockEntityClass() {
        return PotentiometerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PotentiometerBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.RESISTOR.get();
    }
}
