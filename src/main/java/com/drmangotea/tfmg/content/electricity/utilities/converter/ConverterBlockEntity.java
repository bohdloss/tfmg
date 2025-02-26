package com.drmangotea.tfmg.content.electricity.utilities.converter;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.base.TFMGUtils;
import com.drmangotea.tfmg.config.TFMGConfigs;
import com.drmangotea.tfmg.content.electricity.base.ElectricBlockEntity;
import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.drmangotea.tfmg.content.electricity.storage.AccumulatorBlockEntity;
import com.drmangotea.tfmg.content.electricity.storage.TFMGForgeEnergyStorage;
import com.drmangotea.tfmg.content.machinery.misc.winding_machine.WindingMachineBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import java.util.List;

import static com.drmangotea.tfmg.content.electricity.utilities.converter.ConverterBlock.INPUT;
import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;
import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class ConverterBlockEntity extends ElectricBlockEntity {

    public final TFMGForgeEnergyStorage energy = createEnergyStorage();
    private LazyOptional<IEnergyStorage> energyCapability = LazyOptional.empty();


    protected ScrollValueBehaviour voltageGenerated;

    public ConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        energyCapability = LazyOptional.of(() -> energy);
    }

    public TFMGForgeEnergyStorage createEnergyStorage() {
        return new TFMGForgeEnergyStorage(500000, 10000) {
            @Override
            public void onEnergyChanged(int amount, int a) {
                sendStuff();
            }
        };

    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        int max = 250;
        voltageGenerated = new ScrollValueBehaviour(Lang.translateDirect("creative_generator.voltage_generation"),
                this, new ConverterValueBox());
        voltageGenerated.between(1, max);
        voltageGenerated.value = 20;
        voltageGenerated.withCallback(i -> this.updateNextTick());
        behaviours.add(voltageGenerated);

    }


    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("ForgeEnergy", energy.getEnergyStored());
    }


    public boolean isInput() {
        return getBlockState().getValue(INPUT);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if (cap == ForgeCapabilities.ENERGY && side == null) {
            return energyCapability.cast();
        } else if (cap == ForgeCapabilities.ENERGY && hasElectricitySlot(side.getOpposite())) {
            return energyCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public boolean makeElectricityTooltip(List<Component> tooltip, boolean isPlayerSneaking) {


        Lang.text("Energy").add(Lang.number(getCapability(ForgeCapabilities.ENERGY).orElseGet(null).getEnergyStored())).forGoggles(tooltip);
        Lang.number(energy.getMaxEnergyStored()).forGoggles(tooltip);

        super.makeElectricityTooltip(tooltip, isPlayerSneaking);


        Lang.translate("multimeter.energy_stored")
                .add(Component.literal(TFMGUtils.formatUnits((int) energy.getEnergyStored(), "FE")))
                .style(ChatFormatting.GOLD)
                .forGoggles(tooltip, 1);
        /////
        Lang.text("   Usage: ")
                .add(Component.literal(TFMGUtils.formatUnits(getData().networkResistance, "W")))
                .style(ChatFormatting.LIGHT_PURPLE)
                .forGoggles(tooltip, 1);
        int energyToExtract = data.networkPowerGeneration == 0 ? getNetworkPowerUsage() : (int) Math.max(0, Math.max(((float) powerGeneration() / (float) data.networkPowerGeneration) * (float) getNetworkPowerUsage(), 0));
        energyToExtract /= TFMGConfigs.common().machines.FEtoWattTickConversionRate.get();
        Lang.text("   Power Usage: ")
                .add(Lang.number(energyToExtract))
                .style(ChatFormatting.LIGHT_PURPLE)
                .forGoggles(tooltip, 1);

        return true;
    }

    @Override
    public float resistance() {
        if (voltageGeneration() > 0)
            return 0;


        int power = 0;
        for (IElectric member : getOrCreateElectricNetwork().members)
            if (!(member instanceof ConverterBlockEntity) && !(member instanceof AccumulatorBlockEntity))
                power += member.getPowerUsage();
        if (energy.getEnergyStored() == getMaxCapacity() || getData().getVoltage() <= voltageGenerated.getValue() || canPower())
            return 0;



        return (float) (data.voltage * data.voltage) /Math.min(Math.max((data.networkPowerGeneration - power), 0), getMaxChargingRate());
    }

    public boolean canPower() {


        if (getBlockState().getValue(INPUT))
            return false;

        return getData().networkResistance > 0 && (getData().getVoltage() <= voltageGenerated.getValue()) && energy.getEnergyStored() > 0;
    }


    public int getChargingRate() {
        int chargingRate = Math.max((data.networkPowerGeneration - getNetworkPowerUsage()), 0);
        if (energy.getEnergyStored() == getMaxCapacity() || getData().getVoltage() < voltageGenerated.getValue() || canPower())
            return 0;
        return Math.min(chargingRate, getMaxChargingRate());
    }

    @Override
    public int powerGeneration() {
        if (canPower()) {
            return getData().networkResistance > 0 ? voltageGenerated.getValue() : 0;
        }
        return 0;
    }

    @Override
    public void tick() {
        super.tick();


        if (getBlockState().getValue(INPUT)) {
            if (getData().getVoltage() > TFMGConfigs.common().machines.accumulatorVoltage.get()) {
                energy.receiveEnergy((int) (getChargingRate() / TFMGConfigs.common().machines.FEtoWattTickConversionRate.get()), false);

            }
        } else if (canPower()) {

            int energyToExtract = data.networkPowerGeneration == 0 ? getNetworkPowerUsage() : (int) Math.max(0, Math.max(((float) powerGeneration() / (float) data.networkPowerGeneration) * (float) getNetworkPowerUsage(), 0));
            energyToExtract /= TFMGConfigs.common().machines.FEtoWattTickConversionRate.get();
            energy.extractEnergy(Math.max(energyToExtract, 1), false);
            if (energy.getEnergyStored() == 0)
                updateNextTick();
        }

    }


    public int getMaxCapacity() {
        return TFMGConfigs.common().machines.accumulatorStorage.get();
    }

    //in FE per tick
    public int getMaxChargingRate() {
        return TFMGConfigs.common().machines.accumulatorChargingRate.get()*10;
    }

    @Override
    public int voltageGeneration() {

        return canPower() ? voltageGenerated.getValue() : 0;
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        energy.setEnergy(compound.getInt("ForgeEnergy"));
    }

    @Override
    public boolean hasElectricitySlot(Direction direction) {
        return direction == getBlockState().getValue(FACING).getClockWise();
    }
    public static class ConverterValueBox extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 3, 16.05);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis() == state.getValue(HORIZONTAL_FACING).getAxis();
        }
    }
}
