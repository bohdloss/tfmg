package it.bohdloss.tfmg.datagen.recipes.values.create;

import com.simibubi.create.api.data.recipe.PressingRecipeGen;
import it.bohdloss.tfmg.TFMG;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static it.bohdloss.tfmg.datagen.recipes.TFMGRecipeProvider.I.*;

public class TFMGPressingRecipeGen extends PressingRecipeGen {
    GeneratedRecipe

            CAST_IRON_SHEET = create("cast_iron_ingot", b -> b.require(castIronIngot())
            .output(castIronSheetTFMG())),

    ALUMINUM_SHEET = create("aluminum_ingot", b -> b.require(aluminumIngot())
            .output(aluminumSheetTFMG())),

    LEAD_SHEET = create("lead_ingot", b -> b.require(leadIngot())
            .output(leadSheetTFMG())),

    NICKEL_SHEET = create("nickel_ingot", b -> b.require(nickelIngot())
            .output(nickelSheetTFMG())),

    SYNTHETIC_LEATHER = create("synthetic_leather", b -> b
            .require(rubber())
            .output(syntheticLeather())
    );

    public TFMGPressingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, TFMG.MOD_ID);
    }
}
