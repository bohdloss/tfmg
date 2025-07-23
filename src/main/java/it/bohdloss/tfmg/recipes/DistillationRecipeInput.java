package it.bohdloss.tfmg.recipes;

import it.bohdloss.tfmg.base.TFMGRecipeWrapper;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.function.Supplier;

public class DistillationRecipeInput extends TFMGRecipeWrapper {
    protected final Supplier<Integer> outputCount;

    public DistillationRecipeInput(IItemHandler items, IFluidHandler fluids, Supplier<Integer> outputCount) {
        super(items, fluids);
        this.outputCount = outputCount;
    }

    public int getOutputCount() {
        return outputCount.get();
    }
}
