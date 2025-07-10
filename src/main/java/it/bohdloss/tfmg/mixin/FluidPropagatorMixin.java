package it.bohdloss.tfmg.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FluidPropagator.class)
public class FluidPropagatorMixin {
    @ModifyVariable(ordinal = 2, method = "propagateChangedPipe", at = @At(value = "STORE"))
    private static BlockState propagateChangedPipe(BlockState originalState) {
        if(originalState.getBlock() instanceof PumpBlock) {
            Direction facing = originalState.getValue(PumpBlock.FACING);
            BlockState newState = AllBlocks.MECHANICAL_PUMP.getDefaultState();
            newState.setValue(PumpBlock.FACING, facing);
            return newState;
        } else {
            return originalState;
        }
    }
}
