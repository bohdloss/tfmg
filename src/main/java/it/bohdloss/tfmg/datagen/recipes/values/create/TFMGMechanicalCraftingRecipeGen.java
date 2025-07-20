package it.bohdloss.tfmg.datagen.recipes.values.create;

import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeGen;
import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.registry.TFMGItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

import static it.bohdloss.tfmg.datagen.recipes.TFMGRecipeProvider.I.*;

public class TFMGMechanicalCraftingRecipeGen extends MechanicalCraftingRecipeGen {
    GeneratedRecipe

            // TODO
//            ENGINE_CONTROLLER = create(TFMGBlocks.ENGINE_CONTROLLER::get)
//            .recipe(b -> b
//                    .key('R',  rubber())
//                    .key('S', shaft())
//                    .key('V', TFMGBlocks.VOLTMETER)
//                    .key('W', copperWire())
//                    .key('C', heavyMachineryCasing())
//                    .key('Z', circuitBoard())
//                    .key('M', steelMechanism())
//                    .patternLine("RRR")
//                    .patternLine("VSV")
//                    .patternLine("WCW")
//                    .patternLine("ZMZ")
//                    .disallowMirrored()),
//
//    ROTOR = create(TFMGBlocks.ROTOR::get)
//            .recipe(b -> b
//                    .key('C', TFMGItems.ELECTROMAGNETIC_COIL)
//                    .key('A', aluminumIngot())
//                    .key('S', steelBlock())
//                    .patternLine(" CCC ")
//                    .patternLine("CAAAC")
//                    .patternLine("CASAC")
//                    .patternLine("CAAAC")
//                    .patternLine(" CCC ")
//                    .disallowMirrored()),
//
//    STATOR = create(TFMGBlocks.STATOR::get).returns(3)
//            .recipe(b -> b
//                    .key('C', TFMGItems.ELECTROMAGNETIC_COIL)
//                    .key('A', aluminumSheet())
//                    .key('W', copperWire())
//                    .key('M', magnet())
//                    .key('S', steelBlock())
//                    .patternLine("CM  ")
//                    .patternLine("ACM ")
//                    .patternLine("ASCM")
//                    .patternLine("WAAC")
//                    .disallowMirrored()),
//
//    SIMPLE_LARGE_ENGINE = create(TFMGBlocks.SIMPLE_LARGE_ENGINE::get)
//            .recipe(b -> b
//                    .key('C', castIronIngot())
//                    .key('O', steelSheet())
//                    .key('M', precisionMechanism())
//                    .patternLine("CCC")
//                    .patternLine("OCO")
//                    .patternLine("OMO")
//                    .patternLine("OCO")
//                    .disallowMirrored()),
//
//    QUAD_POTATO_CANNON = create(TFMGItems.QUAD_POTATO_CANNON::get)
//            .recipe(b -> b
//                    .key('O', steelIngot())
//                    .key('C', castIronIngot())
//                    .key('P', steelPipe())
//                    .key('M', steelMechanism())
//                    .patternLine("PMPC")
//                    .patternLine("PMPC")
//                    .patternLine(" O  ")
//                    .disallowMirrored()),
//
//    FLAMETHROWER = create(TFMGItems.FLAMETHROWER::get)
//            .recipe(b -> b
//                    .key('O', steelIngot())
//                    .key('C', circuitBoard())
//                    .key('T', steelTank())
//                    .key('P', steelPipe())
//                    .key('S', TFMGItems.SPARK_PLUG)
//                    .key('M', steelMechanism())
//                    .key('B', TFMGBlocks.ALUMINUM_BARS)
//                    .key('W', copperWire())
//                    .patternLine("BWC ")
//                    .patternLine("PPTM")
//                    .patternLine("S O ")
//                    .disallowMirrored()),
//
//    ADVANCED_POTATO_CANNON = create(TFMGItems.ADVANCED_POTATO_CANNON::get)
//            .recipe(b -> b
//                    .key('O', rebar())
//                    .key('C', circuitBoard())
//                    .key('T', steelTank())
//                    .key('P', steelPipe())
//                    .key('M', steelMechanism())
//                    .patternLine("PPPT")
//                    .patternLine(" MCO")
//                    .disallowMirrored()),
//
//    LARGE_ENGINE = create(TFMGBlocks.LARGE_ENGINE::get)
//            .recipe((b) -> b
//                    .key('A', aluminumSheet())
//                    .key('B', aluminumIngot())
//                    .key('H', I.heavyPlate())
//                    .key('S', I.steelMechanism())
//                    .key('C', I.heavyMachineryCasing())
//                    .key('O', steelIngot())
//                    .key('T', TFMGBlocks.STEEL_FLUID_TANK.get())
//                    .patternLine(" O ")
//                    .patternLine(" B ")
//                    .patternLine("AOA")
//                    .patternLine("SCS")
//                    .patternLine("STS")
//                    .patternLine("HHH")),

    SPARK_PLUG = create(TFMGItems.SPARK_PLUG::get)
            .recipe(b -> b
                    .key('F', Items.FLINT)
                    .key('A', aluminumIngot())
                    .patternLine("F")
                    .patternLine("A")
                    .disallowMirrored());

    public TFMGMechanicalCraftingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, TFMG.MOD_ID);
    }
}
