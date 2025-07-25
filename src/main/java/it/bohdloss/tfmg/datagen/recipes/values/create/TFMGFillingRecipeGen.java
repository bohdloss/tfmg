package it.bohdloss.tfmg.datagen.recipes.values.create;

import com.simibubi.create.api.data.recipe.FillingRecipeGen;
import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.registry.TFMGBlocks;
import it.bohdloss.tfmg.registry.TFMGFluids;
import it.bohdloss.tfmg.registry.TFMGItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

import static it.bohdloss.tfmg.datagen.recipes.TFMGRecipeProvider.F.*;

public class TFMGFillingRecipeGen extends FillingRecipeGen {
    GeneratedRecipe

            HARDENED_PLANKS = create("hardened_planks", b -> b
            .require(ItemTags.PLANKS)
            .require(TFMGFluids.CREOSOTE.getSource(), 250)
            .output(TFMGBlocks.HARDENED_PLANKS)),

    NAPALM_POTATO = create("napalm_potato", b -> b
            .require(Items.POTATO)
            .require(TFMGFluids.NAPALM.getSource(), 500)
            .output(TFMGItems.NAPALM_POTATO)),


    //GAS TANKS

    LPG_TANK = create("lpg_tank", b -> b
            .require(Items.BUCKET)
            .require(lpg(), 1000)
            .output(lpgTank())
    ),
    //BUTANE_TANK = create("butane_tank", b -> b
    //        .require(butane(), 1000)
    //        .output(butaneTank())
    //),
    //PROPANE_TANK = create("propane_tank", b -> b
    //        .require(propane(), 1000)
    //        .output(propaneTank())
    //),
    HYDROGEN_TANK = create("hydrogen_tank", b -> b
            .require(Items.BUCKET)
            .require(hydrogen(), 1000)
            .output(hydrogenTank())
    ),
            FURNACE_GAS_TANK = create("furnace_gas_tank", b -> b
                    .require(Items.BUCKET)
                    .require(blastFurnaceGas(), 1000)
                    .output(furnaceGasTank())
            ),
            ETHYLENE_TANK = create("ethylene_tank", b -> b
                    .require(Items.BUCKET)
                    .require(ethylene(), 1000)
                    .output(ethyleneTank())
            ),
            PROPYLENE_TANK = create("propylene_tank", b -> b
                    .require(Items.BUCKET)
                    .require(propylene(), 1000)
                    .output(propyleneTank())
            ),
            NEON_TANK = create("neon_tank", b -> b
                    .require(Items.BUCKET)
                    .require(neon(), 1000)
                    .output(neonTank())
            ),
            CARBON_DIOXIDE_TANK = create("carbon_dioxide_tank", b -> b
                    .require(Items.BUCKET)
                    .require(carbonDioxide(), 1000)
                    .output(carbonDioxideTank())
            ),
            AIR_TANK = create("air_tank", b -> b
                    .require(Items.BUCKET)
                    .require(air(), 1000)
                    .output(airTank())
            ),
            HOT_AIR_TANK = create("hot_air_tank", b -> b
                    .require(Items.BUCKET)
                    .require(hotAir(), 1000)
                    .output(hotAirTank())
            );

    public TFMGFillingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, TFMG.MOD_ID);
    }
}
