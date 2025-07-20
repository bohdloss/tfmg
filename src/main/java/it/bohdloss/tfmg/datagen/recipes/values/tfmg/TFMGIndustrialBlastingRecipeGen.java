package it.bohdloss.tfmg.datagen.recipes.values.tfmg;

import com.simibubi.create.AllItems;
import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.datagen.recipes.builder.IndustrialBlastingRecipeGen;
import it.bohdloss.tfmg.registry.TFMGFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

import static it.bohdloss.tfmg.datagen.recipes.TFMGRecipeProvider.I.ironDust;

public class TFMGIndustrialBlastingRecipeGen extends IndustrialBlastingRecipeGen {

    GeneratedRecipe
            SILICON = create("silicon", b -> b
                    .require(Items.QUARTZ)
                    .output(TFMGFluids.LIQUID_SILICON.get(), 40)
                    .duration(5)
                    .hotAirUsage(0)
                    .needsFlux(false)
            ),
            STEEL = create("steel", b -> b
                    .require(AllItems.CRUSHED_IRON)
                    .output(TFMGFluids.MOLTEN_STEEL.get(), 144)
                    .output(TFMGFluids.MOLTEN_SLAG.get(), 144)
                    .output(TFMGFluids.FURNACE_GAS.get(), 200)
                    .duration(20)
                    .hotAirUsage(20)
                    .needsFlux(true)
            ),
            STEEL_DOUBLE = create("steel_from_raw_iron", b -> b
                    .require(Items.RAW_IRON)
                    .output(TFMGFluids.MOLTEN_STEEL.get(), 288)
                    .output(TFMGFluids.MOLTEN_SLAG.get(), 288)
                    .output(TFMGFluids.FURNACE_GAS.get(), 200)
                    .duration(40)
                    .hotAirUsage(30)
                    .needsFlux(true)
            ),
            STEEL_DUST = create("steel_from_dust", b -> b
                    .require(ironDust())
                    .output(TFMGFluids.MOLTEN_STEEL.get(), 144)
                    .output(TFMGFluids.MOLTEN_SLAG.get(), 144)
                    .output(TFMGFluids.FURNACE_GAS.get(), 20)
                    .duration(20)
                    .hotAirUsage(20)
                    .needsFlux(true)
            );

    public TFMGIndustrialBlastingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, TFMG.MOD_ID);
    }
}
