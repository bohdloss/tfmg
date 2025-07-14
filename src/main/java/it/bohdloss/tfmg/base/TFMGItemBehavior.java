package it.bohdloss.tfmg.base;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import it.bohdloss.tfmg.registry.TFMGItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class TFMGItemBehavior extends BlockEntityBehaviour {
    public static final BehaviourType<TFMGItemBehavior> TYPE = new BehaviourType<>();
    public static final BehaviourType<TFMGItemBehavior> SECONDARY_TYPE = new BehaviourType<>();

    private final BehaviourType<TFMGItemBehavior> type;
    private final String name;
    private final TFMGInventory handler;
    private final OutwardHandler capability;
    public boolean allowExtraction = true;
    public boolean allowInsertion = true;
    public boolean stackNonStackables;
    public int stackSize = 64;
    private Runnable updateCallback;
    private BiPredicate<Integer, ItemStack> validator;

    public TFMGItemBehavior(BehaviourType<TFMGItemBehavior> type, String name, SmartBlockEntity be, int slots) {
        super(be);
        this.type = type;
        this.name = name;
        updateCallback = () -> {};
        validator = (x, y) -> true;
        handler = new TFMGInventory(this, slots);
        capability = new OutwardHandler(this);
    }

    public TFMGItemBehavior withStackSize(int stackSize) {
        this.stackSize = stackSize;
        return this;
    }

    public TFMGItemBehavior withStackNonStackables(boolean stackNonStackables) {
        this.stackNonStackables = stackNonStackables;
        return this;
    }

    public TFMGItemBehavior withCallback(Runnable updateCallback) {
        this.updateCallback = updateCallback == null ? () -> {} : updateCallback;
        return this;
    }

    public TFMGItemBehavior allowExtraction(boolean allowExtraction) {
        this.allowExtraction = allowExtraction;
        return this;
    }

    public TFMGItemBehavior allowInsertion(boolean allowInsertion) {
        this.allowInsertion = allowInsertion;
        return this;
    }

    public TFMGItemBehavior withValidator(BiPredicate<Integer, ItemStack> validator) {
        this.validator = validator == null ? ((x, y) -> true) : validator;
        return this;
    }

    public IItemHandler getCapability() {
        return capability;
    }

    public ItemStackHandler getHandler() {
        return handler;
    }

    @Override
    public BehaviourType<?> getType() {
        return type;
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);

        nbt.put(name, handler.serializeNBT(registries));
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);

        handler.deserializeNBT(registries, nbt.getCompound(name));
    }

    private record OutwardHandler(TFMGItemBehavior owner) implements IItemHandler {
        @Override
        public int getSlots() {
            return owner.handler.getSlots();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return owner.handler.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(!owner.allowInsertion) {
                return ItemStack.EMPTY;
            }
            return owner.handler.insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(!owner.allowExtraction) {
                return ItemStack.EMPTY;
            }
            return owner.handler.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return owner.handler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return owner.handler.isItemValid(slot, stack);
        }
    }

    private static class TFMGInventory extends ItemStackHandler {
        private final TFMGItemBehavior owner;
        private boolean tempDisableCallback;

        public TFMGInventory(TFMGItemBehavior owner, int slots) {
            super(slots);
            this.owner = owner;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return owner.validator.test(slot, stack);
        }

        @Override
        public int getSlotLimit(int slot) {
            return Math.min(owner.stackNonStackables ? 64 : super.getSlotLimit(slot), owner.stackSize);
        }

        @Override
        public void setSize(int size) {
            super.setSize(size);
            if(!tempDisableCallback) {
                owner.updateCallback.run();
            }
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            owner.updateCallback.run();
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
            tempDisableCallback = true;
            super.deserializeNBT(provider, nbt);
            tempDisableCallback = false;
        }
    }
}
