package com.drmangotea.tfmg.content.electricity.utilities.electrical_switch;

import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.drmangotea.tfmg.content.electricity.utilities.diode.ElectricDiodeBlockEntity;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class ElectricalSwitchBlockEntity extends ElectricDiodeBlockEntity {

    int signal;
    boolean signalChanged;


    public ElectricalSwitchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();

        neighbourChanged();
    }

    @Override
    public void tick() {
        super.tick();

        if (signalChanged) {
            signalChanged = false;
            analogSignalChanged(level.getBestNeighborSignal(worldPosition));
        }
    }
    protected void analogSignalChanged(int newSignal) {
        signal = newSignal;
        updateNetwork();
        updateInFrontNextTick();

    }
    public void neighbourChanged() {
        if (!hasLevel())
            return;
        int power = level.getBestNeighborSignal(worldPosition);
        if (power != signal)
            signalChanged = true;
    }

    @Override
    public int getOutputVoltage() {
        return (int) (data.getVoltage()*(signal/15f));
    }

    @Override
    public boolean hasElectricitySlot(Direction direction) {
        return direction == getBlockState().getValue(FACING).getOpposite();
    }
}
