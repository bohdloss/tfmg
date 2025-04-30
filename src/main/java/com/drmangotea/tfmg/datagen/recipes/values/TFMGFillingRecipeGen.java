package com.drmangotea.tfmg.datagen.recipes.values;

import com.drmangotea.tfmg.datagen.recipes.TFMGProcessingRecipeGen;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import com.drmangotea.tfmg.registry.TFMGFluids;
import com.simibubi.create.AllRecipeTypes;
import net.minecraft.data.PackOutput;

public class TFMGFillingRecipeGen extends TFMGProcessingRecipeGen {

    GeneratedRecipe

            HARDENED_PLANKS = create("hardened_planks", b -> b
            .require(TFMGFluids.CREOSOTE.getSource(), 250)
            .output(TFMGBlocks.HARDENED_PLANKS));


    public TFMGFillingRecipeGen(PackOutput output) {
        super(output);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.FILLING;
    }

}