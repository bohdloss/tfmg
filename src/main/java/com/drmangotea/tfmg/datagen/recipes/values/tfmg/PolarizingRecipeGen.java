package com.drmangotea.tfmg.datagen.recipes.values.tfmg;

import com.drmangotea.tfmg.datagen.recipes.TFMGProcessingRecipeGen;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.drmangotea.tfmg.registry.TFMGRecipeTypes;
import net.minecraft.data.PackOutput;

public class PolarizingRecipeGen extends TFMGProcessingRecipeGen {

	GeneratedRecipe

	MAGNET = create("magnet", b ->b
			.require(TFMGItems.MAGNETIC_ALLOY_INGOT)
			.output(TFMGItems.MAGNET));
;
	public PolarizingRecipeGen(PackOutput output) {
		super(output);
	}

	@Override
	protected TFMGRecipeTypes getRecipeType() {
		return TFMGRecipeTypes.POLARIZING;
	}

}