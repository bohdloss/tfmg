package it.bohdloss.tfmg.datagen.recipes.values.tfmg;

import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.datagen.recipes.builder.WindingRecipeGen;
import it.bohdloss.tfmg.registry.TFMGItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static it.bohdloss.tfmg.datagen.recipes.TFMGRecipeProvider.I.coil100Turns;

public class TFMGWindingRecipeGen extends WindingRecipeGen {
    GeneratedRecipe

            ELECTROMAGNETIC_COIL = create("electromagnetic_coil", b ->b
            .require(TFMGItems.UNFINISHED_ELECTROMAGNETIC_COIL)
            .require(TFMGItems.COPPER_SPOOL)
            .output(coil100Turns())
            .duration(100));
//            RESISTOR = create("resistor", b ->b TODO
//                    .require(TFMGItems.UNFINISHED_RESISTOR)
//                    .require(TFMGItems.CONSTANTAN_SPOOL)
//                    .output(resistor10Ohms())
//                    .duration(50))
//                    ;

    public TFMGWindingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, TFMG.MOD_ID);
    }
}
