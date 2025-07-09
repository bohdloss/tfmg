package it.bohdloss.tfmg.base.fluid;

import com.simibubi.create.AllFluids;
import com.simibubi.create.Create;
import com.tterrag.registrate.builders.FluidBuilder;
import it.bohdloss.tfmg.registry.TFMGDamageSources;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class AcidFluidType extends AllFluids.TintedFluidType {
    public AcidFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        super(properties, stillTexture, flowingTexture);
    }
    private Vector3f fogColor;
    private Supplier<Float> fogDistance;

    public static FluidBuilder.FluidTypeFactory  create(int fogColor, Supplier<Float> fogDistance) {
        return (p, s, f) -> {
            AcidFluidType fluidType = new AcidFluidType(p, s, f);
            fluidType.fogColor = new Color(fogColor, false).asVectorF();
            fluidType.fogDistance = fogDistance;
            return fluidType;
        };
    }
    @Override
    protected Vector3f getCustomFogColor() {
        return fogColor;
    }

    @Override
    protected float getFogDistanceModifier() {
        return fogDistance.get();
    }

    @Override
    protected int getTintColor(FluidStack stack) {
        return NO_TINT;
    }
    @Override
    public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
        return 0x00ffffff;
    }


    @Override
    public boolean move(@NotNull FluidState state, @NotNull LivingEntity entity, @NotNull Vec3 movementVector, double gravity) {
        if(Create.RANDOM.nextInt(2) == 0) {
            entity.hurt(TFMGDamageSources.acid(entity.level()),2);
        }

        return false;
    }
}
