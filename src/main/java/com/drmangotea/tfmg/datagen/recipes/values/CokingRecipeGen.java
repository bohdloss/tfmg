package com.drmangotea.tfmg.datagen.recipes.values;

import com.drmangotea.tfmg.datagen.recipes.TFMGProcessingRecipeGen;
import com.drmangotea.tfmg.registry.TFMGFluids;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.drmangotea.tfmg.registry.TFMGRecipeTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;

import static com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider.I.log;

public class CokingRecipeGen extends TFMGProcessingRecipeGen {

	GeneratedRecipe

	COAL_COKE = create(()-> Items.COAL, b ->b
			.output(new FluidStack(TFMGFluids.CREOSOTE.getSource(), 1))
			.output(new FluidStack(TFMGFluids.CARBON_DIOXIDE.getSource(), 30))
			.output(TFMGItems.COAL_COKE)
			.duration(20*60)),

	CHARCOAL = create("charcoal", b ->b
			.require(log())
			.output(new FluidStack(TFMGFluids.CREOSOTE.getSource(), 2))
			.output(new FluidStack(TFMGFluids.CARBON_DIOXIDE.getSource(), 20))
			.output(Items.CHARCOAL)
			.duration(20*30));
;
	public CokingRecipeGen(PackOutput output) {
		super(output);
	}

	@Override
	protected TFMGRecipeTypes getRecipeType() {
		return TFMGRecipeTypes.COKING;
	}

}