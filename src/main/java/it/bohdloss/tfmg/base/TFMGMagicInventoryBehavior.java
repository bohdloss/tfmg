package it.bohdloss.tfmg.base;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public class TFMGMagicInventoryBehavior extends TFMGItemBehavior {
    public static final BehaviourType<TFMGMagicInventoryBehavior> TYPE = new BehaviourType<>();
    public static final BehaviourType<TFMGMagicInventoryBehavior> SECONDARY_TYPE = new BehaviourType<>();

    protected final BehaviourType<TFMGMagicInventoryBehavior> type;
    protected IItemHandlerModifiable managed;
    protected int maxSlots;

    public static final int MAGIC_SLOT = -1;

    public TFMGMagicInventoryBehavior(BehaviourType<TFMGMagicInventoryBehavior> type, String name, SmartBlockEntity be, int maxSlots) {
        super(TFMGItemBehavior.TYPE, name, be, 1);
        this.type = type;
        this.handler = new TFMGAutoResizeInventory(this);
        this.managed = new TFMGManagedInventory(this);
        this.maxSlots = maxSlots;
    }

    public TFMGMagicInventoryBehavior withStackSize(int stackSize) {
        this.stackSize = stackSize;
        return this;
    }

    public TFMGMagicInventoryBehavior withStackNonStackables(boolean stackNonStackables) {
        this.stackNonStackables = stackNonStackables;
        return this;
    }

    public TFMGMagicInventoryBehavior withCallback(Runnable updateCallback) {
        this.updateCallback = updateCallback == null ? () -> {} : updateCallback;
        return this;
    }

    public TFMGMagicInventoryBehavior allowExtraction(boolean allowExtraction) {
        this.allowExtraction = allowExtraction;
        return this;
    }

    public TFMGMagicInventoryBehavior allowInsertion(boolean allowInsertion) {
        this.allowInsertion = allowInsertion;
        return this;
    }

    public TFMGMagicInventoryBehavior withValidator(BiPredicate<Integer, ItemStack> validator) {
        this.validator = validator == null ? ((x, y) -> true) : validator;
        return this;
    }

    public IItemHandlerModifiable getManagedHandler() {
        return managed;
    }

    @Override
    public BehaviourType<TFMGMagicInventoryBehavior> getType() {
        return type;
    }

    // How does this work?
    // This lies about there being 2 slots
    // Slot at index 0 contains the first item actually present in the inventory
    // Slot at index 1 is always empty
    // This ensures that mods that check for an empty slots to insert items, or an occupied slot to extract them,
    // always find what they need
    //
    // A lot of the calls on slot 0 are forwarded to the main inventory with the MAGIC_SLOT slot
    // Most of the behavior is implemented in the main inventory with a check for the magic slot number,
    // due to the need to access protected class members
    protected static class TFMGManagedInventory implements IItemHandlerModifiable {
        protected final TFMGMagicInventoryBehavior owner;

        public TFMGManagedInventory(TFMGMagicInventoryBehavior owner) {
            this.owner = owner;
        }

        @Override
        public int getSlots() {
            return 2;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            if(slot == 0) {
                return owner.handler.getStackInSlot(0).copy();
            } else if(slot == 1) {
                return ItemStack.EMPTY;
            } else {
                throw new RuntimeException("Slot " + slot + " not in valid range - [0,1)");
            }
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (slot != 0 && slot != 1) {
                throw new RuntimeException("Slot " + slot + " not in valid range - [0,1)");
            }
            return owner.handler.insertItem(MAGIC_SLOT, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != 0 && slot != 1) {
                throw new RuntimeException("Slot " + slot + " not in valid range - [0,1)");
            }
            return owner.handler.extractItem(MAGIC_SLOT, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return owner.handler.getSlotLimit(0);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return owner.handler.isItemValid(slot, stack);
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            if(slot == 0) {
                int count = owner.handler.getStackInSlot(0).getCount();
                int amount = stack.getCount() - count;
                if(amount <= 0) {
                    return;
                }
                owner.handler.insertItem(MAGIC_SLOT, stack.copyWithCount(amount), false);
            } else if(slot == 1) {
                owner.handler.insertItem(MAGIC_SLOT, stack, false);
            } else {
                throw new RuntimeException("Slot " + slot + " not in valid range - [0,1)");
            }
        }
    }

    protected static class TFMGAutoResizeInventory extends TFMGItemBehavior.TFMGInventory {
        public TFMGAutoResizeInventory(TFMGMagicInventoryBehavior owner) {
            super(owner, 1);
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            validateSlotIndex(slot);
            if(stack.isEmpty() && this.stacks.size() > 1) {
                this.stacks.removeFirst();
            } else {
                this.stacks.set(slot, stack);
            }
            onContentsChanged(slot);
        }

        @Override
        protected void validateSlotIndex(int slot) {
            if ((slot < 0 || slot >= stacks.size()) && slot != MAGIC_SLOT) {
                throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
            }
        }

        protected void growSlots() {
            NonNullList<ItemStack> newSlots = NonNullList.withSize(this.stacks.size() + 1, ItemStack.EMPTY);
            for(int i = 0; i < this.stacks.size(); i++) {
                newSlots.set(i, this.stacks.get(i));
            }
            this.stacks = newSlots;
        }

        protected void removeSlot(int slot) {
            NonNullList<ItemStack> newSlots = NonNullList.withSize(this.stacks.size() - 1, ItemStack.EMPTY);
            for(int i = 0; i < newSlots.size(); i++) {
                newSlots.set(i, this.stacks.get(i >= slot ? i + 1 : i));
            }
            this.stacks = newSlots;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(slot != MAGIC_SLOT) {
                return super.insertItem(slot, stack, simulate);
            }
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
            if (!isItemValid(slot, stack)) {
                return stack;
            }
            validateSlotIndex(slot);

            ItemStack input = stack.copy();
            boolean contentsChanged = false;
            int remaining = input.getCount();

            // Attempt to distribute item across existing matching slots
            int i = -1;
            for(ItemStack search : this.stacks) {
                i++;
                if(!search.isEmpty() && !ItemStack.isSameItemSameComponents(input, search)) {
                    continue;
                }
                int limit = getStackLimit(0, input);
                int fitting = limit - search.getCount();
                if(fitting != 0) {
                    int shrinkage = Math.min(fitting, remaining);
                    remaining -= shrinkage;

                    if(!simulate) {
                        var putting = input.copyWithCount(search.getCount() + shrinkage);
                        this.stacks.set(i, putting);
                        contentsChanged = true;
                    }
                    input.shrink(shrinkage);
                }
                if(remaining == 0) {
                    break;
                }
            }

            // Add new slots in case there are still items left, until there are none
            if(!simulate) {
                while (remaining > 0 && this.stacks.size() <= ((TFMGMagicInventoryBehavior) owner).maxSlots) {
                    int max = Math.min(getStackLimit(0, input), input.getCount());
                    growSlots();
                    this.stacks.set(this.stacks.size() - 1, input.copyWithCount(max));
                    remaining -= max;
                    input.shrink(max);
                    contentsChanged = true;
                }

                if(contentsChanged) {
                    onContentsChanged(0);
                }
            }

            return input.isEmpty() ? ItemStack.EMPTY : input;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(slot != MAGIC_SLOT) {
                return super.extractItem(slot, amount, simulate);
            }
            if (amount == 0) {
                return ItemStack.EMPTY;
            }
            validateSlotIndex(slot);

            boolean contentsChanged = false;
            int remaining = amount;
            ItemStack firstFound = null;

            // Search for existing matching
            int i = -1;
            for (ItemStack search : this.stacks) {
                i++;
                if ((firstFound != null && !ItemStack.isSameItemSameComponents(firstFound, search)) || search.isEmpty()) {
                    continue;
                }
                int extractable = Math.min(search.getCount(), search.getMaxStackSize());
                if (extractable != 0) {
                    if(firstFound == null) {
                        firstFound = search.copyWithCount(0);
                    }
                    int shrinkage = Math.min(extractable, remaining);
                    remaining -= shrinkage;

                    if (!simulate) {
                        this.stacks.set(i, search.copyWithCount(search.getCount() - shrinkage));
                        contentsChanged = true;
                    }
                    firstFound.grow(shrinkage);
                }
                if(firstFound != null && firstFound.getCount() == firstFound.getMaxStackSize()) {
                    break;
                }
                if (remaining == 0) {
                    break;
                }
            }

            if(!simulate) {
                int size = this.stacks.size();

                // Search for empty slots and remove them
                for(i = 1; i < size; i++) {
                    if(stacks.get(i).isEmpty()) {
                        removeSlot(i);
                        size -= 1;
                        i -= 1;
                    }
                }

                if(contentsChanged) {
                    onContentsChanged(0);
                }
            }

            return firstFound == null || firstFound.isEmpty() ? ItemStack.EMPTY : firstFound;
        }
    }
}
