package it.bohdloss.tfmg.mixin.fluid_handling;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import it.bohdloss.tfmg.mixin_interfaces.IOnRefreshCapability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidTankBlockEntity.class)
public class FluidTankBlockEntityMixin implements IOnRefreshCapability {
    @Inject(method = "refreshCapability", at = @At(value = "HEAD"), cancellable = true)
    void refreshCapability(CallbackInfo ci) {
        if(tfmg$onRefreshCapability()) {
            ci.cancel();
        }
    }

    @Override
    public boolean tfmg$onRefreshCapability() {
        return false;
    }
}
