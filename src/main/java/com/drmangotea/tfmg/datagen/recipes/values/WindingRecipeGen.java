package com.drmangotea.tfmg.datagen.recipes.values;

import com.drmangotea.tfmg.datagen.recipes.TFMGProcessingRecipeGen;
import com.drmangotea.tfmg.registry.TFMGFluids;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.drmangotea.tfmg.registry.TFMGRecipeTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;


public class WindingRecipeGen extends TFMGProcessingRecipeGen {

	GeneratedRecipe


	COPPER_COIL = create("copper_coil", b ->b
			.require(Blocks.GOLD_BLOCK.asItem())
			.require(TFMGItems.COPPER_SPOOL)
			.output(TFMGItems.COAL_COKE)
			.duration(200))
;
	public WindingRecipeGen(PackOutput output) {
		super(output);
	}

	@Override
	protected TFMGRecipeTypes getRecipeType() {
		return TFMGRecipeTypes.WINDING;
	}

}