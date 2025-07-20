package it.bohdloss.tfmg.datagen.recipes.values.create;

import com.simibubi.create.api.data.recipe.CompactingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.registry.TFMGItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static it.bohdloss.tfmg.datagen.recipes.TFMGRecipeProvider.F.heavyOil;
import static it.bohdloss.tfmg.datagen.recipes.TFMGRecipeProvider.I.*;

public class TFMGCompactingRecipeGen extends CompactingRecipeGen {
    GeneratedRecipe
            BITUMEN = create("bitumen", b -> b
            .require(heavyOil(),1000)
            .output(bitumen(), 1)
            .requiresHeat(HeatCondition.HEATED)
    ),
            CINDERFLOURBLOCK = create("cinderflourblock", b -> b
                    .require(cinderFlour())
                    .require(cinderFlour())
                    .require(cinderFlour())
                    .require(cinderFlour())
                    .output(TFMGItems.CINDERFLOURBLOCK)
            ),
            CAST_IRON = create("cast_iron", b -> b
                    .require(ironIngot())
                    .require(coal())
                    .output(TFMGItems.CAST_IRON_INGOT, 1)
                    .requiresHeat(HeatCondition.HEATED)
            );

    public TFMGCompactingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, TFMG.MOD_ID);
    }
}
