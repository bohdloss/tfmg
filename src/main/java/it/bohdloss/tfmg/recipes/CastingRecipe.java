package it.bohdloss.tfmg.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import it.bohdloss.tfmg.base.TFMGRecipeWrapper;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CastingRecipe extends StandardProcessingRecipe<TFMGRecipeWrapper> {
    public CastingRecipe(ProcessingRecipeParams params) {
        super(TFMGRecipeTypes.CASTING, params);
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 1;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 0;
    }

    public FluidIngredient getIngredient(){
        return fluidIngredients.get(0);
    }

    @Override
    protected int getMaxInputCount() {
        return 0;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    @Override
    public boolean matches(@NotNull TFMGRecipeWrapper inv, @NotNull Level level) {
        if (inv.isEmpty()) {
            return false;
        }
        return fluidIngredients.get(0).test(inv.getFluid(0));
    }
}
