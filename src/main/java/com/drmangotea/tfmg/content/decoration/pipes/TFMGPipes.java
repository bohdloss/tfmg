package com.drmangotea.tfmg.content.decoration.pipes;

import com.drmangotea.tfmg.config.TFMGStress;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.fluids.PipeAttachmentModel;
import com.simibubi.create.content.fluids.pipes.SmartFluidPipeGenerator;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.model.generators.ConfiguredModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.drmangotea.tfmg.TFMG.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

@SuppressWarnings("removal")
public class TFMGPipes {

    public static final Map<PipeMaterial, List<BlockEntry<? extends Block>>> TFMG_PIPES = new HashMap<>();

    static {


        for (PipeMaterial pipeType : PipeMaterial.values()) {

            List<BlockEntry<? extends Block>> pipes = new ArrayList<>();

            BlockEntry<TFMGPipeBlock> pipe =
                    REGISTRATE.block(pipeType.name + "_pipe", p -> new TFMGPipeBlock(p, pipeType))
                            .initialProperties(SharedProperties::copperMetal)
                            .transform(pickaxeOnly())
                            .blockstate(BlockStateGen.pipe())
                            .onRegister(CreateRegistrate.blockModel(() -> t -> new TFMGPipeAttachmentModel(t, pipeType)))
                            .item()
                            .transform(customItemModel())
                            .register();

            pipes.add(pipe);

            BlockEntry<TFMGEncasedPipeBlock> copper_encased_pipe =
                    REGISTRATE.block("copper_encased_" + pipeType.name + "_pipe", p -> new TFMGEncasedPipeBlock(p, AllBlocks.COPPER_CASING::get, pipeType))
                            .initialProperties(SharedProperties::copperMetal)
                            .properties(p -> p.noOcclusion().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY))
                            .transform(axeOrPickaxe())
                            .blockstate(BlockStateGen.encasedPipe())
                            .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(AllSpriteShifts.COPPER_CASING)))
                            .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, AllSpriteShifts.COPPER_CASING,
                                    (s, f) -> !s.getValue(TFMGEncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(f)))))
                            .onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::withoutAO))
                            .loot((p, b) -> p.dropOther(b, pipe.get()))
                            .transform(EncasingRegistry.addVariantTo(pipe))
                            .register();

            pipes.add(copper_encased_pipe);
           //if(true)
           //    break;
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
                            .onRegister(CreateRegistrate.blockModel(() -> t -> new TFMGPipeAttachmentModel(t, pipeType)))
                            .loot((p, b) -> p.dropOther(b, pipe.get()))
                            .register();

            pipes.add(glass_pipe);

            BlockEntry<TFMGPumpBlock> fluid_pump =
                    REGISTRATE.block(pipeType.name + "_mechanical_pump", TFMGPumpBlock::new)
                            .initialProperties(SharedProperties::copperMetal)
                            .transform(pickaxeOnly())
                            .blockstate(BlockStateGen.directionalBlockProviderIgnoresWaterlogged(true))
                            .onRegister(CreateRegistrate.blockModel(() -> t -> new TFMGPipeAttachmentModel(t, pipeType)))
                            .transform(TFMGStress.setImpact(4.0))
                            .item()
                            .transform(customItemModel())
                            .register();

            pipes.add(fluid_pump);

            BlockEntry<TFMGSmartFluidPipeBlock> smart_pipe =
                    REGISTRATE.block(pipeType.name + "_smart_fluid_pipe", TFMGSmartFluidPipeBlock::new)
                            .initialProperties(SharedProperties::copperMetal)
                            .transform(pickaxeOnly())
                            .blockstate(new SmartFluidPipeGenerator()::generate)
                            .onRegister(CreateRegistrate.blockModel(() -> t -> new TFMGPipeAttachmentModel(t, pipeType)))
                            .item()
                            .transform(customItemModel())
                            .register();

            pipes.add(smart_pipe);

            BlockEntry<TFMGFluidValveBlock> fluid_valve =
                    REGISTRATE.block(pipeType.name + "_fluid_valve", TFMGFluidValveBlock::new)
                            .initialProperties(SharedProperties::copperMetal)
                            .transform(pickaxeOnly())
                            .blockstate((c, p) -> BlockStateGen.directionalAxisBlock(c, p,
                                    (state, vertical) -> AssetLookup.partialBaseModel(c, p, vertical ? "vertical" : "horizontal",
                                            state.getValue(FluidValveBlock.ENABLED) ? "open" : "closed")))
                            .onRegister(CreateRegistrate.blockModel(() -> t -> new TFMGPipeAttachmentModel(t, pipeType)))
                            .item()
                            .transform(customItemModel())
                            .register();

            pipes.add(fluid_valve);

            TFMG_PIPES.put(pipeType, pipes);
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
