package it.bohdloss.tfmg.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class DistillationRecipe extends StandardProcessingRecipe<DistillationRecipeInput> {
    public static final int MAX_OUTPUTS = 6;

    public DistillationRecipe(ProcessingRecipeParams params) {
        super(TFMGRecipeTypes.DISTILLATION, params);
    }
    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    @Override
    protected boolean canRequireHeat() {
        return true;
    }

    public FluidIngredient getInputFluid(){
        return getFluidIngredients().get(0);
    }

    @Override
    public int getMaxFluidOutputCount() {
        return MAX_OUTPUTS;
    }

    @Override
    public int getMaxInputCount() {
        return 0;
    }

    @Override
    public int getMaxFluidInputCount() {
        return 1;
    }

    @Override
    public int getMaxOutputCount() {
        return 0;
    }

    public FluidStack getFirstFluidResult(){
        return fluidResults.get(0);
    }

    public int getOutputCount(){
        return fluidResults.size();
    }

    @Override
    public boolean matches(@NotNull DistillationRecipeInput pContainer, @NotNull Level pLevel) {
        return !pContainer.getFluid(0).isEmpty() &&
                getInputFluid().test(pContainer.getFluid(0)) &&
                pContainer.getOutputCount() == getOutputCount();
    }
}
