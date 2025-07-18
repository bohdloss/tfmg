package it.bohdloss.tfmg.base;

import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import it.bohdloss.tfmg.DebugStuff;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public abstract class TFMGRecipeInput implements RecipeInput {
    public abstract FluidStack getFluid(int i);
    public abstract int fluidSize();

    public abstract void useInputs(
            NonNullList<Pair<Integer, Integer>> shrinkItems,
            NonNullList<Pair<Fluid, Integer>> drainFluids
    );

    public <T extends StandardProcessingRecipe<I>, I extends TFMGRecipeInput> boolean hasIngredients(
            T theRecipe,
            NonNullList<Pair<Integer, Integer>> shrinkItems,
            NonNullList<Pair<Fluid, Integer>> drainFluids
    ) {
        shrinkItems.clear();
        drainFluids.clear();

        // Checks if the input contains all the ingredients and has enough of each
        for(Ingredient ingredient : theRecipe.getIngredients()) {
            boolean found = false;

            matching:
            for(int i = 0; i < this.size(); i++) {
                for(ItemStack matching : ingredient.getItems()) {
                    ItemStack item = this.getItem(i);
                    if (item.getItem() == matching.getItem() && item.getCount() >= matching.getCount()) {
                        shrinkItems.add(Pair.of(i, matching.getCount())); // Item slots to shrink when applying recipe
                        found = true;
                        break matching;
                    }
                }
            }

            if(!found) {
                return false;
            }
        }

        // And do the same for fluid ingredients
        for(FluidIngredient ingredient : theRecipe.getFluidIngredients()) {
            boolean found = false;

            matching:
            for(int i = 0; i < this.fluidSize(); i++) {
                for(FluidStack matching : ingredient.getMatchingFluidStacks()) {
                    FluidStack fluid = this.getFluid(i);
                    if (fluid.getFluid().isSame(matching.getFluid()) && fluid.getAmount() >= matching.getAmount()) {
                        drainFluids.add(Pair.of(fluid.getFluid(), matching.getAmount())); // Fluid tanks to shrink when applying recipe
                        found = true;
                        break matching;
                    }
                }
            }

            if(!found) {
                return false;
            }
        }

        return true;
    }
}
