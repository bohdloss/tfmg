package com.drmangotea.tfmg.content.electricity.utilities.transistor;

import com.drmangotea.tfmg.content.electricity.utilities.diode.ElectricDiodeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class TransistorBlockEntity extends ElectricDiodeBlockEntity {



    public TransistorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    public int findBestVoltage(){


        return 0;
    }

    @Override
    public int getOutputVoltage() {
        return (int) (data.getVoltage()*(findBestVoltage()/100f));
    }

    @Override
    public boolean hasElectricitySlot(Direction direction) {
        return direction == getBlockState().getValue(FACING).getOpposite();
    }
}
