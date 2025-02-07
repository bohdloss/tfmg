package com.drmangotea.tfmg.datagen.recipes.values;

import com.drmangotea.tfmg.datagen.recipes.TFMGProcessingRecipeGen;
import com.drmangotea.tfmg.registry.TFMGFluids;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.drmangotea.tfmg.registry.TFMGRecipeTypes;
import com.drmangotea.tfmg.registry.TFMGTags;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fluids.FluidStack;

import static com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider.F.*;

public class HotBlastRecipeGen extends TFMGProcessingRecipeGen {

	GeneratedRecipe


	HOT_AIR = create("hot_air", b ->b
			.require(air(),5)
			.require(TFMGTags.TFMGFluidTags.BLAST_STOVE_FUEL.tag,5)
			.output(heatedAir(), 5)
			.output(carbonDioxide(), 5)
			.duration(200));
	public HotBlastRecipeGen(PackOutput output) {
		super(output);
	}

	@Override
	protected TFMGRecipeTypes getRecipeType() {
		return TFMGRecipeTypes.HOT_BLAST;
	}

}