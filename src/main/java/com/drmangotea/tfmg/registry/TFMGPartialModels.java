package com.drmangotea.tfmg.registry;


import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.content.decoration.pipes.TFMGPipes;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.*;


public class TFMGPartialModels {

    public static final PartialModel
            AIR_INTAKE_FAN = block("air_intake/fan"),
            AIR_INTAKE_FAN_MEDIUM = block("air_intake/fan_medium"),
            AIR_INTAKE_FAN_LARGE = block("air_intake/fan_large"),
            AIR_INTAKE_FRAME = block("air_intake/frame"),
            AIR_INTAKE_FRAME_CLOSED = block("air_intake/frame_closed"),
            AIR_INTAKE_MEDIUM = block("air_intake/block_medium"),
            AIR_INTAKE_LARGE = block("air_intake/block_large"),
            COKE_OVEN_DOOR_LEFT = block("coke_oven/door_left"),
            COKE_OVEN_DOOR_RIGHT = block("coke_oven/door_right"),
            COKE_OVEN_DOOR_LEFT_BOTTOM = block("coke_oven/door_left_bottom"),
            COKE_OVEN_DOOR_RIGHT_BOTTOM = block("coke_oven/door_right_bottom"),
            COKE_OVEN_DOOR_LEFT_MIDDLE = block("coke_oven/door_left_middle"),
            COKE_OVEN_DOOR_RIGHT_MIDDLE = block("coke_oven/door_right_middle"),
            COKE_OVEN_DOOR_LEFT_TOP = block("coke_oven/door_left_top"),
            COKE_OVEN_DOOR_RIGHT_TOP = block("coke_oven/door_right_top"),
            COAL_COKE_DUST_LAYER = block("coal_coke_dust_layer"),
            POLARIZER_DIAL = block("polarizer/dial"),
            DISTILLATION_CONTROLLER_DIAL = block("steel_distillation_controller/dial"),
            PUMPJACK_HAMMER = block("pumpjack/hammer_holder"),
            PUMPJACK_FRONT_ROPE = block("pumpjack/pumpjack_front_rope"),
            PUMPJACK_CONNECTOR = block("pumpjack/pumpjack_connector"),
            PUMPJACK_CRANK_BLOCK = block("pumpjack/pumpjack_crank_block"),
            PUMPJACK_CRANK = block("pumpjack_crank/crank"),
            PUMPJACK_CONNECTORS = block("pumpjack_crank/connectors"),
            TOWER_GAUGE = block("distillation_tower/gauge"),
            SURFACE_SCANNER_DIAL = block("surface_scanner/dial"),
            SURFACE_SCANNER_FLAG = block("surface_scanner/flag"),

    INGOT_MOLD = block("casting_basin/mold_base"),
            BlOCK_MOLD = block("casting_basin/block_mold"),

    STATOR_OUTPUT = block("stator/output"),
            VOLTMETER_DIAL = block("voltmeter/dial"),

    LIGHT_BULB = block("light_bulb/light"),

    ALUMINUM_LAMP = block("aluminum_lamp/light"),
    CIRCULAR_LIGHT = block("circular_light/light"),
    MODERN_LIGHT = block("modern_light/light"),

    TRAFFIC_LIGHT = block("traffic_light/light"),

    NEON_TUBE_LIGHT = block("neon_tube/light"),

    DIESEL_ENGINE_LINKAGE = block("diesel_engine/linkage"),
            DIESEL_ENGINE_PISTON = block("diesel_engine/piston"),
            STEEL_COGHWEEL = block("steel_cogwheel_shaftless"),
            ENGINE_EXTENSION = block("regular_engine/extension"),
            ENGINE_EXTENSION_FRONT = block("regular_engine/extension_front"),
            LARGE_STEEL_COGHWEEL = block("large_steel_cogwheel_shaftless"),
            ALUMINUM_COGHWEEL = block("aluminum_cogwheel_shaftless"),
            LARGE_ALUMINUM_COGHWEEL = block("large_aluminum_cogwheel_shaftless"),
            SPOOL = block("winding_machine/spool"),
            COPPER_SPOOL = block("winding_machine/copper_spool"),
            ALUMINUM_SPOOL = block("winding_machine/aluminum_spool"),
            CONSTANTAN_SPOOL = block("winding_machine/constantan_spool"),
            CYLINDER = block("regular_engine/cylinder"),
            SMALL_CYLINDER = block("regular_engine/cylinder_small"),
            TRANSFORMER_COIL = block("transformer/coil"),
            FUSE = block("fuse_block/fuse"),
            CONNNECTING_WIRE = block("winding_machine/connecting_wire"),
            CONNNECTING_WIRE_ANIMATED = block("winding_machine/connecting_wire_animated"),
            SMALL_MIXER = block("industrial_mixer/mixer_small"),
            MIXER = block("industrial_mixer/mixer"),
            MIXER_SHAFT = block("industrial_mixer/mixer_shaft"),
            CENTRIFUGE = block("industrial_mixer/centrifuge"),
            SMALL_CENTRIFUGE = block("industrial_mixer/centrifuge_small"),
            CENTRIFUGE_MIDDLE = block("industrial_mixer/centrifuge_middle"),
            SMALL_CENTRIFUGE_MIDDLE = block("industrial_mixer/centrifuge_small_middle"),
            COPPER_ELECTRODE = block("electrode_holder/copper_electrode"),
            ZINC_ELECTRODE = block("electrode_holder/zinc_electrode"),
            GRAPHITE_ELECTRODE = block("electrode_holder/graphite_electrode"),
            GRAPHITE_ELECTRODE_SUPERHEATED = block("electrode_holder/superheated_graphite_electrode"),
            SURFACE_SCANNER_LIGHT = block("surface_scanner/light");

    //Display Segments

    public static final List<PartialModel> SEGMENTS = new ArrayList<>();
    public static final Map<TFMGPipes.PipeMaterial, PartialModel> PIPE_CASINGS = new HashMap<>();
    public static final Map<ResourceLocation, Couple<PartialModel>> FOLDING_DOORS = new HashMap<>();
    public static final Map<TFMGPipes.PipeMaterial, Map<FluidTransportBehaviour.AttachmentTypes.ComponentPartials, Map<Direction, PartialModel>>> PIPE_ATTACHMENTS = new HashMap<>();

    static {

        for (int i = 0; i < 21; i++) {

            SEGMENTS.add(block("segmented_display/segments/" + i));

        }


    }

    static {

        for (TFMGPipes.PipeMaterial material : TFMGPipes.PipeMaterial.values()) {

            Map<FluidTransportBehaviour.AttachmentTypes.ComponentPartials, Map<Direction, PartialModel>> attachments = new EnumMap<>(FluidTransportBehaviour.AttachmentTypes.ComponentPartials.class);

            for (FluidTransportBehaviour.AttachmentTypes.ComponentPartials type : FluidTransportBehaviour.AttachmentTypes.ComponentPartials.values()) {
                Map<Direction, PartialModel> map = new HashMap<>();
                for (Direction d : Iterate.directions) {
                    String asId = Lang.asId(type.name());
                    map.put(d, block(material.name + "_pipe/" + asId + "/" + Lang.asId(d.getSerializedName())));
                }
                attachments.put(type, map);
            }

            PIPE_ATTACHMENTS.put(material, attachments);

            PIPE_CASINGS.put(material, block(material.name + "_pipe/casing"));

        }


        ////////////////
        putFoldingDoor("steel_door");


    }


    private static void putFoldingDoor(String path) {
        FOLDING_DOORS.put(TFMG.asResource(path),
                Couple.create(block(path + "/fold_left"), block(path + "/fold_right")));
    }

    private static PartialModel block(String path) {
        return new PartialModel(TFMG.asResource("block/" + path));
    }

    public static void init() {
    }

}