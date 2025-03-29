package com.drmangotea.tfmg.base.fluid;


import com.tterrag.registrate.builders.FluidBuilder;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class GasFluidType extends FluidType {
    public GasFluidType(Properties properties) {
        super(properties);
    }

    public static FluidBuilder.FluidTypeFactory  create() {
        return (p, s, f) -> {
            HotFluidType fluidType = new HotFluidType(p, s, f);
            return fluidType;
        };
    }

    @Override
    public int getDensity() {
        return -1;
    }
}