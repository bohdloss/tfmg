package it.bohdloss.tfmg.datagen.recipes.builder;

import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import it.bohdloss.tfmg.recipes.CastingRecipe;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class CastingRecipeGen extends StandardProcessingRecipeGen<CastingRecipe> {
    public CastingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return TFMGRecipeTypes.CASTING;
    }
}
