package com.drmangotea.tfmg.datagen.recipes.values.create;

import com.drmangotea.tfmg.datagen.recipes.TFMGProcessingRecipeGen;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.drmangotea.tfmg.registry.TFMGPaletteStoneTypes;
import com.simibubi.create.AllRecipeTypes;
import net.minecraft.data.PackOutput;

import static com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider.I.*;

public class TFMGCrushingRecipeGen extends TFMGProcessingRecipeGen {

    GeneratedRecipe
            COPPER_SULFATE = create(I::copperSulfate, b -> b
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
            LIMESAND = create(I::limestone, b -> b
                    .output(limesand(), 1)
            ),
            SLAG = create(TFMGBlocks.SLAG_BLOCK::get, b -> b
                    .output(slag(), 2)
                    .output(.3f,slag())
            ),
            COAL_COKE = create(I::coalCoke, b -> b
                    .output(coalCokeDust(), 1)
            ),
            SALTPETER = create(I::dirt, b -> b
                    .output(.05f, nitrateDust(), 1)
            ),
            GALENA = create(TFMGPaletteStoneTypes.GALENA.getBaseBlock()::get, b -> b
                    .output(.4f, crushedRawLead(), 1)
                    .output(.1f, TFMGItems.LEAD_NUGGET, 2)
            ),
            SULFUR = create(() -> TFMGBlocks.SULFUR, b -> b
                    .output(.2f, sulfurDust(), 1)
                    .output(.1f, sulfurDust(), 1)
            );

    public TFMGCrushingRecipeGen(PackOutput output) {
        super(output);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.CRUSHING;
    }

}
