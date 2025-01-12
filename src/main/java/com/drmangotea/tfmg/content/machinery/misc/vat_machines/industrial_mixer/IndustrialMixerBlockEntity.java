package com.drmangotea.tfmg.content.machinery.misc.vat_machines.industrial_mixer;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.content.machinery.misc.vat_machines.base.IVatMachine;
import com.drmangotea.tfmg.content.machinery.misc.vat_machines.base.VatBlock;
import com.drmangotea.tfmg.content.machinery.misc.vat_machines.base.VatBlockEntity;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Objects;


public class IndustrialMixerBlockEntity extends KineticBlockEntity implements IVatMachine {


    public MixerMode mixerMode = MixerMode.NONE;
    public int vatSize = 1;
    public int vatHeight = 1;
    public BlockPos vatPos = null;

    LerpedFloat visualSpeed = LerpedFloat.linear();
    float angle;

    public IndustrialMixerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void vatUpdated(VatBlockEntity be) {


        vatSize = be.getWidth();
        vatHeight = be.getHeight();
        vatPos = be.getBlockPos();


    }

    @Override
    public void tick() {
        super.tick();

        if (!level.isClientSide)
            return;

        float targetSpeed = getSpeed();
        visualSpeed.updateChaseTarget(targetSpeed);
        visualSpeed.tickChaser();


    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        for (MixerMode mode : MixerMode.values()) {
            if (mode == mixerMode) {
                compound.putString("MixerMode", mode.name);
            }
        }
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {

        TFMG.LOGGER.debug("Loading " + compound.getString("MixerMode"));
        setMixerMode(compound.getString("MixerMode"), false);

        if (clientPacket)
            visualSpeed.chase(getGeneratedSpeed(), (double) 1 / 32, LerpedFloat.Chaser.EXP);
        super.read(compound, clientPacket);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(getBlockPos()).inflate(3);
    }

    @Override
    public String getOperationId() {
        return switch (mixerMode) {

            case NONE -> "";
            case MIXING -> "tfmg:mixing";
            case CENTRIFUGE -> "tfmg:centrifuge";
        };
    }

    public boolean setMixerMode(ItemStack modeItem, boolean simulate) {
        for (MixerMode mode : MixerMode.values()) {
            if (mode.item.is(modeItem.getItem())) {
                if (!simulate) {
                    mixerMode = mode;
                } else return true;
            }
        }
        if (!simulate && hasLevel())
            VatBlock.updateVatState(getBlockState(), getLevel(), getBlockPos().relative(Direction.DOWN));
        sendData();
        return false;
    }

    public boolean setMixerMode(String name, boolean simulate) {
        for (MixerMode mode : MixerMode.values()) {
            if (Objects.equals(mode.name, name)) {
                if (!simulate) {
                    mixerMode = mode;

                } else return true;
            }
        }
        if (!simulate && hasLevel())
            VatBlock.updateVatState(getBlockState(), getLevel(), getBlockPos().relative(Direction.DOWN));
        sendData();
        return false;
    }

    @Override
    public PositionRequirement getPositionRequirement() {
        return PositionRequirement.TOP_CENTER;
    }

    @Override
    public String[] doesntWorkWith() {
        return new String[]{"electrodes"};
    }

    enum MixerMode {
        NONE("none", ItemStack.EMPTY),
        MIXING("mixing", TFMGItems.MIXER_BLADE.asStack()),
        CENTRIFUGE("centrifuge", TFMGItems.CENTRIFUGE.asStack());
        public final String name;
        public final ItemStack item;

        MixerMode(String name, ItemStack stack) {
            this.name = name;
            this.item = stack;
        }
    }
}
