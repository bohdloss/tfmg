package com.drmangotea.tfmg.registry;

import com.drmangotea.tfmg.base.HalfShaftRenderer;
import com.drmangotea.tfmg.content.decoration.cogs.*;
import com.drmangotea.tfmg.content.decoration.doors.TFMGSlidingDoorBlockEntity;
import com.drmangotea.tfmg.content.decoration.doors.TFMGSlidingDoorRenderer;
import com.drmangotea.tfmg.content.decoration.flywheels.TFMGFlywheelBlockEntity;
import com.drmangotea.tfmg.content.decoration.flywheels.TFMGFlywheelInstance;
import com.drmangotea.tfmg.content.decoration.flywheels.TFMGFlywheelRenderer;
import com.drmangotea.tfmg.content.decoration.pipes.TFMGPipeBlockEntity;
import com.drmangotea.tfmg.content.decoration.pipes.TFMGPipes;
import com.drmangotea.tfmg.content.decoration.tanks.TFMGFluidTankRenderer;
import com.drmangotea.tfmg.content.decoration.tanks.steel.SteelFluidTankRenderer;
import com.drmangotea.tfmg.content.decoration.tanks.steel.SteelTankBlockEntity;
import com.drmangotea.tfmg.content.electricity.base.ElectricBlockEntity;
import com.drmangotea.tfmg.content.electricity.connection.cable_hub.CableHubBlockEntity;
import com.drmangotea.tfmg.content.electricity.connection.cables.CableConnectorBlockEntity;
import com.drmangotea.tfmg.content.electricity.connection.cables.CableConnectorRenderer;
import com.drmangotea.tfmg.content.electricity.connection.copycat_cable.CopycatCableBlockEntity;
import com.drmangotea.tfmg.content.electricity.connection.diagonal.DiagonalCableBlockEntity;
import com.drmangotea.tfmg.content.electricity.connection.tube.CableTubeBlockEntity;
import com.drmangotea.tfmg.content.electricity.debug.DebugGeneratorBlockEntity;
import com.drmangotea.tfmg.content.electricity.generators.GeneratorBlockEntity;
import com.drmangotea.tfmg.content.electricity.generators.creative_generator.CreativeGeneratorBlockEntity;
import com.drmangotea.tfmg.content.electricity.generators.large_generator.*;
import com.drmangotea.tfmg.content.electricity.lights.*;
import com.drmangotea.tfmg.content.electricity.lights.neon_tube.NeonTubeBlockEntity;
import com.drmangotea.tfmg.content.electricity.lights.neon_tube.NeonTubeRenderer;
import com.drmangotea.tfmg.content.electricity.lights.variants.AluminumLampRenderer;
import com.drmangotea.tfmg.content.electricity.lights.variants.CircularLightRenderer;
import com.drmangotea.tfmg.content.electricity.lights.variants.ModernLightRenderer;
import com.drmangotea.tfmg.content.electricity.measurement.AmmeterBlockEntity;
import com.drmangotea.tfmg.content.electricity.measurement.VoltMeterBlockEntity;
import com.drmangotea.tfmg.content.electricity.measurement.VoltMeterRenderer;
import com.drmangotea.tfmg.content.electricity.storage.AccumulatorBlockEntity;
import com.drmangotea.tfmg.content.electricity.utilities.converter.ConverterBlockEntity;
import com.drmangotea.tfmg.content.electricity.utilities.diode.ElectricDiodeBlockEntity;
import com.drmangotea.tfmg.content.electricity.utilities.electric_motor.ElectricMotorBlockEntity;
import com.drmangotea.tfmg.content.electricity.utilities.electric_pump.ElectricPumpBlockEntity;
import com.drmangotea.tfmg.content.electricity.utilities.electric_switch.ElectricSwitchBlockEntity;
import com.drmangotea.tfmg.content.electricity.utilities.fuse_block.FuseBlockEntity;
import com.drmangotea.tfmg.content.electricity.utilities.fuse_block.FuseBlockRenderer;
import com.drmangotea.tfmg.content.electricity.utilities.polarizer.PolarizerBlockEntity;
import com.drmangotea.tfmg.content.electricity.utilities.polarizer.PolarizerRenderer;
import com.drmangotea.tfmg.content.electricity.utilities.potentiometer.PotentiometerBlockEntity;
import com.drmangotea.tfmg.content.electricity.utilities.resistor.ResistorBlockEntity;
import com.drmangotea.tfmg.content.electricity.utilities.segmented_display.SegmentedDisplayBlockEntity;
import com.drmangotea.tfmg.content.electricity.utilities.segmented_display.SegmentedDisplayRenderer;
import com.drmangotea.tfmg.content.electricity.utilities.segmented_display.SegmentedDisplaySource;
import com.drmangotea.tfmg.content.electricity.utilities.segmented_display.SegmentedDisplayTarget;
import com.drmangotea.tfmg.content.electricity.utilities.traffic_light.TrafficLightBlockEntity;
import com.drmangotea.tfmg.content.electricity.utilities.traffic_light.TrafficLightRenderer;
import com.drmangotea.tfmg.content.electricity.utilities.transformer.TransformerBlockEntity;
import com.drmangotea.tfmg.content.electricity.utilities.transformer.TransformerRenderer;
import com.drmangotea.tfmg.content.electricity.utilities.voltage_observer.VoltageObserverBlockEntity;
import com.drmangotea.tfmg.content.engines.base.EngineInstance;
import com.drmangotea.tfmg.content.engines.engine_controller.EngineControllerBlock;
import com.drmangotea.tfmg.content.engines.engine_controller.EngineControllerBlockEntity;
import com.drmangotea.tfmg.content.engines.engine_gearbox.EngineGearboxBlockEntity;
import com.drmangotea.tfmg.content.engines.engine_gearbox.EngineGearboxInstance;
import com.drmangotea.tfmg.content.engines.engine_gearbox.EngineGearboxRenderer;
import com.drmangotea.tfmg.content.engines.regular_engine.RegularEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.regular_engine.RegularEngineRenderer;
import com.drmangotea.tfmg.content.machinery.metallurgy.blast_furnace.BlastFurnaceHatchBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.blast_furnace.BlastFurnaceOutputBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.blast_furnace.BlastFurnaceRenderer;
import com.drmangotea.tfmg.content.machinery.metallurgy.blast_stove.BlastStoveBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.casting_basin.CastingBasinBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.casting_basin.CastingBasinRenderer;
import com.drmangotea.tfmg.content.machinery.metallurgy.coke_oven.CokeOvenBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.coke_oven.CokeOvenRenderer;
import com.drmangotea.tfmg.content.machinery.misc.air_intake.AirIntakeBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.air_intake.AirIntakeRenderer;
import com.drmangotea.tfmg.content.machinery.misc.concrete_hose.ConcreteHoseBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.concrete_hose.ConcreteHoseInstance;
import com.drmangotea.tfmg.content.machinery.misc.concrete_hose.ConcreteHoseRenderer;
import com.drmangotea.tfmg.content.machinery.misc.exhaust.ExhaustBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.firebox.FireboxBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.flarestack.FlarestackBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.machine_input.MachineInputBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.smokestack.SmokestackBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.vat_machines.base.VatBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.vat_machines.base.VatRenderer;
import com.drmangotea.tfmg.content.machinery.misc.vat_machines.electrode_holder.ElectrodeHolderBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.vat_machines.electrode_holder.ElectrodeHolderRenderer;
import com.drmangotea.tfmg.content.machinery.misc.vat_machines.industrial_mixer.IndustrialMixerBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.vat_machines.industrial_mixer.IndustrialMixerInstance;
import com.drmangotea.tfmg.content.machinery.misc.vat_machines.industrial_mixer.IndustrialMixerRenderer;
import com.drmangotea.tfmg.content.machinery.misc.winding_machine.WindingMachineBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.winding_machine.WindingMachineInstance;
import com.drmangotea.tfmg.content.machinery.misc.winding_machine.WindingMachineRenderer;
import com.drmangotea.tfmg.content.machinery.oil_processing.distillation_tower.controller.DistillationControllerBlockEntity;
import com.drmangotea.tfmg.content.machinery.oil_processing.distillation_tower.controller.DistillationControllerRenderer;
import com.drmangotea.tfmg.content.machinery.oil_processing.distillation_tower.output.DistillationOutputBlockEntity;
import com.drmangotea.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.base.PumpjackBaseBlockEntity;
import com.drmangotea.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.crank.PumpjackCrankBlockEntity;
import com.drmangotea.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.crank.PumpjackCrankRenderer;
import com.drmangotea.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.hammer.PumpjackBlockEntity;
import com.drmangotea.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.hammer.PumpjackRenderer;
import com.drmangotea.tfmg.content.machinery.oil_processing.surface_scanner.SurfaceScannerBlockEntity;
import com.drmangotea.tfmg.content.machinery.oil_processing.surface_scanner.SurfaceScannerRenderer;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.content.fluids.pipes.SmartFluidPipeBlockEntity;
import com.simibubi.create.content.fluids.pipes.StraightPipeBlockEntity;
import com.simibubi.create.content.fluids.pipes.TransparentStraightPipeRenderer;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlockEntity;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveInstance;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveRenderer;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import com.simibubi.create.content.fluids.pump.PumpCogInstance;
import com.simibubi.create.content.fluids.pump.PumpRenderer;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.*;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity;
import com.simibubi.create.content.kinetics.gearbox.GearboxInstance;
import com.simibubi.create.content.kinetics.gearbox.GearboxRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.drmangotea.tfmg.TFMG.REGISTRATE;
import static com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours.assignDataBehaviourBE;

public class TFMGBlockEntities {
    public static final BlockEntityEntry<DistillationOutputBlockEntity> STEEL_DISTILLATION_OUTPUT = REGISTRATE
            .blockEntity("distillation_tower_output", DistillationOutputBlockEntity::new)
            .validBlocks(TFMGBlocks.STEEL_DISTILLATION_OUTPUT)
            .register();
    public static final BlockEntityEntry<ElectricPumpBlockEntity> ELECTRIC_PUMP = REGISTRATE
            .blockEntity("electric_pump", ElectricPumpBlockEntity::new)
            .validBlocks(TFMGBlocks.ELECTRIC_PUMP)
            .register();
    public static final BlockEntityEntry<ElectricSwitchBlockEntity> ELECTRIC_SWITCH = REGISTRATE
            .blockEntity("electrical_switch", ElectricSwitchBlockEntity::new)
            .validBlocks(TFMGBlocks.ELECTRICAL_SWITCH)
            .register();
    public static final BlockEntityEntry<PolarizerBlockEntity> POLARIZER = REGISTRATE
            .blockEntity("polarizer", PolarizerBlockEntity::new)
            .validBlocks(TFMGBlocks.POLARIZER)
            .renderer(() -> PolarizerRenderer::new)
            .register();
    public static final BlockEntityEntry<CopycatCableBlockEntity> COPYCAT_CABLE =
            REGISTRATE.blockEntity("copycat_cable", CopycatCableBlockEntity::new)
                    .validBlocks(TFMGBlocks.COPYCAT_CABLE_BLOCK)
                    .register();
    public static final BlockEntityEntry<ResistorBlockEntity> RESISTOR = REGISTRATE
            .blockEntity("resistor", ResistorBlockEntity::new)
            .validBlocks(TFMGBlocks.RESISTOR)
            .register();
    public static final BlockEntityEntry<StatorBlockEntity>  STATOR = REGISTRATE
            .blockEntity("stator", StatorBlockEntity::new)
            .validBlocks(TFMGBlocks.STATOR)
            .register();

    public static final BlockEntityEntry<NeonTubeBlockEntity>  NEON_TUBE = REGISTRATE
            .blockEntity("neon_tube", NeonTubeBlockEntity::new)
            .validBlocks(TFMGBlocks.NEON_TUBE)
            .renderer(() -> NeonTubeRenderer::new)
            .register();

    public static final BlockEntityEntry<DiagonalCableBlockEntity> DIAGONAL_CABLE_BLOCK = REGISTRATE
            .blockEntity("diagonal_cable_block", DiagonalCableBlockEntity::new)
            .validBlocks(TFMGBlocks.DIAGONAL_CABLE_BLOCK)
            .register();

    public static final BlockEntityEntry<CableTubeBlockEntity> CABLE_TUBE = REGISTRATE
            .blockEntity("cable_tube", CableTubeBlockEntity::new)
            .validBlocks(TFMGBlocks.CABLE_TUBE, TFMGBlocks.ELECTRIC_POST,
                    TFMGBlocks.CONCRETE_ENCASED_CABLE_TUBE, TFMGBlocks.CONCRETE_ENCASED_ELECTRIC_POST)
            .register();

    public static final BlockEntityEntry<RotorBlockEntity> ROTOR = REGISTRATE
            .blockEntity("rotor", RotorBlockEntity::new)
            .instance(() -> RotorInstance::new, false)
            .validBlocks(TFMGBlocks.ROTOR)
            .renderer(() -> RotorRenderer::new)
            .register();
    public static final BlockEntityEntry<ConcreteHoseBlockEntity> CONCRETE_HOSE = REGISTRATE
            .blockEntity("concrete_hose", ConcreteHoseBlockEntity::new)
            .instance(() -> ConcreteHoseInstance::new)
            .validBlocks(TFMGBlocks.CONCRETE_HOSE)
            .renderer(() -> ConcreteHoseRenderer::new)
            .register();
    public static final BlockEntityEntry<PumpjackBlockEntity> PUMPJACK_HAMMER = REGISTRATE
            .blockEntity("pumpjack_hammer", PumpjackBlockEntity::new)
            .validBlocks(TFMGBlocks.PUMPJACK_HAMMER)
            .renderer(() -> PumpjackRenderer::new)
            .register();

    public static final BlockEntityEntry<PumpjackCrankBlockEntity> PUMPJACK_CRANK = REGISTRATE
            .blockEntity("pumpjack_crank", PumpjackCrankBlockEntity::new)
            .validBlocks(TFMGBlocks.PUMPJACK_CRANK)
            .renderer(() -> PumpjackCrankRenderer::new)
            .register();

    public static final BlockEntityEntry<PumpjackBaseBlockEntity> PUMPJACK_BASE = REGISTRATE
            .blockEntity("pumpjack_base", PumpjackBaseBlockEntity::new)
            .validBlocks(TFMGBlocks.PUMPJACK_BASE)
            .register();
    public static final BlockEntityEntry<ConverterBlockEntity> CONVERTER = REGISTRATE
            .blockEntity("converter", ConverterBlockEntity::new)
            .validBlocks(TFMGBlocks.CONVERTER)
            .register();
    public static final BlockEntityEntry<CastingBasinBlockEntity> CASTING_BASIN = REGISTRATE
            .blockEntity("casting_basin", CastingBasinBlockEntity::new)
            .validBlocks(TFMGBlocks.CASTING_BASIN)
            .renderer(() -> CastingBasinRenderer::new)
            .register();

    public static final BlockEntityEntry<SurfaceScannerBlockEntity> SURFACE_SCANNER = REGISTRATE
            .blockEntity("surface_scanner", SurfaceScannerBlockEntity::new)
            .validBlocks(TFMGBlocks.SURFACE_SCANNER)
            .renderer(() -> SurfaceScannerRenderer::new)
            .register();
    public static final BlockEntityEntry<FireboxBlockEntity> FIREBOX = REGISTRATE
            .blockEntity("firebox", FireboxBlockEntity::new)
            .validBlocks(TFMGBlocks.FIREBOX)
            .register();
    public static final BlockEntityEntry<VoltageObserverBlockEntity> VOLTAGE_OBSERVER = REGISTRATE
            .blockEntity("voltage_observer", VoltageObserverBlockEntity::new)
            .validBlocks(TFMGBlocks.VOLTAGE_OBSERVER)
            .register();
    public static final BlockEntityEntry<DistillationControllerBlockEntity> STEEL_DISTILLATION_CONTROLLER = REGISTRATE
            .blockEntity("distillation_tower_controller", DistillationControllerBlockEntity::new)
            .validBlocks(TFMGBlocks.STEEL_DISTILLATION_CONTROLLER)
            .renderer(() -> DistillationControllerRenderer::new)
            .register();
    public static final BlockEntityEntry<MachineInputBlockEntity> MACHINE_INPUT = REGISTRATE
            .blockEntity("machine_input", MachineInputBlockEntity::new)
            .instance(() -> HalfShaftInstance::new,true)
            .validBlocks(TFMGBlocks.MACHINE_INPUT)
            .renderer(() -> HalfShaftRenderer::new)
            .register();
    public static final BlockEntityEntry<LightBulbBlockEntity> LIGHT_BULB = REGISTRATE
            .blockEntity("light_bulb", LightBulbBlockEntity::new)
            .validBlocks(TFMGBlocks.LIGHT_BULB)
            .renderer(() -> LightBulbRenderer::new)
            .register();
    public static final BlockEntityEntry<LightBulbBlockEntity> CIRCULAR_LIGHT = REGISTRATE
            .blockEntity("circular_light", LightBulbBlockEntity::new)
            .validBlocks(TFMGBlocks.CIRCULAR_LIGHT)
            .renderer(() -> CircularLightRenderer::new)
            .register();
    public static final BlockEntityEntry<LightBulbBlockEntity> MODERN_LIGHT = REGISTRATE
            .blockEntity("modern_light", LightBulbBlockEntity::new)
            .validBlocks(TFMGBlocks.MODERN_LIGHT)
            .renderer(() -> ModernLightRenderer::new)
            .register();
    public static final BlockEntityEntry<LightBulbBlockEntity> ALUMINUM_LAMP = REGISTRATE
            .blockEntity("aluminum_lamp", LightBulbBlockEntity::new)
            .validBlocks(TFMGBlocks.ALUMINUM_LAMP)
            .renderer(() -> AluminumLampRenderer::new)
            .register();

    public static final BlockEntityEntry<AccumulatorBlockEntity> ACCUMULATOR = REGISTRATE
            .blockEntity("accumulator", AccumulatorBlockEntity::new)
            .validBlocks(TFMGBlocks.ACCUMULATOR)
            .register();

    public static final BlockEntityEntry<ElectricDiodeBlockEntity> DIODE = REGISTRATE
            .blockEntity("electric_diode", ElectricDiodeBlockEntity::new)
            .validBlocks(TFMGBlocks.DIODE,TFMGBlocks.ENCASED_DIODE)
            .register();


    public static final BlockEntityEntry<RegularEngineBlockEntity> REGULAR_ENGINE = REGISTRATE
            .blockEntity("regular_engine", RegularEngineBlockEntity::new)
            .instance(() -> EngineInstance::new, true)
            .renderer(() -> RegularEngineRenderer::new)
            .validBlocks(TFMGBlocks.REGULAR_ENGINE)
            .register();

    public static final BlockEntityEntry<PotentiometerBlockEntity> POTENTIOMETER = REGISTRATE
            .blockEntity("potentiometer", PotentiometerBlockEntity::new)
            .validBlocks(TFMGBlocks.POTENTIOMETER,TFMGBlocks.ENCASED_POTENTIOMETER)
            .register();

    public static final BlockEntityEntry<TFMGSlidingDoorBlockEntity> TFMG_SLIDING_DOOR =
            REGISTRATE.blockEntity("tfmg_sliding_door", TFMGSlidingDoorBlockEntity::new)
                    .renderer(() -> TFMGSlidingDoorRenderer::new)
                    .validBlocks(TFMGBlocks.HEAVY_CASING_DOOR, TFMGBlocks.STEEL_CASING_DOOR, TFMGBlocks.HEAVY_PLATED_DOOR, TFMGBlocks.ALUMINUM_DOOR)
                    .register();
    public static final BlockEntityEntry<EngineGearboxBlockEntity> ENGINE_GEARBOX = REGISTRATE
            .blockEntity("engine_gearbox", EngineGearboxBlockEntity::new)
            .instance(() -> ShaftInstance::new, false)
            .validBlocks(TFMGBlocks.ENGINE_GEARBOX)
            .renderer(() -> EngineGearboxRenderer::new)
            .register();

    public static final BlockEntityEntry<EngineControllerBlockEntity> ENGINE_CONTROLLER = REGISTRATE
            .blockEntity("engine_controller", EngineControllerBlockEntity::new)
           // .instance(() -> ShaftInstance::new, false)
            .validBlocks(TFMGBlocks.ENGINE_CONTROLLER)
            //.renderer(() -> EngineGearboxRenderer::new)
            .register();
    public static final BlockEntityEntry<IndustrialMixerBlockEntity> INDUSTRIAL_MIXER = REGISTRATE
            .blockEntity("industrial_mixer", IndustrialMixerBlockEntity::new)
            .instance(() -> IndustrialMixerInstance::new, true)
            .renderer(() -> IndustrialMixerRenderer::new)
            .validBlocks(TFMGBlocks.INDUSTRIAL_MIXER)
            .register();
    public static final BlockEntityEntry<ElectrodeHolderBlockEntity> ELECTRODE_HOLDER = REGISTRATE
            .blockEntity("electrode_holder", ElectrodeHolderBlockEntity::new)
            .validBlocks(TFMGBlocks.ELECTRODE_HOLDER)
            .renderer(() -> ElectrodeHolderRenderer::new)
            .register();
    // public static final BlockEntityEntry<CatalystHolderBlockEntity> CATALYST_HOLDER = REGISTRATE
    //         .blockEntity("catalyst_holder", CatalystHolderBlockEntity::new)
    //         .validBlocks(TFMGBlocks.ELECTRODE_HOLDER)
    //         .register();


    public static final BlockEntityEntry<SteelTankBlockEntity> STEEL_FLUID_TANK = REGISTRATE
            .blockEntity("steel_fluid_tank", SteelTankBlockEntity::new)
            .validBlocks(TFMGBlocks.STEEL_FLUID_TANK)
            .renderer(() -> SteelFluidTankRenderer::new)
            .register();

    public static final BlockEntityEntry<FluidTankBlockEntity> TFMG_FLUID_TANK = REGISTRATE
            .blockEntity("tfmg_fluid_tank", FluidTankBlockEntity::new)
            .validBlocks(TFMGBlocks.ALUMINUM_FLUID_TANK, TFMGBlocks.CAST_IRON_FLUID_TANK)
            .renderer(() -> TFMGFluidTankRenderer::new)
            .register();


    public static final BlockEntityEntry<VatBlockEntity> CHEMICAL_VAT = REGISTRATE
            .blockEntity("chemical_vat", VatBlockEntity::new)
            .validBlocks(TFMGBlocks.STEEL_CHEMICAL_VAT)
            .renderer(() -> VatRenderer::new)
            .register();
    public static final BlockEntityEntry<BlastStoveBlockEntity> BLAST_STOVE = REGISTRATE
            .blockEntity("blast_stove", BlastStoveBlockEntity::new)
            .validBlocks(TFMGBlocks.BLAST_STOVE)
            .register();



    public static final BlockEntityEntry<CreativeGeneratorBlockEntity> CREATIVE_GENERATOR = REGISTRATE
            .blockEntity("creative_generator", CreativeGeneratorBlockEntity::new)
            .validBlocks(TFMGBlocks.CREATIVE_GENERATOR)
            .register();

    public static final BlockEntityEntry<VoltMeterBlockEntity> VOLTMETER = REGISTRATE
            .blockEntity("voltmeter", VoltMeterBlockEntity::new)
            .validBlocks(TFMGBlocks.VOLTMETER)
            .renderer(() -> VoltMeterRenderer::new)
            .register();
    public static final BlockEntityEntry<AmmeterBlockEntity> AMMETER = REGISTRATE
            .blockEntity("ammeter", AmmeterBlockEntity::new)
            .validBlocks(TFMGBlocks.AMMETER)
            .renderer(() -> VoltMeterRenderer::new)
            .register();

    public static final BlockEntityEntry<TrafficLightBlockEntity> TRAFFIC_LIGHT = REGISTRATE
            .blockEntity("traffic_light", TrafficLightBlockEntity::new)
            .validBlocks(TFMGBlocks.TRAFFIC_LIGHT)
            .renderer(() -> TrafficLightRenderer::new)
            .register();
    public static final BlockEntityEntry<TransformerBlockEntity> TRANSFORMER = REGISTRATE
            .blockEntity("transformer", TransformerBlockEntity::new)
            .validBlocks(TFMGBlocks.TRANSFORMER)
            .renderer(() -> TransformerRenderer::new)
            .register();
    public static final BlockEntityEntry<FuseBlockEntity> FUSE_BLOCK = REGISTRATE
            .blockEntity("fuse_block", FuseBlockEntity::new)
            .validBlocks(TFMGBlocks.FUSE_BLOCK)
            .renderer(() -> FuseBlockRenderer::new)
            .register();
    public static final BlockEntityEntry<SegmentedDisplayBlockEntity> SEGMENTED_DISPLAY = REGISTRATE
            .blockEntity("segmented_display", SegmentedDisplayBlockEntity::new)
            .validBlocks(TFMGBlocks.SEGMENTED_DISPLAY)
            .renderer(() -> SegmentedDisplayRenderer::new)
            .onRegister(assignDataBehaviourBE(new SegmentedDisplayTarget()))
            .onRegister(assignDataBehaviourBE(new SegmentedDisplaySource()))
            .register();

    public static final BlockEntityEntry<SmokestackBlockEntity> SMOKESTACK = REGISTRATE
            .blockEntity("smokestack", SmokestackBlockEntity::new)
            .validBlocks(TFMGBlocks.BRICK_SMOKESTACK, TFMGBlocks.METAL_SMOKESTACK, TFMGBlocks.CONCRETE_SMOKESTACK)
            .register();

    public static final BlockEntityEntry<ExhaustBlockEntity> EXHAUST = REGISTRATE
            .blockEntity("exhaust", ExhaustBlockEntity::new)
            .validBlocks(TFMGBlocks.EXHAUST)
            .register();

    public static final BlockEntityEntry<FlarestackBlockEntity> FLARESTACK = REGISTRATE
            .blockEntity("flarestack", FlarestackBlockEntity::new)
            .validBlocks(TFMGBlocks.FLARESTACK)
            .register();

    public static final BlockEntityEntry<SimpleKineticBlockEntity> TFMG_COGWHEEL = REGISTRATE
            .blockEntity("tfmg_simple_kinetic", SimpleKineticBlockEntity::new)
            .instance(() -> TFMGCogwheelInstance::new, false)
            .validBlocks(TFMGBlocks.STEEL_COGWHEEL, TFMGBlocks.LARGE_STEEL_COGWHEEL, TFMGBlocks.ALUMINUM_COGWHEEL, TFMGBlocks.LARGE_ALUMINUM_COGWHEEL)
            .renderer(() -> TFMGCogwheelRenderer::new)
            .register();

    public static final BlockEntityEntry<SimpleKineticBlockEntity> ENCASED_STEEL_COGWHEEL = REGISTRATE
            .blockEntity("tfmg_encased_steel_cogwheel", SimpleKineticBlockEntity::new)
            .instance(() -> EncasedSteelCogInstance::small, false)
            .validBlocks(
                    TFMGEncasedBlocks.STEEL_ENCASED_STEEL_COGWHEEL,
                    TFMGEncasedBlocks.HEAVY_CASING_ENCASED_STEEL_COGWHEEL

            )
            .renderer(() -> EncasedSteelCogRenderer::small)
            .register();

    public static final BlockEntityEntry<SimpleKineticBlockEntity> ENCASED_LARGE_STEEL_COGWHEEL = REGISTRATE
            .blockEntity("encased_large_steel_cogwheel", SimpleKineticBlockEntity::new)
            .instance(() -> EncasedSteelCogInstance::large, false)
            .validBlocks(
                    TFMGEncasedBlocks.STEEL_ENCASED_LARGE_STEEL_COGWHEEL,
                    TFMGEncasedBlocks.HEAVY_CASING_ENCASED_LARGE_STEEL_COGWHEEL
            )

            .renderer(() -> EncasedSteelCogRenderer::large)
            .register();
    public static final BlockEntityEntry<SimpleKineticBlockEntity> ENCASED_ALUMINUM_COGWHEEL = REGISTRATE
            .blockEntity("tfmg_encased_aluminum_cogwheel", SimpleKineticBlockEntity::new)
            .instance(() -> EncasedAluminumCogInstance::small, false)
            .validBlocks(
                    TFMGEncasedBlocks.STEEL_ENCASED_ALUMINUM_COGWHEEL,
                    TFMGEncasedBlocks.HEAVY_CASING_ENCASED_ALUMINUM_COGWHEEL

            )
            .renderer(() -> EncasedAluminumCogRenderer::small)
            .register();

    public static final BlockEntityEntry<SimpleKineticBlockEntity> ENCASED_LARGE_ALUMINUM_COGWHEEL = REGISTRATE
            .blockEntity("encased_large_aluminum_cogwheel", SimpleKineticBlockEntity::new)
            .instance(() -> EncasedAluminumCogInstance::large, false)
            .validBlocks(
                    TFMGEncasedBlocks.STEEL_ENCASED_LARGE_ALUMINUM_COGWHEEL,
                    TFMGEncasedBlocks.HEAVY_CASING_ENCASED_LARGE_ALUMINUM_COGWHEEL
            )

            .renderer(() -> EncasedAluminumCogRenderer::large)
            .register();

    public static final BlockEntityEntry<KineticBlockEntity> TFMG_ENCASED_SHAFT = REGISTRATE
            .blockEntity("tfmg_encased_shaft", KineticBlockEntity::new)
            .instance(() -> ShaftInstance::new, false)
            .validBlocks(TFMGEncasedBlocks.STEEL_ENCASED_SHAFT, TFMGEncasedBlocks.HEAVY_CASING_ENCASED_SHAFT)
            .renderer(() -> ShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<GearboxBlockEntity> STEEL_GEARBOX = REGISTRATE
            .blockEntity("steel_gearbox", GearboxBlockEntity::new)
            .instance(() -> GearboxInstance::new, false)
            .validBlocks(TFMGBlocks.STEEL_GEARBOX)
            .renderer(() -> GearboxRenderer::new)
            .register();

    public static final BlockEntityEntry<TFMGFlywheelBlockEntity> TFMG_FLYWHEEL = REGISTRATE
            .blockEntity("steel_flywheel", TFMGFlywheelBlockEntity::new)
            .instance(() -> TFMGFlywheelInstance::new, false)
            .validBlocks(
                    TFMGBlocks.STEEL_FLYWHEEL,
                    TFMGBlocks.ALUMINUM_FLYWHEEL,
                    TFMGBlocks.CAST_IRON_FLYWHEEL,
                    TFMGBlocks.LEAD_FLYWHEEL,
                    TFMGBlocks.NICKEL_FLYWHEEL

            )
            .renderer(() -> TFMGFlywheelRenderer::new)
            .register();

    public static final BlockEntityEntry<BlastFurnaceOutputBlockEntity> BLAST_FURNACE_OUTPUT = REGISTRATE
            .blockEntity("blast_furnace_output", BlastFurnaceOutputBlockEntity::new)
            .renderer(() -> BlastFurnaceRenderer::new)
            .validBlocks(TFMGBlocks.BLAST_FURNACE_OUTPUT)
            .register();

    public static final BlockEntityEntry<BlastFurnaceHatchBlockEntity> BLAST_FURNACE_HATCH = REGISTRATE
            .blockEntity("blast_furnace_hatch", BlastFurnaceHatchBlockEntity::new)
            .validBlocks(TFMGBlocks.BLAST_FURNACE_HATCH)
            .register();

    public static final BlockEntityEntry<ElectricBlockEntity> DEBUG_ELECTRIC_BLOCK = REGISTRATE
            .blockEntity("debug_electric_block", ElectricBlockEntity::new)
            .validBlocks(TFMGBlocks.DEBUG_ELECTRIC_BLOCK)
            .register();

    public static final BlockEntityEntry<CableHubBlockEntity> CABLE_HUB = REGISTRATE
            .blockEntity("cable_hub", CableHubBlockEntity::new)
            .validBlocks(TFMGBlocks.DEBUG_ELECTRIC_BLOCK, TFMGBlocks.BRASS_CABLE_HUB,
                    TFMGBlocks.COPPER_CABLE_HUB,
                    TFMGBlocks.STEEL_CABLE_HUB,
                    TFMGBlocks.ALUMINUM_CABLE_HUB,
                    TFMGBlocks.HEAVY_CABLE_HUB,
                    TFMGBlocks.STEEL_CASING_CABLE_HUB

            )
            .register();

    public static final BlockEntityEntry<DebugGeneratorBlockEntity> DEBUG_GENERATOR_BLOCK = REGISTRATE
            .blockEntity("debug_generator_block", DebugGeneratorBlockEntity::new)
            .validBlocks(TFMGBlocks.DEBUG_GENERATOR_BLOCK)
            .register();

    public static final BlockEntityEntry<CokeOvenBlockEntity> COKE_OVEN = REGISTRATE
            .blockEntity("coke_oven", CokeOvenBlockEntity::new)
            .renderer(() -> CokeOvenRenderer::new)
            .validBlocks(TFMGBlocks.COKE_OVEN)
            .register();

    public static final BlockEntityEntry<GeneratorBlockEntity> GENERATOR = REGISTRATE
            .blockEntity("generator", GeneratorBlockEntity::new)
            .instance(() -> HalfShaftInstance::new)
            .validBlocks(TFMGBlocks.GENERATOR)
            .renderer(() -> HalfShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<ElectricMotorBlockEntity> ELECTRIC_MOTOR = REGISTRATE
            .blockEntity("electric_motor", ElectricMotorBlockEntity::new)
            .instance(() -> HalfShaftInstance::new)
            .validBlocks(TFMGBlocks.ELECTRIC_MOTOR)
            .renderer(() -> HalfShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<AirIntakeBlockEntity> AIR_INTAKE = REGISTRATE
            .blockEntity("air_intake", AirIntakeBlockEntity::new)
            .renderer(() -> AirIntakeRenderer::new)
            .validBlocks(TFMGBlocks.AIR_INTAKE)
            .register();


    public static final BlockEntityEntry<CableConnectorBlockEntity> CABLE_CONNECTOR = REGISTRATE
            .blockEntity("cable_connector", CableConnectorBlockEntity::new)
            .renderer(() -> CableConnectorRenderer::new)
            .validBlocks(TFMGBlocks.CABLE_CONNECTOR, TFMGBlocks.GLASS_CABLE_CONNECTOR)
            .register();

    public static final BlockEntityEntry<WindingMachineBlockEntity> WINDING_MACHINE = REGISTRATE
            .blockEntity("winding_machine", WindingMachineBlockEntity::new)
            .instance(() -> WindingMachineInstance::new)
            .validBlocks(TFMGBlocks.WINDING_MACHINE)
            .renderer(() -> WindingMachineRenderer::new)
            .register();


    public static final BlockEntityEntry<TFMGPipeBlockEntity> TFMG_PIPE = REGISTRATE
            .blockEntity("tfmg_pipe", TFMGPipeBlockEntity::new)
            .validBlocks(
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.STEEL).get(0),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.ALUMINUM).get(0),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.BRASS).get(0),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.CAST_IRON).get(0),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.PLASTIC).get(0)
            )
            .register();

    public static final BlockEntityEntry<StraightPipeBlockEntity> GLASS_TFMG_PIPE = REGISTRATE
            .blockEntity("glass_tfmg_pipe", StraightPipeBlockEntity::new)
            .validBlocks(
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.STEEL).get(2),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.ALUMINUM).get(2),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.BRASS).get(2),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.CAST_IRON).get(2),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.PLASTIC).get(2))
            .renderer(() -> TransparentStraightPipeRenderer::new)
            .register();


    public static final BlockEntityEntry<FluidPipeBlockEntity> ENCASED_TFMG_PIPE = REGISTRATE
            .blockEntity("encased_tfmg_pipe", FluidPipeBlockEntity::new)
            .validBlocks(
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.STEEL).get(1),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.ALUMINUM).get(1),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.BRASS).get(1),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.CAST_IRON).get(1),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.PLASTIC).get(1))
            .register();


    public static final BlockEntityEntry<PumpBlockEntity> TFMG_MECHANICAL_PUMP = REGISTRATE
            .blockEntity("mechanical_pump", PumpBlockEntity::new)
            .instance(() -> PumpCogInstance::new)
            .validBlocks(
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.STEEL).get(3),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.ALUMINUM).get(3),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.BRASS).get(3),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.CAST_IRON).get(3),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.PLASTIC).get(3)
            )
            .renderer(() -> PumpRenderer::new)
            .register();

    public static final BlockEntityEntry<SmartFluidPipeBlockEntity> TFMG_SMART_FLUID_PIPE = REGISTRATE
            .blockEntity("smart_fluid_pipe", SmartFluidPipeBlockEntity::new)
            .validBlocks(
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.STEEL).get(4),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.ALUMINUM).get(4),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.BRASS).get(4),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.CAST_IRON).get(4),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.PLASTIC).get(4)
            )
            .renderer(() -> SmartBlockEntityRenderer::new)
            .register();

    public static final BlockEntityEntry<FluidValveBlockEntity> TFMG_FLUID_VALVE = REGISTRATE
            .blockEntity("fluid_valve", FluidValveBlockEntity::new)
            .instance(() -> FluidValveInstance::new)
            .validBlocks(
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.STEEL).get(5),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.ALUMINUM).get(5),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.BRASS).get(5),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.CAST_IRON).get(5),
                    TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.PLASTIC).get(5)
            )
            .renderer(() -> FluidValveRenderer::new)
            .register();


    public static void init() {
    }
}
