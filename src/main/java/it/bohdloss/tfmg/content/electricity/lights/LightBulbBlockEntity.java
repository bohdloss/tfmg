package it.bohdloss.tfmg.content.electricity.lights;

import it.bohdloss.tfmg.content.electricity.base.ElectricBlockEntity;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static it.bohdloss.tfmg.content.electricity.lights.LightBulbBlock.LIGHT;

public class LightBulbBlockEntity extends ElectricBlockEntity {
    public LerpedFloat glow = LerpedFloat.linear();
    boolean signalChanged;
    boolean hasSignal;
    public DyeColor color = DyeColor.WHITE;

    public LightBulbBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if(!hasSignal && getVoltage() != 0) {
            glow.chase(getConsumedCurrent() * 2.5, 0.4, LerpedFloat.Chaser.EXP);
            glow.tickChaser();
            if (!level.isClientSide && Math.min(getVoltage() / 10, 15) != getBlockState().getValue(LIGHT)) {
                level.setBlock(getBlockPos(), getBlockState().setValue(LIGHT, (int) Math.min(getVoltage() / 10, 15)),  16 | 2);
            }
        } else {
            if (!level.isClientSide && getBlockState().getValue(LIGHT) != 0) {
                level.setBlock(getBlockPos(), getBlockState().setValue(LIGHT, 0), 16 | 2);
            }
            glow.chase(0, 0.4, LerpedFloat.Chaser.EXP);
            glow.tickChaser();
        }
        if (!level.isClientSide && signalChanged) {
            signalChanged = false;
            analogSignalChanged(level.getBestNeighborSignal(worldPosition));
        }
    }

    public void setColor(DyeColor color) {
        if(color == DyeColor.BLACK || color == DyeColor.LIGHT_GRAY || color == DyeColor.GRAY) {
            return;
        }

        this.color = color;
        notifyUpdate();
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        neighbourChanged();
    }

    protected void analogSignalChanged(int newSignal) {
        hasSignal = newSignal > 0;
    }

    public void neighbourChanged() {
        if (!hasLevel())
            return;
        boolean powered = level.getBestNeighborSignal(worldPosition)>0;
        if (powered != hasSignal)
            signalChanged = true;
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);

        NBTHelper.writeEnum(compound,"Color", color);
        compound.putBoolean("HasSignal", hasSignal);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        color = NBTHelper.readEnum(compound,"Color", DyeColor.class);
        hasSignal = compound.getBoolean("HasSignal");
    }
}
