package it.bohdloss.tfmg.datagen.recipes.values.tfmg;

import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.datagen.recipes.builder.DistillationRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static it.bohdloss.tfmg.datagen.recipes.TFMGRecipeProvider.F.*;

public class TFMGDistillationRecipeGen extends DistillationRecipeGen {
    GeneratedRecipe

            CRUDE_OIL = create(TFMG.asResource("crude_oil"), b ->b
            .require(crudeOil(),340)
            .output(heavyOil(), 120)
            .output(diesel(), 60)
            .output(kerosene(), 30)
            .output(naphtha(), 10)
            .output(gasoline(), 60)
            .output(lpg(), 60)),
            CRUDE_OIL_NO_NAPHTHA = create(TFMG.asResource("crude_oil_no_naphtha"), b ->b
                    .require(crudeOil(),330)
                    .output(heavyOil(), 120)
                    .output(diesel(), 60)
                    .output(kerosene(), 30)
                    .output(gasoline(), 60)
                    .output(lpg(), 60)),
            CRUDE_OIL_LIGHT_DISTILLATION = create(TFMG.asResource("crude_oil_light_distillation"), b ->b
                    .require(crudeOil(),200)
                    .output(heavyOil(), 150)
                    .output(diesel(), 45)
                    .output(gasoline(), 5)),


    HEAVY_OIL = create(TFMG.asResource("heavy_oil"), b ->b
            .require(heavyOil(),200)
            .output(heavyOil(), 100)
            .output(lubricationOil(), 25)
            .output(diesel(), 50)
            .output(kerosene(), 20)
            .output(naphtha(), 5)),

    HEAVY_OIL_NO_NAPHTHA = create(TFMG.asResource("heavy_oil_no_naphtha"), b ->b
            .require(heavyOil(),200)
            .output(heavyOil(), 100)
            .output(lubricationOil(), 30)
            .output(diesel(), 50)
            .output(kerosene(), 20)),

    HEAVY_OIL_LIGHT_DISTILLATION = create(TFMG.asResource("heavy_oil_light_distillation"), b ->b
            .require(heavyOil(),200)
            .output(heavyOil(), 100)
            .output(diesel(), 50)
            .output(lubricationOil(), 50));
    ;

    public TFMGDistillationRecipeGen(PackOutput generator, CompletableFuture<HolderLookup.Provider> registries) {
        super(generator, registries, TFMG.MOD_ID);
    }
}
