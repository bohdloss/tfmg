package com.drmangotea.tfmg.datagen.recipes.values;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider;
import com.drmangotea.tfmg.datagen.recipes.builder.IndustrialBlastingRecipeBuilder;
import com.drmangotea.tfmg.datagen.recipes.builder.VatMachineRecipeBuilder;
import com.drmangotea.tfmg.registry.TFMGFluids;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.drmangotea.tfmg.registry.TFMGTags;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;

import static com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider.I.*;
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
                    .output(TFMGFluids.LIQUID_CONCRETE.get(), 1000)
            , mixing()),
            DEBUG = createVatRecipe("debug", b -> (VatMachineRecipeBuilder) b
                            .require(Blocks.GOLD_BLOCK.asItem())
                            .require(TFMGItems.CINDERBLOCK)
                            .output(TFMGItems.ALUMINUM_INGOT, 2)
                    , mixing()),
            DEBUG_2 = createVatRecipe("debug_2", b -> (VatMachineRecipeBuilder) b
                            .require(TFMGItems.ALUMINUM_INGOT)
                            .require(TFMGItems.CINDERFLOURBLOCK)
                            .output(TFMGItems.BITUMEN, 2)
                    , electrolysis()),
            ARC_FURNACE_STEEL = createVatRecipe("arc_furnace_steel", b -> (VatMachineRecipeBuilder) b
                            .require(crushedRawIron())
                            .require(TFMGTags.TFMGItemTags.FLUX.tag)
                            .require(TFMGTags.TFMGItemTags.BLAST_FURNACE_FUEL.tag)
                            .output(TFMGItems.STEEL_INGOT, 2)
                    , arcBlasting()),
            ETCHED_CIRCUIT_BOARD = createVatRecipe("etched_circuit_board", b -> (VatMachineRecipeBuilder) b
                            .require(TFMGItems.COATED_CIRCUIT_BOARD)
                            .require(Fluids.WATER.getSource(), 100)
                            .output(TFMGItems.ETCHED_CIRCUIT_BOARD)
                            .duration(100)
                    , new VatRecipeParams()),
            DEBUG_3 = createVatRecipe("debug_3", b -> (VatMachineRecipeBuilder) b
                            .require(Blocks.GOLD_BLOCK.asItem())
                            .require(TFMGFluids.LIQUID_CONCRETE.getSource(), 1)
                            .require(TFMGFluids.HEAVY_OIL.getSource(), 1)
                            .output(TFMGItems.ALUMINUM_INGOT, 2)
                    , mixing()),
            DEBUG_4 = createVatRecipe("debug_4", b -> (VatMachineRecipeBuilder) b
                            .require(Blocks.GOLD_BLOCK.asItem())
                            .require(Blocks.DIAMOND_BLOCK.asItem())
                            .require(TFMGFluids.LIQUID_CONCRETE.getSource(), 1)
                            .require(TFMGFluids.HEAVY_OIL.getSource(), 1)
                            .require(TFMGFluids.HEAVY_OIL.getSource(), 1)
                            .output(TFMGItems.ALUMINUM_INGOT, 2)
                    , mixing()),
            DEBUG_5 = createVatRecipe("debug_5", b -> (VatMachineRecipeBuilder) b
                            .require(Blocks.GOLD_BLOCK.asItem())
                            .require(Blocks.DIAMOND_BLOCK.asItem())
                            .require(Blocks.IRON_BLOCK.asItem())
                            .require(Blocks.COAL_BLOCK.asItem())
                            .require(TFMGFluids.LIQUID_CONCRETE.getSource(), 1)
                            .require(TFMGFluids.HEAVY_OIL.getSource(), 1)
                            .require(TFMGFluids.COOLING_FLUID.getSource(), 1)
                            .require(TFMGFluids.CRUDE_OIL.getSource(), 1)
                            .output(TFMGFluids.LIQUID_CONCRETE.get(), 1)
                            .output(TFMGFluids.HEAVY_OIL.get(), 1)
                            .output(TFMGFluids.COOLING_FLUID.get(), 1)
                            .output(TFMGFluids.CRUDE_OIL.get(), 1)
                            .output(Items.EGG)
                            .output(Items.ARROW)
                            .output(Items.DIAMOND)
                            .output(Items.STRING)
                    , mixing());


    /// ////
    public VatRecipeParams electrolysis() {
        VatRecipeParams params = new VatRecipeParams();
        params.machines.add("tfmg:electrode");
        params.machines.add("tfmg:electrode");
        return params;
    }

    public VatRecipeParams mixing() {
        VatRecipeParams params = new VatRecipeParams();
        params.machines.add("tfmg:mixing");
        return params;
    }

    public VatRecipeParams centrifuge() {
        VatRecipeParams params = new VatRecipeParams();
        params.machines.add("tfmg:centrifuge");
        return params;
    }

    public VatRecipeParams freezing() {
        VatRecipeParams params = new VatRecipeParams();
        params.machines.add("tfmg:freezing");
        return params;
    }

    public VatRecipeParams intenseFreezing() {
        VatRecipeParams params = new VatRecipeParams();
        params.machines.add("tfmg:freezing");
        params.machines.add("tfmg:freezing");
        params.machines.add("tfmg:freezing");
        return params;
    }

    public VatRecipeParams arcBlasting() {
        VatRecipeParams params = new VatRecipeParams();
        params.machines.add("tfmg:graphite_electrode");
        params.machines.add("tfmg:graphite_electrode");
        params.machines.add("tfmg:graphite_electrode");
        params.minSize = 9;
        params.allowedVatTypes = new ArrayList<>();
        params.allowedVatTypes.add(TFMG.asResource("firebrick_lined_vat").getPath());
        return params;
    }
}
