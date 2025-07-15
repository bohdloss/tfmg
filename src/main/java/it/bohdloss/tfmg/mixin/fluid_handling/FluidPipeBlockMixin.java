package it.bohdloss.tfmg.mixin.fluid_handling;

import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import it.bohdloss.tfmg.mixin_interfaces.ILockablePipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.level.block.PipeBlock.PROPERTY_BY_DIRECTION;

@Mixin(FluidPipeBlock.class)
public class FluidPipeBlockMixin {
    @Inject(at = @At("HEAD"), method = "updateBlockState", cancellable = true)
    void updateBlockState(BlockState state, Direction preferredDirection, Direction ignore, BlockAndTintGetter world, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        if(world.getBlockEntity(pos) instanceof ILockablePipe pipe) {
            if(pipe.tfmg$isPipeLocked()) {
                cir.setReturnValue(state);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "canConnectTo", cancellable = true)
    private static void canConnectTo(BlockAndTintGetter world, BlockPos neighbourPos, BlockState neighbour, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (world.getBlockEntity(neighbourPos) instanceof ILockablePipe pipe) {
            if (pipe.tfmg$isPipeLocked()) {
                if (world.getBlockState(neighbourPos).getValue(PROPERTY_BY_DIRECTION.get(direction.getOpposite()))) {
                    cir.setReturnValue(true);
                } else {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
