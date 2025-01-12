package com.drmangotea.tfmg.content.electricity.utilities.fuse_block;

import com.drmangotea.tfmg.base.TFMGUtils;
import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.drmangotea.tfmg.content.electricity.utilities.diode.ElectricDiodeBlockEntity;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;


public class FuseBlockEntity extends ElectricDiodeBlockEntity {

    public ItemStack fuse = ItemStack.EMPTY;

    public FuseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public boolean makeElectricityTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.makeElectricityTooltip(tooltip, isPlayerSneaking);
        if(!fuse.isEmpty())
            Lang.text("RATING "+TFMGUtils.formatUnits(fuse.getOrCreateTag().getInt("AmpRating"), "A"))
                    .style(ChatFormatting.RED)
                    .forGoggles(tooltip, 1);



        Lang.text("CURRENT "+ getCurrent())
                .style(ChatFormatting.RED)
                .forGoggles(tooltip, 1);

        Lang.text("USAGE "+TFMGUtils.formatUnits(getNetworkResistance(), "W"))
                .style(ChatFormatting.RED)
                .forGoggles(tooltip, 1);

        Lang.text("VOLTAGE "+TFMGUtils.formatUnits(getData().getVoltage(), "V"))
                .style(ChatFormatting.RED)
                .forGoggles(tooltip, 1);


        return true;
    }

    public void updateInFront(){


        Direction facing = getBlockState().getValue(FACING);
        if (level.getBlockEntity(getBlockPos().relative(facing)) instanceof IElectric be) {
            if (be.hasElectricitySlot(facing.getCounterClockWise())) {
                be.updateNextTick();

            }
        }
        sendStuff();
        setChanged();
    }
    @Override
    public float resistance() {

        Direction facing = getBlockState().getValue(FACING).getCounterClockWise();
        if (level.getBlockEntity(getBlockPos().relative(facing)) instanceof IElectric be) {
            return Math.max(be.getNetworkResistance()-(be.getPowerUsage()-be.powerGeneration()),0);
        }

        return 0;
    }

    @Override
    public void onNetworkChanged(int oldVoltage, int oldPower) {
        super.onNetworkChanged(oldVoltage, oldPower);

        Direction facing = getBlockState().getValue(FACING).getCounterClockWise();
        if (level.getBlockEntity(getBlockPos().relative(facing)) instanceof IElectric be)
            if(hasFuse()&&getCurrent()>=(float) fuse.getOrCreateTag().getInt("AmpRating")){
                blowFuse();
                updateNetwork();
                updateInFront();

            }

    }

    @Override
    public int getOutputVoltage() {
        return fuse.isEmpty() ? 0 : super.getOutputVoltage();
    }

    @Override
    public int getOutputPower() {
        return fuse.isEmpty() ? 0 : super.getOutputPower();
    }

    public boolean hasFuse(){
        return !fuse.isEmpty();
    }

    public void blowFuse(){
        level.playSound(null, getBlockPos(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 2.0F,
                level.random.nextFloat() * 0.4F + 0.8F);
        fuse = ItemStack.EMPTY;
        TFMGUtils.spawnElectricParticles(level,getBlockPos());

    }

    @Override
    public boolean hasElectricitySlot(Direction direction) {
        return direction == getBlockState().getValue(FACING).getClockWise();
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.put("Fuse", fuse.serializeNBT());
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        fuse = ItemStack.of(compound.getCompound("Fuse"));
    }
}
