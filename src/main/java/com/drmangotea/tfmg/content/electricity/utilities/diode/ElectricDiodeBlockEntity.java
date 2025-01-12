package com.drmangotea.tfmg.content.electricity.utilities.diode;


import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.drmangotea.tfmg.content.electricity.base.UpdateInFrontPacket;
import com.drmangotea.tfmg.content.electricity.base.VoltageAlteringBlockEntity;
import com.drmangotea.tfmg.registry.TFMGPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

import static net.minecraft.world.level.block.DirectionalBlock.FACING;

public class ElectricDiodeBlockEntity extends VoltageAlteringBlockEntity {

    boolean updateInFront = false;

    public ElectricDiodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public int getOutputVoltage() {
        return getData().getVoltage();
    }

    @Override
    public int getOutputPower() {
        return getPowerUsage();
    }

    @Override
    public void tick() {
        super.tick();
        if (updateInFront) {
            updateInFront();
            updateInFront = false;
        }
    }


    @Override
    public float resistance() {
        Direction facing = getBlockState().getValue(FACING);
        if (level.getBlockEntity(getBlockPos().relative(facing)) instanceof IElectric be && be.getData().getId() != data.getId()) {
            if (be.hasElectricitySlot(facing.getOpposite()))
                return Math.max(be.getNetworkResistance(), 0);
        }
        return 0;
    }

    @Override
    public boolean hasElectricitySlot(Direction direction) {
        return getBlockState().getValues().containsKey(FACING) && direction == getBlockState().getValue(FACING).getOpposite();
    }

    @Override
    public void onNetworkChanged(int oldVoltage, int oldPower) {
        super.onNetworkChanged(oldVoltage, oldPower);

        if (oldVoltage != getData().getVoltage() || oldPower != getPowerUsage()) {
            updateInFront = true;
        }
        sendStuff();
        setChanged();
    }



    @Override
    public void remove() {

        super.remove();
        updateInFront();
    }

    @Override
    public void onPlaced() {

        super.onPlaced();
        updateInFront = true;
    }

    public void updateInFrontNextTick(){
        updateInFront = true;
    }

    public void updateInFront() {

        TFMG.LOGGER.debug("IN_FRONT");
        if(!level.isClientSide)
            TFMGPackets.getChannel().send(PacketDistributor.ALL.noArg(), new UpdateInFrontPacket(BlockPos.of(getPos())));
        Direction facing = getBlockState().getValue(FACING);
        if (level.getBlockEntity(getBlockPos().relative(facing)) instanceof IElectric be && be.getData().getId() != data.getId()) {
            if (be.hasElectricitySlot(facing.getOpposite())) {
                be.updateNextTick();

            }
        }
        sendStuff();
        setChanged();
    }
}
