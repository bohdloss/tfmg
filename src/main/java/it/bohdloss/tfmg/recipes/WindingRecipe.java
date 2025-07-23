package it.bohdloss.tfmg.recipes;

import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.*;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.base.IWindable;
import it.bohdloss.tfmg.base.TFMGRecipeWrapper;
import it.bohdloss.tfmg.recipes.jei.WindingCategory;
import it.bohdloss.tfmg.registry.TFMGBlocks;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class WindingRecipe extends StandardProcessingRecipe<TFMGRecipeWrapper> implements IAssemblyRecipe {
    public WindingRecipe(ProcessingRecipeParams params) {
        super(TFMGRecipeTypes.WINDING, params);
    }

    @Override
    public @NotNull List<String> validate() {
        List<String> errors = super.validate();

        if(ingredients.size() != 2) {
            errors.add("Winding recipes must have exactly 2 inputs, the item and the spool");
        }

        for(ItemStack itemStack : ingredients.get(1).getItems()) {
            if(!(itemStack.getItem() instanceof IWindable)) {
                errors.add("The input spool item must implement IWindable (got " + itemStack.getItem() + ", which does not)");
            }
        }

        for(ProcessingOutput result : results) {
            ItemStack output = result.getStack();
            if(!(output.getItem() instanceof IWindable)) {
                errors.add("The output item of a winding recipe must implement IWindable (got " + output.getItem() + ", which does not)");
            }
        }

        return errors;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    @Override
    protected int getMaxInputCount() {
        return 2;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    public Ingredient getIngredient() {
        return getIngredients().get(0);
    }

    public Ingredient getSpool() {
        return getIngredients().get(1);
    }

    @Override
    public boolean matches(TFMGRecipeWrapper inv, @NotNull Level worldIn) {
        if (inv.isEmpty())
            return false;
        return getIngredient().test(inv.getItem(0)) &&
                getSpool().test(inv.getItem(1));
    }

    @Override
    public Component getDescriptionForAssembly() {
        return CreateLang.translateDirect("recipe.assembly.winding");
    }

    @Override
    public void addRequiredMachines(Set<ItemLike> list) {
        list.add(TFMGBlocks.WINDING_MACHINE.get());
    }

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {
        list.add(ingredients.get(1));
    }

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> WindingCategory.AssemblyWinding::new;
    }
}
