package com.drmangotea.tfmg.ponder;


import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.ponder.scenes.MetallurgyScenes;
import com.drmangotea.tfmg.ponder.scenes.MiscTFMGScenes;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;


public class TFMGPonderIndex {
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(TFMG.MOD_ID);

    public static void register() {
        HELPER.forComponents(TFMGBlocks.REGULAR_ENGINE, TFMGBlocks.EXHAUST)
                .addStoryBoard("engines", MiscTFMGScenes::small_engines, TFMGPonderTag.TFMG_MACHINERY);


        //HELPER.forComponents(
        //        TFMGBlocks.DIESEL_ENGINE,
        //        TFMGBlocks.AIR_INTAKE,
        //        TFMGBlocks.DIESEL_ENGINE_EXPANSION
        //        )
//
        //        .addStoryBoard("diesel_engine", MiscTFMGScenes::diesel_engine, TFMGPonderTag.OIL)
        //        .addStoryBoard("diesel_engine_expansion", MiscTFMGScenes::diesel_engine_expansion, TFMGPonderTag.OIL);

        HELPER.forComponents(
                TFMGBlocks.PUMPJACK_BASE,
                TFMGBlocks.PUMPJACK_CRANK,
                TFMGBlocks.PUMPJACK_HAMMER,
                TFMGBlocks.PUMPJACK_HAMMER_CONNECTOR,
                TFMGBlocks.PUMPJACK_HAMMER_PART,
                TFMGBlocks.PUMPJACK_HAMMER_HEAD,
                TFMGBlocks.LARGE_PUMPJACK_HAMMER_CONNECTOR,
                TFMGBlocks.LARGE_PUMPJACK_HAMMER_PART,
                TFMGBlocks.LARGE_PUMPJACK_HAMMER_HEAD
        ).addStoryBoard("pumpjack", MiscTFMGScenes::pumpjack, TFMGPonderTag.TFMG_MACHINERY);


        HELPER.forComponents(TFMGBlocks.STEEL_DISTILLATION_CONTROLLER, TFMGBlocks.STEEL_DISTILLATION_OUTPUT)
                .addStoryBoard("distillation_tower", MiscTFMGScenes::distillation_tower, TFMGPonderTag.TFMG_MACHINERY);

        HELPER.forComponents(
                TFMGBlocks.STEEL_CHEMICAL_VAT,
                TFMGBlocks.INDUSTRIAL_MIXER,
                TFMGBlocks.ELECTRODE_HOLDER
                ).addStoryBoard("chemical_vat", MiscTFMGScenes::distillation_tower, TFMGPonderTag.TFMG_MACHINERY);
        /////////////////////////////////////////

        HELPER.forComponents(TFMGBlocks.BLAST_FURNACE_OUTPUT,
                        TFMGBlocks.FIREPROOF_BRICKS,
                        TFMGBlocks.FIREPROOF_BRICK_REINFORCEMENT,
                        TFMGBlocks.BLAST_FURNACE_HATCH,
                        TFMGBlocks.BLAST_FURNACE_REINFORCEMENT,
                        TFMGBlocks.RUSTED_BLAST_FURNACE_REINFORCEMENT
                )
                .addStoryBoard("blast_furnace", MetallurgyScenes::blast_furnace, TFMGPonderTag.METALLURGY);


        HELPER.forComponents(TFMGBlocks.COKE_OVEN)

                .addStoryBoard("coke_oven", MetallurgyScenes::coke_oven, TFMGPonderTag.METALLURGY);


    }

    public static void registerTags() {
        PonderRegistry.TAGS.forTag(TFMGPonderTag.METALLURGY)
                .add(TFMGBlocks.COKE_OVEN)
                .add(TFMGBlocks.CASTING_BASIN)
                .add(TFMGBlocks.FIREPROOF_BRICKS)
                .add(TFMGBlocks.FIREPROOF_BRICK_REINFORCEMENT)
                .add(TFMGBlocks.RUSTED_BLAST_FURNACE_REINFORCEMENT)
                .add(TFMGBlocks.BLAST_FURNACE_HATCH)
                .add(TFMGBlocks.BLAST_FURNACE_REINFORCEMENT)
                .add(TFMGBlocks.FIREPROOF_BRICK_REINFORCEMENT)
                .add(TFMGBlocks.BLAST_FURNACE_OUTPUT);

        PonderRegistry.TAGS.forTag(TFMGPonderTag.TFMG_MACHINERY)
                .add(TFMGBlocks.STATOR)
                .add(TFMGBlocks.ROTOR)
                .add(TFMGBlocks.CABLE_CONNECTOR)
                .add(TFMGBlocks.ACCUMULATOR)
                .add(TFMGBlocks.REGULAR_ENGINE)
                .add(TFMGBlocks.STEEL_DISTILLATION_OUTPUT)
                .add(TFMGBlocks.STEEL_DISTILLATION_CONTROLLER)
                .add(TFMGBlocks.PUMPJACK_BASE)
                .add(TFMGBlocks.PUMPJACK_HAMMER)
                .add(TFMGBlocks.PUMPJACK_CRANK);
    }

}
