package it.bohdloss.tfmg.content.machinery.misc.machine_input;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class MachineInputPowered extends SmartBlockEntity {
    public MachineInputPowered(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    public float getMachineInputSpeed() {
        if(level == null) {
            return 0;
        }
        if (level.getBlockEntity(getBlockPos().below()) instanceof MachineInputBlockEntity input) {
            return input.getSpeed();
        }
        return 0;
    }

    public boolean hasMachineInput() {
        if(level == null) {
            return false;
        }
        return level.getBlockEntity(getBlockPos().below()) instanceof MachineInputBlockEntity;
    }
}
