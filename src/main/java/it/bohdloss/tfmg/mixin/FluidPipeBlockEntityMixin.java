package it.bohdloss.tfmg.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import it.bohdloss.tfmg.DebugStuff;
import it.bohdloss.tfmg.content.decoration.pipes.ILockablePipe;
import it.bohdloss.tfmg.content.decoration.pipes.LockedPipeBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(FluidPipeBlockEntity.class)
public class FluidPipeBlockEntityMixin implements ILockablePipe {
    @Unique
    private LockedPipeBehavior tfmg$locked;

    @Override
    public boolean tfmg$canPipeBeLocked() {
        FluidPipeBlockEntity self = (FluidPipeBlockEntity) (Object) this;
        if(self.getBlockState().getBlock() instanceof EncasedPipeBlock) {
            return false;
        }
        return true;
    }

    @Override
    public boolean tfmg$isPipeLocked() {
        FluidPipeBlockEntity self = (FluidPipeBlockEntity) (Object) this;
        if(self.getBlockState().getBlock() instanceof EncasedPipeBlock) {
            return false;
        }

        return tfmg$locked != null && tfmg$locked.locked;
    }

    @Override
    public void tfmg$setPipeLocked(Player player, boolean locked) {
        FluidPipeBlockEntity self = (FluidPipeBlockEntity) (Object) this;
        if(self.getBlockState().getBlock() instanceof EncasedPipeBlock) {
            return;
        }

        self.getLevel().playSound(player, self.getBlockPos(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4f, 0.5f);

        tfmg$locked.locked = locked;
        if (locked) {
            return;
        }

        BlockState newState;
        Level world = self.getLevel();
        BlockPos pos = self.getBlockPos();
        FluidTransportBehaviour.cacheFlows(world, pos);
        newState = tfmg$updatePipe(world, pos, self.getBlockState()).setValue(BlockStateProperties.WATERLOGGED, self.getBlockState().getValue(BlockStateProperties.WATERLOGGED));
        world.setBlock(pos, newState, 3);
        FluidTransportBehaviour.loadFlows(world, pos);
    }

    @Unique
    public BlockState tfmg$updatePipe(LevelAccessor world, BlockPos pos, BlockState state) {
        Direction side = Direction.UP;
        Map<Direction, BooleanProperty> facingToPropertyMap = FluidPipeBlock.PROPERTY_BY_DIRECTION;
        return AllBlocks.FLUID_PIPE.get()
                .updateBlockState(state.getBlock().defaultBlockState()
                        .setValue(facingToPropertyMap.get(side), true)
                        .setValue(facingToPropertyMap.get(side.getOpposite()), true), side, null, world, pos);
    }

    @Inject(at = @At("HEAD"), method = "addBehaviours")
    void addBehaviors(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        FluidPipeBlockEntity self = (FluidPipeBlockEntity) (Object) this;
        if(self.getBlockState().getBlock() instanceof EncasedPipeBlock) {
            return;
        }
        tfmg$locked = new LockedPipeBehavior(self);
        behaviours.add(tfmg$locked);
    }
}
