package com.drmangotea.tfmg.datagen.recipes.values;


import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.recipes.WindingRecipe;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import com.drmangotea.tfmg.registry.TFMGFluids;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.UnaryOperator;

import static com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider.I.*;
import static com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider.F.*;


public class TFMGSequencedAssemblyRecipeGen extends CreateRecipeProvider {

    GeneratedRecipe POTENTIOMETER = create("potentiometer", b -> b.require(TFMGBlocks.HEAVY_MACHINERY_CASING.get())
            .transitionTo(TFMGItems.UNFINISHED_POTENTIOMETER.get())
            .addOutput(TFMGBlocks.POTENTIOMETER.get(), 120)
            .addOutput(TFMGBlocks.STEEL_CASING.get(), 8)
            .addOutput(TFMGBlocks.STEEL_COGWHEEL.get(), 8)
            .addOutput(TFMGBlocks.ELECTRIC_POST.get(), 8)
            .loops(3)
            .addStep(WindingRecipe::new, rb -> rb.require(TFMGItems.CONSTANTAN_SPOOL.get()).duration(100))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(TFMGBlocks.STEEL_COGWHEEL))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(TFMGItems.COPPER_WIRE))
            .addStep(FillingRecipe::new, rb -> rb.require(lubricationOil(), 50))),

    GENERATOR = create("generator", b -> b.require(shaft())
            .transitionTo(TFMGItems.UNFINISHED_GENERATOR.get())
            .addOutput(TFMGBlocks.GENERATOR.get(), 120)
            .addOutput(TFMGBlocks.STEEL_CASING.get(), 8)
            .addOutput(TFMGBlocks.STEEL_COGWHEEL.get(), 8)
            .addOutput(TFMGItems.CAPACITOR.get(), 8)
            .loops(5)
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(TFMGItems.CAPACITOR))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(steelSheet()))
            .addStep(WindingRecipe::new, rb -> rb.require(TFMGItems.COPPER_SPOOL.get()).duration(75))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(magnet()))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(steelMechanism()))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(TFMGItems.SCREWDRIVER))
    ),

    MOTOR = create("motor", b -> b.require(shaft())
            .transitionTo(TFMGItems.UNFINISHED_GENERATOR.get())
            .addOutput(TFMGBlocks.ELECTRIC_MOTOR.get(), 120)
            .addOutput(TFMGBlocks.STEEL_CASING.get(), 8)
            .addOutput(TFMGItems.NICKEL_SHEET.get(), 8)
            .loops(5)
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(nickelSheet()))
            .addStep(WindingRecipe::new, rb -> rb.require(TFMGItems.COPPER_SPOOL.get()).duration(75))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(magnet()))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(steelSheet()))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(steelMechanism()))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(TFMGItems.SCREWDRIVER))
    ),

    TRANSISTOR_PLASTIC = create("transistor", b -> b.require(plasticSheet())
            .transitionTo(TFMGItems.UNFINISHED_TRANSISTOR.get())
            .addOutput(new ItemStack(TFMGItems.TRANSISTOR.get(),4), 120)
            .addOutput(TFMGItems.SILICON_INGOT.get(), 8)
            .addOutput(TFMGItems.P_SEMICONDUCTOR.get(), 8)
            .addOutput(TFMGItems.N_SEMICONDUCTOR.get(), 8)
            .loops(1)
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(copperWire()))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(TFMGItems.N_SEMICONDUCTOR))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(TFMGItems.P_SEMICONDUCTOR))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(TFMGItems.N_SEMICONDUCTOR))),
    CAPACITOR = create("capacitor", b -> b.require(steelSheet())
            .transitionTo(TFMGItems.UNFINISHED_CAPACITOR.get())
            .addOutput(new ItemStack(TFMGItems.CAPACITOR.get(),4), 1)
            .loops(1)
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(copperSheet()))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Items.PAPER))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(copperSheet())));

    public TFMGSequencedAssemblyRecipeGen(PackOutput p_i48262_1_) {
        super(p_i48262_1_);
    }

    protected GeneratedRecipe create(String name, UnaryOperator<SequencedAssemblyRecipeBuilder> transform) {
        GeneratedRecipe generatedRecipe =
                c -> transform.apply(new SequencedAssemblyRecipeBuilder(TFMG.asResource(name)))
                        .build(c);
        all.add(generatedRecipe);
        return generatedRecipe;
    }


}
