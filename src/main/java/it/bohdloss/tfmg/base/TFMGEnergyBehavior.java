package it.bohdloss.tfmg.base;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class TFMGEnergyBehavior extends BlockEntityBehaviour {
    public static final BehaviourType<TFMGEnergyBehavior> TYPE = new BehaviourType<>();
    public static final BehaviourType<TFMGEnergyBehavior> SECONDARY_TYPE = new BehaviourType<>();
    public static final BehaviourType<TFMGEnergyBehavior> TERTIARY_TYPE = new BehaviourType<>();
    public static final BehaviourType<TFMGEnergyBehavior> QUATERNARY_TYPE = new BehaviourType<>();

    private final BehaviourType<TFMGEnergyBehavior> type;
    private final String name;
    private final TFMGEnergyStorage handler;
    private final TFMGEnergyFluidTank fluid;
    private final OutwardHandler capability;
    public float maxExtraction = 0;
    public float maxInsertion = 0;
    public boolean syncCapacity = true;
    public Runnable updateCallback;
    public Runnable capabilityCallback;

    public TFMGEnergyBehavior(BehaviourType<TFMGEnergyBehavior> type, String name, SmartBlockEntity be, float capacity) {
        super(be);
        this.type = type;
        this.name = name;
        handler = new TFMGEnergyStorage(this, capacity);
        capability = new OutwardHandler(this);
        fluid = new TFMGEnergyFluidTank(this);
    }

    public TFMGEnergyBehavior withCallback(Runnable updateCallback) {
        this.updateCallback = updateCallback == null ? () -> {} : updateCallback;
        return this;
    }

    public TFMGEnergyBehavior withCapabilityCallback(Runnable capabilityCallback) {
        this.capabilityCallback = capabilityCallback == null ? () -> {} : capabilityCallback;
        return this;
    }

    public TFMGEnergyBehavior maxExtraction(float maxExtraction) {
        this.maxExtraction = maxExtraction;
        return this;
    }

    public TFMGEnergyBehavior maxInsertion(float maxInsertion) {
        this.maxInsertion = maxInsertion;
        return this;
    }

    public TFMGEnergyBehavior syncCapacity(boolean syncCapacity) {
        this.syncCapacity = syncCapacity;
        return this;
    }

    public IEnergyStorage getCapability() {
        return capability;
    }

    public FluidTank asFluidHandler() {
        return fluid;
    }

    public TFMGEnergyStorage getHandler() {
        return handler;
    }

    @Override
    public BehaviourType<?> getType() {
        return type;
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);

        nbt.put(name, handler.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);

        handler.readFromNBT(registries, nbt.getCompound(name));
    }

    private record OutwardHandler(TFMGEnergyBehavior owner) implements IEnergyStorage {
        @Override
        public int receiveEnergy(int toReceive, boolean simulate) {
            int inserted = owner.handler.receiveEnergy(Math.min(toReceive, (int) owner.maxInsertion), simulate);
            if(inserted > 0 && !simulate) {
                owner.capabilityCallback.run();
            }
            return inserted;
        }

        @Override
        public int extractEnergy(int toExtract, boolean simulate) {
            int extracted = owner.handler.extractEnergy(Math.min(toExtract, (int) owner.maxExtraction), simulate);
            if(extracted > 0 && !simulate) {
                owner.capabilityCallback.run();
            }
            return extracted;
        }

        @Override
        public int getEnergyStored() {
            return owner.handler.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return owner.handler.getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            return owner.maxExtraction > 0;
        }

        @Override
        public boolean canReceive() {
            return owner.maxInsertion > 0;
        }
    }

    public class TFMGEnergyFluidTank extends FluidTank {
        private final TFMGEnergyBehavior owner;

        public TFMGEnergyFluidTank(TFMGEnergyBehavior owner) {
            super(0);
            this.owner = owner;
        }

        @Override
        public FluidStack getFluid() {
            return new FluidStack(Fluids.WATER.getSource(), owner.handler.getEnergyStored());
        }

        @Override
        public int getFluidAmount() {
            return owner.handler.getEnergyStored();
        }

        @Override
        public int getCapacity() {
            return owner.handler.getMaxEnergyStored();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.is(Fluids.WATER.getSource());
        }

        @Override
        public FluidTank setValidator(Predicate<FluidStack> validator) {
            return this;
        }

        @Override
        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            if(!isFluidValid(resource)) {
                return 0;
            }
            return owner.handler.receiveEnergy(resource.getAmount(), action.simulate());
        }

        @Override
        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            return new FluidStack(Fluids.WATER.getSource(), owner.handler.extractEnergy(maxDrain, action.simulate()));
        }

        @Override
        public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            if(!isFluidValid(resource)) {
                return FluidStack.EMPTY;
            }
            return new FluidStack(Fluids.WATER.getSource(), owner.handler.extractEnergy(resource.getAmount(), action.simulate()));
        }

        @Override
        protected void onContentsChanged() {
            owner.updateCallback.run();
        }

        @Override
        public void setFluid(FluidStack stack) {
            if(!isFluidValid(stack)) {
                return;
            }
            owner.handler.setEnergyStored(stack.getAmount());
        }

        @Override
        public FluidTank setCapacity(int capacity) {
            owner.handler.setMaxEnergyStored(capacity);
            return this;
        }

        @Override
        public int getTankCapacity(int tank) {
            return getCapacity();
        }

        @Override
        public boolean isEmpty() {
            return owner.handler.getEnergyStored() <= 0;
        }

        @Override
        public int getSpace() {
            return Math.max(0, owner.handler.getMaxEnergyStored() - owner.handler.getEnergyStored());
        }

        @Override
        public FluidTank readFromNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
            owner.handler.readFromNBT(lookupProvider, nbt);
            return this;
        }

        @Override
        public CompoundTag writeToNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
            return owner.handler.writeToNBT(lookupProvider, nbt);
        }
    }

    public static class TFMGEnergyStorage extends EnergyStorage {
        private final TFMGEnergyBehavior owner;
        protected float energy;
        protected float capacity;
        protected float maxReceive;
        protected float maxExtract;

        public TFMGEnergyStorage(TFMGEnergyBehavior owner, float capacity) {
            super(0);
            this.capacity = capacity;
            this.owner = owner;
        }

        @Override
        public int receiveEnergy(int toReceive, boolean simulate) {
            if (!canReceive() || toReceive <= 0) {
                return 0;
            }

            int energyReceived = Mth.clamp((int) this.capacity - (int) this.energy, 0, Math.min((int) this.maxReceive, toReceive));
            if (!simulate) {
                this.energy += energyReceived;
                owner.updateCallback.run();
            }
            return energyReceived;
        }

        @Override
        public int extractEnergy(int toExtract, boolean simulate) {
            if (!canExtract() || toExtract <= 0) {
                return 0;
            }

            int energyExtracted = Math.min((int) this.energy, Math.min((int) this.maxExtract, toExtract));
            if (!simulate) {
                this.energy -= energyExtracted;
                owner.updateCallback.run();
            }
            return energyExtracted;
        }


        @Override
        public int getEnergyStored() {
            return (int) energy;
        }

        @Override
        public int getMaxEnergyStored() {
            return (int) capacity;
        }

        @Override
        public boolean canExtract() {
            return ((int) maxExtract) > 0f;
        }

        @Override
        public boolean canReceive() {
            return ((int) maxReceive) > 0f;
        }

        public @NotNull CompoundTag writeToNBT(HolderLookup.@NotNull Provider lookupProvider, @NotNull CompoundTag nbt) {
            if(owner.syncCapacity) {
                nbt.putFloat("Capacity", capacity);
            }
            nbt.putFloat("Energy", energy);
            return nbt;
        }

        public void readFromNBT(HolderLookup.@NotNull Provider lookupProvider, @NotNull CompoundTag nbt) {
            if(owner.syncCapacity) {
                this.capacity = nbt.getFloat("Capacity");
            }
            energy = nbt.getFloat("Energy");
        }

        public void setMaxEnergyStored(float capacity) {
            this.capacity = capacity;
            owner.updateCallback.run();
        }

        public float getMaxEnergyStoredF() {
            return capacity;
        }

        public void setEnergyStored(float energy) {
            this.energy = energy;
            owner.updateCallback.run();
        }

        public float getEnergyStoredF() {
            return energy;
        }
    }
}
