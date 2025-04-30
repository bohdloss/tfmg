package com.drmangotea.tfmg.datagen.recipes.values;

import com.drmangotea.tfmg.datagen.recipes.TFMGProcessingRecipeGen;
import com.drmangotea.tfmg.registry.TFMGFluids;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.drmangotea.tfmg.registry.TFMGRecipeTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;

import static com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider.I.log;

public class CastingRecipeGen extends TFMGProcessingRecipeGen {

	GeneratedRecipe

	STEEL_INGOT = create("steel", b ->b
			.require(TFMGFluids.MOLTEN_STEEL.get(),144)
			.output(TFMGItems.STEEL_INGOT)
			.duration(60)),

	SILICON = create("silicon", b ->b
			.require(TFMGFluids.LIQUID_SILICON.get(),144)
			.output(TFMGItems.SILICON_INGOT)
			.duration(60));
;
	public CastingRecipeGen(PackOutput output) {
		super(output);
	}

	@Override
	protected TFMGRecipeTypes getRecipeType() {
		return TFMGRecipeTypes.CASTING;
	}

}