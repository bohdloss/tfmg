package com.drmangotea.tfmg.datagen.recipes.values;

import com.drmangotea.tfmg.datagen.recipes.TFMGProcessingRecipeGen;
import com.simibubi.create.AllRecipeTypes;
import net.minecraft.data.PackOutput;

import static com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider.I.*;

public class TFMGPressingRecipeGen extends TFMGProcessingRecipeGen {

    GeneratedRecipe

            CAST_IRON_SHEET = create("cast_iron_ingot", b -> b.require(castIronIngot())
            .output(castIronSheetTFMG()).duration(50)),

    ALUMINUM_SHEET = create("aluminum_ingot", b -> b.require(aluminumIngot())
            .output(aluminumSheetTFMG()).duration(50)),

    LEAD_SHEET = create("lead_ingot", b -> b.require(leadIngot())
            .output(leadSheetTFMG()).duration(50)),

    NICKEL_SHEET = create("nickel_ingot", b -> b.require(nickelIngot())
            .output(nickelSheetTFMG()).duration(50));

    public TFMGPressingRecipeGen(PackOutput output) {
        super(output);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.PRESSING;
    }

}