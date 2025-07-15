package it.bohdloss.tfmg.content.machinery.misc.concrete_hose;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import it.bohdloss.tfmg.base.TFMGFluidFillingBehaviour;
import it.bohdloss.tfmg.content.decoration.concrete.ConcreteloggedBlock;
import it.bohdloss.tfmg.registry.TFMGFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class ConcreteFillingBehavior extends TFMGFluidFillingBehaviour {

    public static final BehaviourType<ConcreteFillingBehavior> TYPE = new BehaviourType<>();

    public ConcreteFillingBehavior(SmartBlockEntity be) {
        super(be);
    }

    @Override
    protected SpaceType getAtPos(Level world, BlockPos pos, Fluid toFill) {
        if(world.getBlockState(pos).hasProperty(ConcreteloggedBlock.CONCRETELOGGED)) {
            return super.getAtPos(world, pos, toFill);
        } else {
            return SpaceType.BLOCKING;
        }
    }

    protected boolean canFluidLog(BlockState blockState, Fluid fluid) {
        return canFluidLog(blockState) && fluid.isSame(TFMGFluids.LIQUID_CONCRETE.getSource());
    }

    protected boolean canFluidLog(BlockState blockState) {
        return blockState.hasProperty(ConcreteloggedBlock.CONCRETELOGGED);
    }

    protected BlockState doFluidLog(BlockState blockState, Fluid fluid) {
        return blockState.setValue(ConcreteloggedBlock.CONCRETELOGGED, true);
    }

    protected boolean isFluidLogged(BlockState blockState, Fluid fluid) {
        return blockState.getValue(ConcreteloggedBlock.CONCRETELOGGED);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
