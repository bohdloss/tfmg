package it.bohdloss.tfmg.base;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class TFMGFluidBehavior extends BlockEntityBehaviour {
    public static final BehaviourType<TFMGFluidBehavior> TYPE = new BehaviourType<>();
    public static final BehaviourType<TFMGFluidBehavior> SECONDARY_TYPE = new BehaviourType<>();
    public static final BehaviourType<TFMGFluidBehavior> TERTIARY_TYPE = new BehaviourType<>();
    public static final BehaviourType<TFMGFluidBehavior> QUATERNARY_TYPE = new BehaviourType<>();

    private final BehaviourType<TFMGFluidBehavior> type;
    private final String name;
    private final TFMGFluidTank handler;
    private final OutwardHandler capability;
    public boolean allowExtraction = true;
    public boolean allowInsertion = true;
    public boolean syncCapacity = true;
    public Runnable updateCallback;

    public TFMGFluidBehavior(BehaviourType<TFMGFluidBehavior> type, String name, SmartBlockEntity be, int capacity) {
        super(be);
        this.type = type;
        this.name = name;
        handler = new TFMGFluidTank(this, capacity, x -> true);
        capability = new OutwardHandler(this);
    }

    public TFMGFluidBehavior withCallback(Runnable updateCallback) {
        this.updateCallback = updateCallback == null ? () -> {} : updateCallback;
        return this;
    }

    public TFMGFluidBehavior allowExtraction(boolean allowExtraction) {
        this.allowExtraction = allowExtraction;
        return this;
    }

    public TFMGFluidBehavior allowInsertion(boolean allowInsertion) {
        this.allowInsertion = allowInsertion;
        return this;
    }

    public TFMGFluidBehavior syncCapacity(boolean syncCapacity) {
        this.syncCapacity = syncCapacity;
        return this;
    }

    public TFMGFluidBehavior withValidator(Predicate<FluidStack> validator) {
        this.handler.setValidator(validator == null ? (x -> true) : validator);
        return this;
    }

    public IFluidHandler getCapability() {
        return capability;
    }

    public FluidTank getHandler() {
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

    private record OutwardHandler(TFMGFluidBehavior owner) implements IFluidHandler, IFluidTank {
        @Override
            public int getTanks() {
                return owner.handler.getTanks();
            }

            @Override
            public @NotNull FluidStack getFluidInTank(int tank) {
                return owner.handler.getFluidInTank(tank);
            }

            @Override
            public int getTankCapacity(int tank) {
                return owner.handler.getTankCapacity(tank);
            }

            @Override
            public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
                return owner.handler.isFluidValid(tank, stack);
            }

            @Override
            public @NotNull FluidStack getFluid() {
                return owner.handler.getFluid();
            }

            @Override
            public int getFluidAmount() {
                return owner.handler.getFluidAmount();
            }

            @Override
            public int getCapacity() {
                return owner.handler.getCapacity();
            }

            @Override
            public boolean isFluidValid(@NotNull FluidStack stack) {
                return owner.handler.isFluidValid(stack);
            }

            @Override
            public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
                if (!owner.allowInsertion) {
                    return 0;
                } else {
                    return owner.handler.fill(resource, action);
                }
            }

            @Override
            public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
                if (!owner.allowExtraction) {
                    return FluidStack.EMPTY;
                } else {
                    return owner.handler.drain(resource, action);
                }
            }

            @Override
            public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
                if (!owner.allowExtraction) {
                    return FluidStack.EMPTY;
                } else {
                    return owner.handler.drain(maxDrain, action);
                }
            }
        }

    private static class TFMGFluidTank extends FluidTank {
        private final TFMGFluidBehavior owner;

        public TFMGFluidTank(TFMGFluidBehavior owner, int capacity, Predicate<FluidStack> validator) {
            super(capacity, validator);
            this.owner = owner;
        }

        @Override
        public @NotNull CompoundTag writeToNBT(HolderLookup.@NotNull Provider lookupProvider, @NotNull CompoundTag nbt) {
            if(owner.syncCapacity) {
                nbt.putInt("Capacity", capacity);
            }
            return super.writeToNBT(lookupProvider, nbt);
        }

        @Override
        public @NotNull FluidTank readFromNBT(HolderLookup.@NotNull Provider lookupProvider, @NotNull CompoundTag nbt) {
            if(owner.syncCapacity) {
                this.capacity = nbt.getInt("Capacity");
            }
            super.readFromNBT(lookupProvider, nbt);
            return this;
        }

        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            owner.updateCallback.run();
        }

        @Override
        public @NotNull FluidTank setCapacity(int capacity) {
            super.setCapacity(capacity);
            if(owner.syncCapacity) {
                owner.updateCallback.run();
            }
            return this;
        }

        @Override
        public void setFluid(@NotNull FluidStack stack) {
            super.setFluid(stack);
            owner.updateCallback.run();
        }
    }
}
