package com.drmangotea.tfmg.datagen.recipes.values;

import com.drmangotea.tfmg.datagen.recipes.TFMGProcessingRecipeGen;
import com.simibubi.create.AllRecipeTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;


public class TFMGPressingRecipeGen extends TFMGProcessingRecipeGen {

	//GeneratedRecipe

//	SUGAR_CANE = create(() -> Items.SUGAR_CANE, b -> b.output(Items.PAPER))
	//,


		//BRASS = create("brass_ingot", b -> b.require(brass())
		//	.output(AllItems.BRASS_SHEET.get()).duration(50))

	//;

	public TFMGPressingRecipeGen(PackOutput output) {
		super(output);
	}

	@Override
	protected AllRecipeTypes getRecipeType() {
		return AllRecipeTypes.PRESSING;
	}

}