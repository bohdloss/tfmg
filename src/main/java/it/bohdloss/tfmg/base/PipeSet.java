package it.bohdloss.tfmg.base;

import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.GlassFluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.SmartFluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlock;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.tterrag.registrate.util.entry.BlockEntry;

public record PipeSet(
        BlockEntry<? extends FluidPipeBlock> pipe,
        BlockEntry<? extends EncasedPipeBlock> encased,
        BlockEntry<? extends GlassFluidPipeBlock> glass,
        BlockEntry<? extends PumpBlock> pump,
        BlockEntry<? extends SmartFluidPipeBlock> smartPipe,
        BlockEntry<? extends FluidValveBlock> valve
) {}
