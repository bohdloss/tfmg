package it.bohdloss.tfmg.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class CokingRecipe extends StandardProcessingRecipe<RecipeInput> {
    public CokingRecipe(ProcessingRecipeParams params) {
        super(TFMGRecipeTypes.COKING, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }
    
    @Override
    protected int getMaxFluidOutputCount() {
        return 2;
    }

    @Override
    protected boolean canRequireHeat() {
        return false;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    public FluidStack getPrimaryResult() {
        return getFluidResults().get(0);
    }
    
    public FluidStack getSecondaryResult() {
        return getFluidResults().get(1);
    }
    
    @Override
    public boolean matches(RecipeInput inv, @NotNull Level worldIn) {
        if (inv.isEmpty()) {
            return false;
        }
        return ingredients.get(0).test(inv.getItem(0));
    }
}
