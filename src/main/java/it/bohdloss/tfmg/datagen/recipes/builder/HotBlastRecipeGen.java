package it.bohdloss.tfmg.datagen.recipes.builder;

import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import it.bohdloss.tfmg.recipes.HotBlastRecipe;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public abstract class HotBlastRecipeGen extends StandardProcessingRecipeGen<HotBlastRecipe> {
    public HotBlastRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    @Override
    protected TFMGRecipeTypes getRecipeType() {
        return TFMGRecipeTypes.HOT_BLAST;
    }
}