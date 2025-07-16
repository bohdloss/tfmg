package it.bohdloss.tfmg.mixin.contraptions;

import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MechanicalBearingBlockEntity.class)
public interface MechanicalBearingBlockEntityAccessor {
    @Accessor void setPrevAngle(float prevAngle);
}
