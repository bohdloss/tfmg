package it.bohdloss.tfmg.datagen.recipes.values.create;

import com.simibubi.create.api.data.recipe.CrushingRecipeGen;
import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.datagen.recipes.TFMGRecipeProvider;
import it.bohdloss.tfmg.registry.TFMGBlocks;
import it.bohdloss.tfmg.registry.TFMGItems;
import it.bohdloss.tfmg.registry.TFMGPaletteStoneTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

import static it.bohdloss.tfmg.datagen.recipes.TFMGRecipeProvider.I.*;

public class TFMGCrushingRecipeGen extends CrushingRecipeGen {
    GeneratedRecipe
            COPPER_SULFATE = create(TFMGRecipeProvider.I::copperSulfate, b -> b
            .output(boneMeal(), 4)
            .output(.25f, boneMeal(), 3)
            .output(.5f, cyanDye(), 1)
            .output(.75f, blueDye(), 1)
    ),
            LIGNITE = create(TFMGBlocks.LIGNITE::get, b -> b
                    .output(.75f,coal(), 1)
                    .output(.2f,coal(), 1)
            ),
            BAUXITE = create(TFMGPaletteStoneTypes.BAUXITE.getBaseBlock()::get, b -> b
                    .output(.75f, TFMGItems.BAUXITE_POWDER, 2)
                    .output(.2f,TFMGItems.BAUXITE_POWDER, 1)
            ),
            LIMESAND = create(TFMGRecipeProvider.I::limestone, b -> b
                    .output(limesand(), 1)
            ),
            SLAG = create(TFMGBlocks.SLAG_BLOCK::get, b -> b
                    .output(slag(), 2)
                    .output(.3f,slag())
            ),
            COAL_COKE = create(TFMGRecipeProvider.I::coalCoke, b -> b
                    .output(coalCokeDust(), 1)
            ),
            SALTPETER = create(TFMGRecipeProvider.I::dirt, b -> b
                    .output(.05f, nitrateDust(), 1)
            ),
            GALENA = create(TFMGPaletteStoneTypes.GALENA.getBaseBlock()::get, b -> b
                    .output(.4f, crushedRawLead(), 1)
                    .output(.1f, TFMGItems.LEAD_NUGGET, 2)
            ),
            SULFUR = create(() -> TFMGBlocks.SULFUR, b -> b
                    .output(.2f, sulfurDust(), 1)
                    .output(.1f, sulfurDust(), 1)
            ),
            LITHIUM_ORE = create(() -> TFMGBlocks.LITHIUM_ORE, b -> b
                    .output(TFMGItems.CRUSHED_LITHIUM, 1)
                    .output(.25f, TFMGItems.CRUSHED_LITHIUM, 1)
                    .output(.75f, TFMGRecipeProvider.I.experienceNugget(), 1)
                    .output(.12f, Items.COBBLESTONE, 1)
            ),
            DEEPSLATE_LITHIUM_ORE = create(() -> TFMGBlocks.DEEPSLATE_LITHIUM_ORE, b -> b
                    .output(TFMGItems.CRUSHED_LITHIUM, 2)
                    .output(.25f, TFMGItems.CRUSHED_LITHIUM, 1)
                    .output(.75f, TFMGRecipeProvider.I.experienceNugget(), 1)
                    .output(.12f, Items.COBBLED_DEEPSLATE, 1)
            ),
            RAW_LITHIUM = create(() -> TFMGItems.RAW_LITHIUM, b -> b
                    .output(TFMGItems.CRUSHED_LITHIUM, 1)
                    .output(.75f, TFMGRecipeProvider.I.experienceNugget(), 1)
            ),
            RAW_LITHIUM_BLOCK = create(() -> TFMGBlocks.RAW_LITHIUM_BLOCK, b -> b
                    .output(TFMGItems.CRUSHED_LITHIUM, 9)
                    .output(.75f, TFMGRecipeProvider.I.experienceNugget(), 9)
            )


                    ;

    public TFMGCrushingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, TFMG.MOD_ID);
    }
}
