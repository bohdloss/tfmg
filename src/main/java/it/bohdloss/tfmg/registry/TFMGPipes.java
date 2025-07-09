package it.bohdloss.tfmg.registry;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.fluids.pipes.SmartFluidPipeGenerator;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import it.bohdloss.tfmg.base.PipeSet;
import it.bohdloss.tfmg.config.TFMGStress;
import it.bohdloss.tfmg.content.decoration.pipes.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

import java.util.HashMap;
import java.util.Map;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static it.bohdloss.tfmg.TFMG.REGISTRATE;

public class TFMGPipes {

    public static final Map<PipeMaterial, PipeSet> TFMG_PIPES = new HashMap<>();

    static {
        REGISTRATE.setCreativeTab(TFMGCreativeTabs.TFMG_DECORATION);
    }

    static {


        for (PipeMaterial pipeType : PipeMaterial.values()) {

            BlockEntry<TFMGPipeBlock> pipe =
                    REGISTRATE.block(pipeType.name + "_pipe", p -> new TFMGPipeBlock(p, pipeType))
                            .initialProperties(SharedProperties::copperMetal)
                            .transform(pickaxeOnly())
                            .blockstate(BlockStateGen.pipe())
                            .onRegister(CreateRegistrate.blockModel(()->
                                    switch (pipeType){
                                        case BRASS -> TFMGPipeAttachmentModel::withAOBrass;
                                        case STEEL -> TFMGPipeAttachmentModel::withAOSteel;
                                        case ALUMINUM -> TFMGPipeAttachmentModel::withAOAluminum;
                                        case CAST_IRON -> TFMGPipeAttachmentModel::withAOCastIron;
                                        case PLASTIC -> TFMGPipeAttachmentModel::withAOPlastic;
                                    }))
                            .item()
                            .transform(customItemModel())
                            .register();

            BlockEntry<TFMGEncasedPipeBlock> copper_encased_pipe =
                    REGISTRATE.block("copper_encased_" + pipeType.name + "_pipe", p -> new TFMGEncasedPipeBlock(p, AllBlocks.COPPER_CASING::get, pipeType))
                            .initialProperties(SharedProperties::copperMetal)
                            .properties(p -> p.noOcclusion().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY))
                            .transform(axeOrPickaxe())
                            .blockstate(BlockStateGen.encasedPipe())
                            .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(AllSpriteShifts.COPPER_CASING)))
                            .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, AllSpriteShifts.COPPER_CASING,
                                    (s, f) -> !s.getValue(TFMGEncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(f)))))
                            .onRegister(CreateRegistrate.blockModel(()->
                                    switch (pipeType){
                                        case BRASS -> TFMGPipeAttachmentModel::withAOBrass;
                                        case STEEL -> TFMGPipeAttachmentModel::withAOSteel;
                                        case ALUMINUM -> TFMGPipeAttachmentModel::withAOAluminum;
                                        case CAST_IRON -> TFMGPipeAttachmentModel::withAOCastIron;
                                        case PLASTIC -> TFMGPipeAttachmentModel::withAOPlastic;
                                    }))

                            .loot((p, b) -> p.dropOther(b, pipe.get()))
                            .transform(EncasingRegistry.addVariantTo(pipe))
                            .register();

            BlockEntry<TFMGGlassPipeBlock> glass_pipe =
                    REGISTRATE.block("glass_" + pipeType.name + "_pipe", p -> new TFMGGlassPipeBlock(p, pipeType))
                            .initialProperties(SharedProperties::copperMetal)
                            .addLayer(() -> RenderType::cutoutMipped)
                            .transform(pickaxeOnly())
                            .blockstate((c, p) -> {
                                p.getVariantBuilder(c.getEntry())
                                        .forAllStatesExcept(state -> {
                                            Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
                                            return ConfiguredModel.builder()
                                                    .modelFile(p.models()
                                                            .getExistingFile(p.modLoc("block/" + pipeType.name + "_pipe/window")))
                                                    .uvLock(false)
                                                    .rotationX(axis == Direction.Axis.Y ? 0 : 90)
                                                    .rotationY(axis == Direction.Axis.X ? 90 : 0)
                                                    .build();
                                        }, BlockStateProperties.WATERLOGGED);
                            })
                            .onRegister(CreateRegistrate.blockModel(()->
                                    switch (pipeType){
                                        case BRASS -> TFMGPipeAttachmentModel::withAOBrass;
                                        case STEEL -> TFMGPipeAttachmentModel::withAOSteel;
                                        case ALUMINUM -> TFMGPipeAttachmentModel::withAOAluminum;
                                        case CAST_IRON -> TFMGPipeAttachmentModel::withAOCastIron;
                                        case PLASTIC -> TFMGPipeAttachmentModel::withAOPlastic;
                                    }))

                            .loot((p, b) -> p.dropOther(b, pipe.get()))
                            .register();

            BlockEntry<TFMGPumpBlock> fluid_pump =
                    REGISTRATE.block(pipeType.name + "_mechanical_pump", TFMGPumpBlock::new)
                            .initialProperties(SharedProperties::copperMetal)
                            .transform(pickaxeOnly())
                            .blockstate(BlockStateGen.directionalBlockProviderIgnoresWaterlogged(true))
                            .onRegister(CreateRegistrate.blockModel(()->
                                    switch (pipeType){
                                        case BRASS -> TFMGPipeAttachmentModel::withAOBrass;
                                        case STEEL -> TFMGPipeAttachmentModel::withAOSteel;
                                        case ALUMINUM -> TFMGPipeAttachmentModel::withAOAluminum;
                                        case CAST_IRON -> TFMGPipeAttachmentModel::withAOCastIron;
                                        case PLASTIC -> TFMGPipeAttachmentModel::withAOPlastic;
                                    }))

                            .transform(TFMGStress.setImpact(4.0))
                            .item()
                            .transform(customItemModel())
                            .register();

            BlockEntry<TFMGSmartFluidPipeBlock> smart_pipe =
                    REGISTRATE.block(pipeType.name + "_smart_fluid_pipe", TFMGSmartFluidPipeBlock::new)
                            .initialProperties(SharedProperties::copperMetal)
                            .transform(pickaxeOnly())
                            .blockstate(new SmartFluidPipeGenerator()::generate)
                            .onRegister(CreateRegistrate.blockModel(()->
                                    switch (pipeType){
                                        case BRASS -> TFMGPipeAttachmentModel::withAOBrass;
                                        case STEEL -> TFMGPipeAttachmentModel::withAOSteel;
                                        case ALUMINUM -> TFMGPipeAttachmentModel::withAOAluminum;
                                        case CAST_IRON -> TFMGPipeAttachmentModel::withAOCastIron;
                                        case PLASTIC -> TFMGPipeAttachmentModel::withAOPlastic;
                                    }))

                            .item()
                            .transform(customItemModel())
                            .register();

            BlockEntry<TFMGFluidValveBlock> fluid_valve =
                    REGISTRATE.block(pipeType.name + "_fluid_valve", TFMGFluidValveBlock::new)
                            .initialProperties(SharedProperties::copperMetal)
                            .transform(pickaxeOnly())
                            .blockstate((c, p) -> BlockStateGen.directionalAxisBlock(c, p,
                                    (state, vertical) -> AssetLookup.partialBaseModel(c, p, vertical ? "vertical" : "horizontal",
                                            state.getValue(FluidValveBlock.ENABLED) ? "open" : "closed")))
                            .item()
                            .transform(customItemModel())
                            .register();

            TFMG_PIPES.put(pipeType, new PipeSet(pipe, copper_encased_pipe, glass_pipe, fluid_pump, smart_pipe, fluid_valve));
        }


    }



    public static void init() {}

    public enum PipeMaterial {
        BRASS("brass"),
        STEEL("steel"),
        ALUMINUM("aluminum"),
        CAST_IRON("cast_iron"),
        PLASTIC("plastic");

        public final String name;

        PipeMaterial(String name) {
            this.name = name;
        }
    }
}
