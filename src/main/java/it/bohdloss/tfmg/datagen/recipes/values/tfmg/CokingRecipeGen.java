package it.bohdloss.tfmg.datagen.recipes.values.tfmg;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import it.bohdloss.tfmg.datagen.recipes.TFMGProcessingRecipeGen;
import it.bohdloss.tfmg.registry.TFMGFluids;
import it.bohdloss.tfmg.registry.TFMGItems;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.concurrent.CompletableFuture;

import static it.bohdloss.tfmg.datagen.recipes.TFMGRecipeProvider.I.log;

public class CokingRecipeGen extends TFMGProcessingRecipeGen {
    GeneratedRecipe

    COAL_COKE = createSingleIngredient("coal", b -> b
            .require(Ingredient.of(new ItemStack(Items.COAL, 5)))
            .output(new FluidStack(TFMGFluids.CREOSOTE, 1))
            .output(new FluidStack(TFMGFluids.CARBON_DIOXIDE, 30))
            .output(TFMGItems.COAL_COKE)
            .duration(20 * 60)),

    CHARCOAL = createSingleIngredient("charcoal", b ->b
            .require(log())
            .output(new FluidStack(TFMGFluids.CREOSOTE, 2))
            .output(new FluidStack(TFMGFluids.CARBON_DIOXIDE, 20))
            .output(Items.CHARCOAL)
            .duration(20 * 30));

    public CokingRecipeGen(PackOutput generator, CompletableFuture<HolderLookup.Provider> registries) {
        super(generator, registries);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return TFMGRecipeTypes.COKING;
    }
}
