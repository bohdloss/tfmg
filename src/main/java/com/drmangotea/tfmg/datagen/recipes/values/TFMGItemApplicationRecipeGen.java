package com.drmangotea.tfmg.datagen.recipes.values;

import com.drmangotea.tfmg.datagen.recipes.TFMGProcessingRecipeGen;
import com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.simibubi.create.AllRecipeTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

import static com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider.I.*;

public class TFMGItemApplicationRecipeGen extends TFMGProcessingRecipeGen {

    GeneratedRecipe STEEL = casing("steel", () -> Ingredient.of(steelIngot()), TFMGBlocks.STEEL_CASING::get, TFMGBlocks.HARDENED_PLANKS::get);
    GeneratedRecipe HEAVY_CASING = casing("heavy_machinery", () -> Ingredient.of(steelSheet()), TFMGBlocks.HEAVY_MACHINERY_CASING::get, TFMGBlocks.STEEL_CASING::get);
    GeneratedRecipe ALUMINUM = casing("aluminum", () -> Ingredient.of(aluminumSheet()), TFMGBlocks.ALUMINUM_CASING::get, TFMGBlocks.STEEL_CASING::get);


    GeneratedRecipe
            COATED_CIRCUIT_BOARD = create("coated_circuit_board", b -> b
            .require(TFMGItems.EMPTY_CIRCUIT_BOARD)
            .require(goldSheet())
            .output(TFMGItems.COATED_CIRCUIT_BOARD)
    );

    protected TFMGRecipeProvider.GeneratedRecipe casing(String type, Supplier<Ingredient> ingredient,
                                                        Supplier<ItemLike> output, Supplier<ItemLike> block) {
        return create(type + "_casing", b -> b.require(block.get())
                .require(ingredient.get())
                .output(output.get()));
    }

    public TFMGItemApplicationRecipeGen(PackOutput output) {
        super(output);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.ITEM_APPLICATION;
    }

}