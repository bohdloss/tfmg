package it.bohdloss.tfmg.mixin;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import it.bohdloss.tfmg.content.decoration.tanks.IRefreshCapability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FluidTankBlockEntity.class)
public abstract class FluidTankBlockEntityMixin implements IRefreshCapability {
    @Shadow abstract void refreshCapability();

    @Override
    public void tfmg$refreshCapability() {
        refreshCapability();
    }
}
