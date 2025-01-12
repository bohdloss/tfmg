package com.drmangotea.tfmg.datagen.recipes.values;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider;
import com.drmangotea.tfmg.datagen.recipes.builder.IndustrialBlastingRecipeBuilder;
import com.drmangotea.tfmg.datagen.recipes.builder.VatMachineRecipeBuilder;
import com.drmangotea.tfmg.registry.TFMGFluids;
import com.drmangotea.tfmg.registry.TFMGItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;

import static com.drmangotea.tfmg.datagen.recipes.builder.VatMachineRecipeBuilder.VatRecipeParams;

public class VatRecipeGen extends TFMGRecipeProvider {
    public VatRecipeGen(PackOutput output) {
        super(output);
    }

    GeneratedRecipe
        CONCRETE = createVatRecipe("concrete", b -> (VatMachineRecipeBuilder) b
            .require(Blocks.SAND.asItem())
            .require(Blocks.GRAVEL.asItem())
            .require(TFMGItems.LIMESAND)
            .require(Fluids.WATER, 250)
            .output(TFMGFluids.LIQUID_CONCRETE.get(),1000)
            , mixing()),
        DEBUG = createVatRecipe("debug", b -> (VatMachineRecipeBuilder) b
            .require(Blocks.GOLD_BLOCK.asItem())
            .require(TFMGItems.CINDERBLOCK)
            .output(TFMGItems.ALUMINUM_INGOT,2)
            , mixing()),
        DEBUG_2 = createVatRecipe("debug_2", b -> (VatMachineRecipeBuilder) b
                .require(TFMGItems.ALUMINUM_INGOT)
                .require(TFMGItems.CINDERFLOURBLOCK)
                .output(TFMGItems.BITUMEN,2)
                , mixing())
    ;



    ///////
    public VatRecipeParams electrolysis(){
        VatRecipeParams params = new VatRecipeParams();
        params.machines.add("tfmg:electrode");
        params.machines.add("tfmg:electrode");
        return params;
    }
    public VatRecipeParams mixing(){
        VatRecipeParams params = new VatRecipeParams();
        params.machines.add("tfmg:mixing");
        return params;
    }
    public VatRecipeParams mixingWithCatalyst(String catalyst){
        VatRecipeParams params = new VatRecipeParams();
        params.machines.add("tfmg:mixing");
        params.machines.add("tfmg:catalyst_"+catalyst);
        return params;
    }
    public VatRecipeParams centrifuge(){
        VatRecipeParams params = new VatRecipeParams();
        params.machines.add("tfmg:centrifuge");
        return params;
    }
    public VatRecipeParams freezing(){
        VatRecipeParams params = new VatRecipeParams();
        params.machines.add("tfmg:freezing");
        return params;
    }
    public VatRecipeParams intenseFreezing(){
        VatRecipeParams params = new VatRecipeParams();
        params.machines.add("tfmg:freezing");
        params.machines.add("tfmg:freezing");
        params.machines.add("tfmg:freezing");
        return params;
    }
    public VatRecipeParams arcBlasting(){
        VatRecipeParams params = new VatRecipeParams();
        params.machines.add("tfmg:graphite_electrode");
        params.machines.add("tfmg:graphite_electrode");
        params.machines.add("tfmg:graphite_electrode");
        params.minSize = 9;
        params.allowedVatTypes = new ArrayList<>();
        params.allowedVatTypes.add(TFMG.asResource("tfmg:firebrick_lined_vat").getPath());
        return params;
    }
}
