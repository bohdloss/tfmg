package it.bohdloss.tfmg.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import it.bohdloss.tfmg.base.TFMGRecipeWrapper;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class HotBlastRecipe extends StandardProcessingRecipe<TFMGRecipeWrapper> {

    public HotBlastRecipe(ProcessingRecipeParams params) {
        super(TFMGRecipeTypes.HOT_BLAST, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 0;
    }

    @Override
    protected int getMaxOutputCount() {
        return 0;
    }
    @Override
    protected int getMaxFluidOutputCount() {
        return 2;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 2;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    public FluidStack getPrimaryResult(){
        return getFluidResults().get(0);
    }
    public FluidStack getSecondaryResult(){
        return getFluidResults().get(1);
    }

    public FluidIngredient getPrimaryIngredient(){
        return getFluidIngredients().get(0);
    }
    public FluidIngredient getSecondaryIngredient(){
        return getFluidIngredients().get(1);
    }


    @Override
    public boolean matches(TFMGRecipeWrapper inv, @NotNull Level worldIn) {
        if (inv.isEmpty()) {
            return false;
        }
        return getPrimaryIngredient().test(inv.getFluid(0)) && getSecondaryIngredient().test(inv.getFluid(1));
    }



}
