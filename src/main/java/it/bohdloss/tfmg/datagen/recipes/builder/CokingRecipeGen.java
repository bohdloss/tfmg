package it.bohdloss.tfmg.datagen.recipes.builder;

import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import it.bohdloss.tfmg.recipes.CokingRecipe;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public abstract class CokingRecipeGen extends StandardProcessingRecipeGen<CokingRecipe> {
    public CokingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    @Override
    protected TFMGRecipeTypes getRecipeType() {
        return TFMGRecipeTypes.COKING;
    }
}