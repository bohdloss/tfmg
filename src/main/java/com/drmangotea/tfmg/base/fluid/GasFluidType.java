package com.drmangotea.tfmg.base.fluid;

import com.drmangotea.tfmg.registry.TFMGFluids;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.Color;
import com.tterrag.registrate.builders.FluidBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
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