package com.drmangotea.tfmg.content.engines.base;

import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public class EngineFluidTank extends SmartFluidTank {


    final boolean extractionAllowed;
    final boolean insertionAllowed;

    public EngineFluidTank(int capacity, boolean extractionAllowed, boolean insertionAllowed, Consumer<FluidStack> updateCallback) {
        super(capacity, updateCallback);
        this.extractionAllowed = extractionAllowed;
        this.insertionAllowed = insertionAllowed;
    }


    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (!extractionAllowed) return FluidStack.EMPTY;
        return super.drain(resource, action);
    }

    public FluidStack forceDrain(FluidStack resource, FluidAction action){
        return super.drain(resource,action);
    }


    public FluidStack forceDrain(int resource, FluidAction action){
        return super.drain(resource,action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (!extractionAllowed) return FluidStack.EMPTY;
        return super.drain(maxDrain, action);
    }

    public int forceFill(FluidStack resource, FluidAction action) {
        return super.fill(resource, action);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (!insertionAllowed) return 0;
        return super.fill(resource, action);
    }
}
