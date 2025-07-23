package it.bohdloss.tfmg.mixin.fluid_handling;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FluidTankBlockEntity.class)
public interface FluidTankBlockEntityAccessor {
    @Accessor("luminosity")
    int tfmg$getLuminosity();

    @Accessor("window")
    boolean tfmg$getWindow();

    @Accessor("tankInventory")
    FluidTank tfmg$getTankInventory();

    @Accessor("fluidCapability")
    IFluidHandler tfmg$getFluidCapability();

    @Invoker("updateConnectivity")
    void tfmg$updateConnectivity();

    @Invoker("refreshCapability")
    void tfmg$refreshCapability();

    @Invoker("handlerForCapability")
    IFluidHandler tfmg$handlerForCapability();
}
