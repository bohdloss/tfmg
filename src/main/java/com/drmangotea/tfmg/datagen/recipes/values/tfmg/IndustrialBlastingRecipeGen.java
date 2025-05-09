package com.drmangotea.tfmg.datagen.recipes.values.tfmg;

import com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider;
import com.drmangotea.tfmg.datagen.recipes.builder.IndustrialBlastingRecipeBuilder;
import com.drmangotea.tfmg.registry.TFMGFluids;
import com.drmangotea.tfmg.registry.TFMGTags;
import com.simibubi.create.AllItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import static com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider.I.ironDust;


public class IndustrialBlastingRecipeGen extends TFMGRecipeProvider{


    GeneratedRecipe

    STEEL = createIndustrialBlastingRecipe("steel", b -> (IndustrialBlastingRecipeBuilder) b
            .require(AllItems.CRUSHED_IRON)
            .require(TFMGTags.TFMGItemTags.FLUX.tag)
            .output(TFMGFluids.MOLTEN_STEEL.get(),144)
            .output(TFMGFluids.MOLTEN_SLAG.get(),144)
            .output(TFMGFluids.FURNACE_GAS.get(),20)
            .duration(60)
            , 20),
    STEEL_DOUBLE = createIndustrialBlastingRecipe("steel_from_raw_iron", b -> (IndustrialBlastingRecipeBuilder) b
            .require(Items.RAW_IRON)
            .require(Ingredient.of(TFMGTags.TFMGItemTags.FLUX.tag))
            .require(Ingredient.of(TFMGTags.TFMGItemTags.FLUX.tag))
            .output(TFMGFluids.MOLTEN_STEEL.get(),288)
            .output(TFMGFluids.MOLTEN_SLAG.get(),288)
            .output(TFMGFluids.FURNACE_GAS.get(),20)
            .duration(120)
            , 30),


    STEEL_DUST = createIndustrialBlastingRecipe("steel_from_dust", b -> (IndustrialBlastingRecipeBuilder) b
            .require(ironDust())
            .require(TFMGTags.TFMGItemTags.FLUX.tag)
            .output(TFMGFluids.MOLTEN_STEEL.get(),144)
            .output(TFMGFluids.MOLTEN_SLAG.get(),144)
            .output(TFMGFluids.FURNACE_GAS.get(),20)
            .duration(50)
    , 20)



    ;


    public IndustrialBlastingRecipeGen(PackOutput output) {super(output);}


}