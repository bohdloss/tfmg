package com.drmangotea.tfmg.content.electricity.storage;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.base.TFMGUtils;
import com.drmangotea.tfmg.config.TFMGConfigs;
import com.drmangotea.tfmg.content.electricity.base.ElectricBlockEntity;
import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.world.level.block.DirectionalBlock.FACING;

public class AccumulatorBlockEntity extends ElectricBlockEntity {

    public final TFMGForgeEnergyStorage energy = createEnergyStorage();
    private LazyOptional<IEnergyStorage> energyCapability = LazyOptional.empty();

    public AccumulatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public boolean makeElectricityTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        super.makeElectricityTooltip(tooltip, isPlayerSneaking);

        // Lang.translate("multimeter.energy_usage")
        //         .add(Component.literal(TFMGUtils.formatUnits((int) getChargingRate(), "FE")))
        //         .style(ChatFormatting.GOLD)
        //         .forGoggles(tooltip, 1);
        Lang.translate("multimeter.energy_stored")
                .add(Component.literal(TFMGUtils.formatUnits((int) energy.getEnergyStored(), "FE")))
                .style(ChatFormatting.GOLD)
                .forGoggles(tooltip, 1);
        /////
        Lang.text("   Usage: ")
                .add(Component.literal(TFMGUtils.formatUnits(getData().networkResistance, "W")))
                .style(ChatFormatting.LIGHT_PURPLE)
                .forGoggles(tooltip, 1);

        Lang.text("   Power Usage: ")
                .add(Lang.number(getPowerUsage() == 0 ? 0 : Math.max(0, Math.max(((float) powerGeneration() / (float) getPowerUsage()) * (float) getData().networkResistance, 0))))
                .style(ChatFormatting.LIGHT_PURPLE)
                .forGoggles(tooltip, 1);

        return true;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {


        if (cap == ForgeCapabilities.ENERGY && side == null) {
            return energyCapability.cast();
        } else if (cap == ForgeCapabilities.ENERGY && hasElectricitySlot(side)) {
            return energyCapability.cast();
        }

        return super.getCapability(cap, side);
    }

    public TFMGForgeEnergyStorage createEnergyStorage() {
        return new TFMGForgeEnergyStorage(getMaxCapacity(), 99999) {
            @Override
            public void onEnergyChanged(int amount) {
                sendStuff();
            }
        };

    }

    public void setCapacity(ItemStack stack) {
        energy.setEnergy(stack.getOrCreateTag().getInt("Storage"));
    }

    @Override
    public void tick() {
        super.tick();
        //if(level.isClientSide)
        //    TFMG.LOGGER.debug("CLIENT: "+energy.getEnergyStored());
        //if(!level.isClientSide)
        //    TFMG.LOGGER.debug("SERVER: "+energy.getEnergyStored());
        if (getData().getVoltage() > TFMGConfigs.common().machines.accumulatorVoltage.get())
            energy.receiveEnergy(getChargingRate(), false);
//
        if (canPower()) {
            energy.extractEnergy(data.networkPowerGeneration == 0 ? getNetworkPowerUsage() : (int) Math.max(0, Math.max(((float) powerGeneration() / (float) data.networkPowerGeneration) * (float) getNetworkPowerUsage(), 0)), false);
            TFMG.LOGGER.debug("A " + energy.extractEnergy(data.networkPowerGeneration == 0 ? getNetworkPowerUsage() : (int) Math.max(0, Math.max(((float) powerGeneration() / (float) data.networkPowerGeneration) * (float) getNetworkPowerUsage(), 0)), true));
            if (energy.getEnergyStored() == 0)
                updateNextTick();
        }

    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("ForgeEnergy", energy.getEnergyStored());
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        energy.setEnergy(compound.getInt("ForgeEnergy"));
    }

    @Override
    public float resistance() {
        if (voltageGeneration() > 0)
            return 0;

        int power = 0;
        for (IElectric member : getOrCreateElectricNetwork().members)
            if (!(member instanceof AccumulatorBlockEntity))
                power += member.getPowerUsage();
        if (energy.getEnergyStored() == getMaxCapacity() || getData().getVoltage() <= getOutputVoltage() || canPower() || getPowerPercentage() < 100)
            return 0;
        return Math.min(Math.max((data.networkPowerGeneration - power), 0), getMaxChargingRate());
    }

    public boolean canPower() {
        return getData().networkResistance > 0 && (getData().getVoltage() <= getOutputVoltage()) && energy.getEnergyStored() > 0;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        energyCapability = LazyOptional.of(() -> energy);
    }

    public int getChargingRate() {
        int chargingRate = Math.max((data.networkPowerGeneration - getNetworkPowerUsage()), 0);
        if (energy.getEnergyStored() == getMaxCapacity() || getData().getVoltage() < getOutputVoltage() || canPower() || getPowerPercentage() < 100)
            return 0;
        return Math.min(chargingRate, getMaxChargingRate());
    }

    @Override
    public int powerGeneration() {
        if (canPower()) {
            return getData().networkResistance > 0 ? maxPowerOutput() : 0;
        }
        return 0;
    }

    public int maxPowerOutput() {
        return getOutputVoltage() * TFMGConfigs.common().machines.accumulatorMaxAmpOutput.get();
    }

    public int getMaxCapacity() {
        return TFMGConfigs.common().machines.accumulatorStorage.get();
    }

    //in FE per tick
    public int getMaxChargingRate() {
        return TFMGConfigs.common().machines.accumulatorChargingRate.get();
    }

    public double conversionRate() {
        return TFMGConfigs.common().machines.FEtoWattTickConversionRate.get();
    }

    public int getOutputVoltage() {
        return TFMGConfigs.common().machines.accumulatorVoltage.get();
    }

    @Override
    public int voltageGeneration() {
        return canPower() ? getOutputVoltage() : 0;
    }


    @Override
    public boolean hasElectricitySlot(Direction direction) {
        return direction.getAxis() == getBlockState().getValue(FACING).getAxis();
    }
}
