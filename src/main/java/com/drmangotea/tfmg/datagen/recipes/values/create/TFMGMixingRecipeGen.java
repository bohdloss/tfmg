package com.drmangotea.tfmg.datagen.recipes.values.create;

import com.drmangotea.tfmg.datagen.recipes.TFMGProcessingRecipeGen;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.minecraft.data.PackOutput;
import static com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider.I.*;
import static com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider.F.*;
public class TFMGMixingRecipeGen extends TFMGProcessingRecipeGen {

	GeneratedRecipe

	THERMITE = create("thermite", b -> b
			.require(AllPaletteStoneTypes.CRIMSITE.getBaseBlock().get())
			.require(AllPaletteStoneTypes.CRIMSITE.getBaseBlock().get())
			.require(aluminumIngot())
			.require(aluminumIngot())
			.output(TFMGItems.THERMITE_POWDER)
	),

	CEMENT = create("cement", b -> b
			.require(clayBall())
			.require(limesand())
			.output(cement(),4)
	),

	ASPHALT_MIXTURE = create("asphalt_mixture", b -> b
			.require(sand())
			.require(bitumen())
			.require(gravel())
			.output(concreteMixture(),16)
	),

	ASPHALT_MIXTURE_FROM_SLAG = create("asphalt_mixture_from_slag", b -> b
			.require(slag())
			.require(bitumen())
			.require(gravel())
			.output(concreteMixture(),32)
	),

	CONCRETE_MIXTURE = create("concrete_mixture", b -> b
			.require(sand())
			.require(cement())
			.require(gravel())
			.output(concreteMixture(),16)
	),

	CONCRETE_MIXTURE_FROM_SLAG = create("concrete_mixture_from_slag", b -> b
			.require(slag())
			.require(cement())
			.require(gravel())
			.output(concreteMixture(),32)
	),

	COPPER_SULFATE = create("copper_sulfate", b -> b
			.require(sulfuricAcid(),500)
			.require(copperIngot())
			.output(copperSulfate())
	),

	LIQUID_CONCRETE = create("liquid_concrete", b -> b
			.require(water(),250)
			.require(concreteMixture())
			.output(liquidConcrete(),1000)
	),

	LIQUID_ASPHALT = create("liquid_asphalt", b -> b
			.require(water(),250)
			.require(asphaltMixture())
			.output(liquidAsphalt(),1000)
	),

	GUNPOWDER = create("gunpowder", b -> b
			.require(nitrateDust())
			.require(nitrateDust())
			.require(nitrateDust())
			.require(charcoal())
			.require(charcoal())
			.require(sulfurDust())
			.output(gunpowder(),3)
	),

	NAPALM = create("napalm", b -> b
			.require(gasoline(),1000)
			.require(aluminumIngot())
				.output(napalm(),250)
	),

	COOLING_FLUID = create("cooling_fluid", b -> b
			.require(water(),250)
			.require(ethylene(),1000)
			.output(coolingFluid(),250)
	),

	MAGNETIC_ALLOY = create("magnetic_alloy", b -> b
			.require(nickelIngot())
			.require(nickelIngot())
			.require(steelIngot())
			.require(steelIngot())
			.output(magneticIngot())
			.duration(300)
			.requiresHeat(HeatCondition.HEATED)
	);



	public TFMGMixingRecipeGen(PackOutput output) {
		super(output);
	}

	@Override
	protected AllRecipeTypes getRecipeType() {
		return AllRecipeTypes.MIXING;
	}

}