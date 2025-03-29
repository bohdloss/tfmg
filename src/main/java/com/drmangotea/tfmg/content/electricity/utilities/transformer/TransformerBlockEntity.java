package com.drmangotea.tfmg.content.electricity.utilities.transformer;

import com.drmangotea.tfmg.base.TFMGHorizontalDirectionalBlock;
import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.drmangotea.tfmg.content.electricity.base.VoltageAlteringBlockEntity;

import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class TransformerBlockEntity extends VoltageAlteringBlockEntity {
    boolean updateInFront = false;

    public ItemStack primaryCoil = ItemStack.EMPTY;
    public ItemStack secondaryCoil = ItemStack.EMPTY;

    public float coilRatio = 0;

    public TransformerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public int getOutputVoltage() {
        return (int) (getData().getVoltage()*coilRatio);
    }

    @Override
    public int getOutputPower() {
        return coilRatio == 0 ? 0 : getPowerUsage();
    }

    @Override
    public void tick() {
        super.tick();
        if(updateInFront) {
            updateInFront();
            updateInFront = false;
        }
    }

    @Override
    public int getPowerUsage() {


        Direction facing = getDirection();
        if (level.getBlockEntity(getBlockPos().relative(facing)) instanceof IElectric be && be.getData().getId() != data.getId()) {
            if (be.hasElectricitySlot(facing.getOpposite()))
                return Math.max(be.getNetworkPowerUsage(this), 0);
        }

        return 0;

    }

    @Override
    public boolean makeElectricityTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.makeElectricityTooltip(tooltip, isPlayerSneaking);

        CreateLang.text("   Ráčio: ")
                .add(CreateLang.number(coilRatio))
                .style(ChatFormatting.LIGHT_PURPLE)
                .forGoggles(tooltip, 1);

        CreateLang.text("   Primary: ")
                .add(CreateLang.number(primaryCoil.getOrCreateTag().getFloat("Turns")))
                .style(ChatFormatting.LIGHT_PURPLE)
                .forGoggles(tooltip, 1);
        CreateLang.text("   Secondary: ")
                .add(CreateLang.number(secondaryCoil.getOrCreateTag().getFloat("Turns")))
                .style(ChatFormatting.LIGHT_PURPLE)
                .forGoggles(tooltip, 1);
        return true;
    }

    public void updateCoils(){

        int primaryTurns = primaryCoil.getOrCreateTag().getInt("Turns");
        int secondaryTurns = secondaryCoil.getOrCreateTag().getInt("Turns");

        if(primaryCoil.isEmpty()||secondaryCoil.isEmpty()||primaryTurns<50||secondaryTurns<50){
            coilRatio = 0;
            updateNextTick();
            updateInFront();
            return;
        }

        coilRatio = (float) (float)secondaryTurns/(float) primaryTurns;

        updateNextTick();
        updateInFront();

    }



    @Override
    public float resistance() {
        Direction facing = getBlockState().getValue(FACING).getCounterClockWise();
        if (level.getBlockEntity(getBlockPos().relative(facing)) instanceof IElectric be && be.getData().getId() != data.getId()) {
            if (be.hasElectricitySlot(facing.getOpposite()))
                return Math.max(be.getNetworkResistance(), 0);
        }
        return 0;
    }
    public Direction getDirection(){
        if(!getBlockState().hasProperty(DirectionalBlock.FACING)){
            return getBlockState().getValue(TFMGHorizontalDirectionalBlock.FACING).getCounterClockWise();
        }

        return getBlockState().getValue(DirectionalBlock.FACING);
    }

    @Override
    public boolean hasElectricitySlot(Direction direction) {
        return direction == getBlockState().getValue(FACING).getClockWise();
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

    public void updateInFront(){

        Direction facing = getBlockState().getValue(FACING).getCounterClockWise();
        if (level.getBlockEntity(getBlockPos().relative(facing)) instanceof IElectric be) {
            if (be.hasElectricitySlot(facing.getOpposite())) {
                be.updateNextTick();

            }
        }
        sendStuff();
        setChanged();
    }
    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);

        compound.put("PrimaryCoil", primaryCoil.serializeNBT());
        compound.put("SecondaryCoil", secondaryCoil.serializeNBT());

        compound.putFloat("CoilRation", coilRatio);

    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        primaryCoil = ItemStack.of(compound.getCompound("PrimaryCoil"));
        secondaryCoil = ItemStack.of(compound.getCompound("SecondaryCoil"));

        coilRatio = compound.getFloat("CoilRation");
    }
}
