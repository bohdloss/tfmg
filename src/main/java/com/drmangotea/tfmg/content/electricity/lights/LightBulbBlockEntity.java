package com.drmangotea.tfmg.content.electricity.lights;



import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.content.electricity.base.ElectricBlockEntity;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import com.drmangotea.tfmg.registry.TFMGPackets;
import com.drmangotea.tfmg.registry.TFMGPartialModels;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static com.drmangotea.tfmg.base.WallMountBlock.FACING;
import static com.drmangotea.tfmg.content.electricity.lights.LightBulbBlock.LIGHT;


public class LightBulbBlockEntity extends ElectricBlockEntity {

    public LerpedFloat glow = LerpedFloat.linear();

    boolean signalChanged;

    boolean hasSignal;

    public DyeColor color= DyeColor.WHITE;



    public LightBulbBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if(!hasSignal) {
            glow.chase(getPowerUsage()*1.5, 0.4, LerpedFloat.Chaser.EXP);
            glow.tickChaser();
            if (Math.min(getData().getVoltage() / 10, 15) != getBlockState().getValue(LIGHT))
                level.setBlock(getBlockPos(), getBlockState().setValue(LIGHT, (int) Math.min(getData().getVoltage() / 10, 15)), 2);
        }else {
            if (getBlockState().getValue(LIGHT)!=0)
                level.setBlock(getBlockPos(), getBlockState().setValue(LIGHT, 0), 2);
            glow.chase(0, 0.4, LerpedFloat.Chaser.EXP);
            glow.tickChaser();

        }
        if (signalChanged) {
            signalChanged = false;
            analogSignalChanged(level.getBestNeighborSignal(worldPosition));
        }
    }
    public void setColor(DyeColor color) {
        if(color==DyeColor.BLACK||color == DyeColor.LIGHT_GRAY|| color == DyeColor.GRAY)
            return;

        this.color = color;
        notifyUpdate();
    }

    @Override
    public void setVoltage(int newVoltage) {
        super.setVoltage(newVoltage);
    }

    @Override
    public float resistance() {
        return 100;
    }


    public void neighbourChanged() {
        if (!hasLevel())
            return;
        boolean powered = level.getBestNeighborSignal(worldPosition)>0;
        if (powered != hasSignal)
            signalChanged = true;
    }
    @Override
    public void lazyTick() {
        super.lazyTick();
        neighbourChanged();
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        NBTHelper.writeEnum(compound,"color",color);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        color = NBTHelper.readEnum(compound,"color",DyeColor.class);
    }

    @Override
    public boolean canBeInGroups() {
        return true;
    }

    protected void analogSignalChanged(int newSignal) {
            hasSignal = newSignal > 0;
    }

    @Override
    public boolean hasElectricitySlot(Direction direction) {
        return direction == getBlockState().getValue(FACING).getOpposite();
    }

}