package it.bohdloss.tfmg.datagen.recipes.builder;

import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import it.bohdloss.tfmg.recipes.IndustrialBlastingRecipe;
import it.bohdloss.tfmg.recipes.IndustrialBlastingRecipeParams;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public abstract class IndustrialBlastingRecipeGen extends ProcessingRecipeGen<IndustrialBlastingRecipeParams, IndustrialBlastingRecipe, IndustrialBlastingRecipe.Builder<IndustrialBlastingRecipe>> {
    public IndustrialBlastingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String namespace) {
        super(output, registries, namespace);
    }

    @Override
    protected TFMGRecipeTypes getRecipeType() {
        return TFMGRecipeTypes.INDUSTRIAL_BLASTING;
    }

    @Override
    protected IndustrialBlastingRecipe.Builder<IndustrialBlastingRecipe> getBuilder(ResourceLocation id) {
        return new IndustrialBlastingRecipe.Builder<>(IndustrialBlastingRecipe::new, id);
    }
}