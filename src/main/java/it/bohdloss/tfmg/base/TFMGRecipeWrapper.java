package it.bohdloss.tfmg.base;

import net.createmod.catnip.data.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class TFMGRecipeWrapper extends TFMGRecipeInput {
    private final IItemHandler items;
    private final IFluidHandler  fluids;

    public TFMGRecipeWrapper(IItemHandler items, IFluidHandler fluids) {
        this.items = items;
        this.fluids = fluids;
    }

    @Override
    public FluidStack getFluid(int i) {
        return fluids == null ? FluidStack.EMPTY : fluids.getFluidInTank(i);
    }

    @Override
    public int fluidSize() {
        return fluids == null ? 0 : fluids.getTanks();
    }

    @Override
    public void useInputs(NonNullList<Pair<Integer, Integer>> shrinkItems, NonNullList<Pair<Fluid, Integer>> drainFluids) {
        if(items != null) {
            for (Pair<Integer, Integer> pair : shrinkItems) {
                items.extractItem(pair.getFirst(), pair.getSecond(), false);
            }
        }

        if(fluids != null) {
            for (Pair<Fluid, Integer> pair : drainFluids) {
                fluids.drain(new FluidStack(pair.getFirst(), pair.getSecond()), IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    public @NotNull ItemStack getItem(int slot) {
        return items == null ? ItemStack.EMPTY : items.getStackInSlot(slot);
    }

    public int size() {
        return items == null ? 0 : items.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for(int i = 0; i < this.size(); ++i) {
            if (!this.getItem(i).isEmpty()) {
                return false;
            }
        }

        for(int i = 0; i < this.fluidSize(); ++i) {
            if (!this.getFluid(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
