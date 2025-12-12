package it.bohdloss.tfmg.content.electricity.storage;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import it.bohdloss.tfmg.base.AbstractMultiblock;
import it.bohdloss.tfmg.base.TFMGEnergyBehavior;
import it.bohdloss.tfmg.config.TFMGConfigs;
import it.bohdloss.tfmg.content.electricity.base.CurrentCalculation;
import it.bohdloss.tfmg.content.electricity.base.ElectricData;
import it.bohdloss.tfmg.content.electricity.base.IElectric;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;

import java.util.List;
import java.util.Set;

@EventBusSubscriber
public class AccumulatorBlockEntity extends AbstractMultiblock implements IElectric, IHaveGoggleInformation, IHaveHoveringInformation {
    private ElectricData electricData;

    protected TFMGEnergyBehavior energy;
    protected boolean updateCharge;

    public AccumulatorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        energy = new TFMGEnergyBehavior(TFMGEnergyBehavior.TYPE, "Energy", this, TFMGConfigs.common().machines.accumulatorStorage.get())
                .syncCapacity(true)
                .withCallback(this::notifyUpdate)
                .withCapabilityCallback(this::syncNetwork)
                // The charge/discharge rates are in amperes, so this is the amount of coulombs that can pass in one second... so we divide
                .maxExtraction(ElectricData.coulombToFe(((float) TFMGConfigs.common().machines.accumulatorMaxAmpOutput.get()) / 20f, getElectricData().getGeneratedVoltage()))
                .maxInsertion(ElectricData.coulombToFe(((float) TFMGConfigs.common().machines.accumulatorChargingRate.get()) / 20f, getElectricData().getGeneratedVoltage()));

        behaviours.add(energy);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                TFMGBlockEntities.ACCUMULATOR.get(),
                (be, _direction) -> ((AccumulatorBlockEntity) be.getControllerBE()).energy.getCapability()
        );
    }

    protected void syncNetwork() {
        updateCharge = true;
        getElectricData().syncNextTick = true;
    }

    protected ElectricData instantiateElectric() {
        return new ElectricData(this) {
            @Override
            public boolean hasConnectorTowards(Direction direction) {
                if(!direction.getAxis().equals(getBlockState().getValue(AccumulatorBlock.FACING).getAxis())) {
                    return false;
                } else if(direction.getAxisDirection().equals(Direction.AxisDirection.NEGATIVE)) {
                    return isController();
                } else if (direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE)) {
                    return getTopBlock().equals(getBlockPos());
                } else {
                    return false;
                }
            }

            @Override
            public void getPotentialNeighbors(Set<BlockPos> neighbors) {
                super.getPotentialNeighbors(neighbors);

                if(getHeight() == 1) {
                    return;
                }

                if(isController()) {
                    neighbors.add(getTopBlock());
                }
                if(getTopBlock().equals(getBlockPos())) {
                    neighbors.add(getController());
                }
            }

            @Override
            public void onChargeChange(float charge) {
                if(!isController()) {
                    return;
                }

                AccumulatorBlockEntity be = (AccumulatorBlockEntity) getControllerBE();
                be.energy.getHandler().setEnergyStored(Math.round(ElectricData.coulombToFe(charge, getGeneratedVoltage())));
            }

            @Override
            public float getMaxCharge() {
                if(!isController()) {
                    return 0f;
                }

                return ElectricData.feToCoulomb(energy.getHandler().getMaxEnergyStoredF(), getGeneratedVoltage());
            }

            @Override
            public float getCharge() {
                if(!isController()) {
                    return -1f;
                }

                if(updateCharge) {
                    updateCharge = false;

                   return ElectricData.feToCoulomb(energy.getHandler().getEnergyStoredF(), getGeneratedVoltage());
                } else {
                    return -1f; // Don't update network
                }
            }

            @Override
            public CurrentCalculation getResistance() {
                if(!isController()) {
                    return CurrentCalculation.constant(0);
                }
                return CurrentCalculation.constant(TFMGConfigs.common().machines.accumulatorChargingRate.get());
            }

            @Override
            public float getGeneratedVoltage() {
                if(!isController()) {
                    return 0f;
                }
                return TFMGConfigs.common().machines.accumulatorVoltage.get();
            }

            @Override
            public CurrentCalculation getGeneratorResistance() {
                if(!isController()) {
                    return CurrentCalculation.constant(0);
                }
                return CurrentCalculation.constant(TFMGConfigs.common().machines.accumulatorMaxAmpOutput.get());
            }

            @Override
            public float getGeneratorFrequency() {
                return 0f;
            }
        };
    }

    protected BlockPos getTopBlock() {
        return getController().relative(Direction.UP, getHeight() - 1);
    }

    @Override
    protected void onMultiblockChange() {
        super.onMultiblockChange();

        if(getLevel().isClientSide() || !isController()) {
            return;
        }

        if(getLevel().getBlockEntity(getController()) instanceof AccumulatorBlockEntity be) {
            be.getElectricData().detach();
            be.getElectricData().connectNextTick = true;
        }
        if(getLevel().getBlockEntity(getTopBlock()) instanceof AccumulatorBlockEntity be) {
            be.getElectricData().detach();
            be.getElectricData().connectNextTick = true;
        }
    }

    @Override
    protected BlockState rotateBlockToMatch(BlockState current, BlockState controller) {
        return controller;
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return getBlockState().getValue(AccumulatorBlock.FACING).getAxis();
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        if(longAxis == getBlockState().getValue(AccumulatorBlock.FACING).getAxis()) {
            return TFMGConfigs.common().machines.accumulatorMaxHeight.get();
        } else {
            return getMaxWidth();
        }
    }

    @Override
    public int getMaxWidth() {
        return 1;
    }

    @Override
    public boolean hasTank() {
        return true;
    }

    @Override
    public int getTankSize(int tank) {
        return height;
    }

    @Override
    public void setTankSize(int tank, int blocks) {
        energy.getHandler().setMaxEnergyStored(TFMGConfigs.common().machines.accumulatorStorage.get() * blocks);
    }

    @Override
    public IFluidTank getTank(int tank) {
        return energy.asFluidHandler();
    }

    @Override
    public FluidStack getFluid(int tank) {
        return energy.asFluidHandler().getFluid();
    }

    @Override
    public boolean hasInventory() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

//        if (electricData.getVoltage() >= getOutputVoltage()) {
//            energy.getHandler().receiveEnergy((int) (getChargingRate() / TFMGConfigs.common().machines.FEtoWattTickConversionRate.get()), false);
//
//            return;
//        }
//        if (canPower()) {
//
//            int energyToExtract = data.networkPowerGeneration == 0 ? getNetworkPowerUsage() : (int) Math.max(0, Math.max(((float) powerGeneration() / (float) data.networkPowerGeneration) * (float) getNetworkPowerUsage(), 0));
//            energyToExtract /= TFMGConfigs.common().machines.FEtoWattTickConversionRate.get();
//            energy.extractEnergy(Math.max(energyToExtract, 1), false);
//            if (energy.getEnergyStored() == 0)
//                updateNextTick();
//        }

        getElectricData().tick();

        // When `updateCharge` is true, it means we own the latest "true" value for the energy, so we must not override it
        if(!getLevel().isClientSide() && isController() && !updateCharge) {
            getElectricData().syncCharge();
        }
    }

//    public boolean canPower() {
//        return getData().networkResistance > 0 && (electricData.getVoltage() <= getOutputVoltage()) && energy.getEnergyStored() > 0 && signal == 0;
//    }

//    public int getChargingRate() {
//        if (
//                energy.getHandler().getEnergyStored() >= energy.getHandler().getMaxEnergyStored() ||
//                electricData.getVoltage() < getOutputVoltage() ||
//                canPower() ||
//                electricData.shortCircuit
//        ) {
//            return 0;
//        }
//
//        return getMaxChargingRate();
//    }

    //in FE per tick
    public int getMaxChargingRate() {
        return TFMGConfigs.common().machines.accumulatorChargingRate.get();
    }

    public int getOutputVoltage() {
        return TFMGConfigs.common().machines.accumulatorVoltage.get() * getHeight();
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        getElectricData().lazyTick();
    }

    @Override
    public void remove() {
        super.remove();
        getElectricData().remove();
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        tag.put("ElectricalData", getElectricData().write(registries, clientPacket));
        tag.putBoolean("UpdateCharge", updateCharge);

        super.write(tag, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        getElectricData().read(tag.getCompound("ElectricalData"), registries, clientPacket);
        updateCharge = tag.getBoolean("UpdateCharge");

        super.read(tag, registries, clientPacket);
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return ((AccumulatorBlockEntity) getControllerBE()).getElectricData().addToTooltip(tooltip, isPlayerSneaking);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return ((AccumulatorBlockEntity) getControllerBE()).getElectricData().addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    @Override
    public ElectricData getElectricData() {
        if(electricData == null) {
            electricData = instantiateElectric();
        }
        return electricData;
    }

}
