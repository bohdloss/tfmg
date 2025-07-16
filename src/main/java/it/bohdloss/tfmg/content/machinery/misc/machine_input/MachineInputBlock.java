package it.bohdloss.tfmg.content.machinery.misc.machine_input;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jetbrains.annotations.NotNull;

public class MachineInputBlock extends DirectionalKineticBlock implements IBE<MachineInputBlockEntity>, IWrenchable {
    public MachineInputBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }


    @Override
    protected boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public Class<MachineInputBlockEntity> getBlockEntityClass() {
        return MachineInputBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MachineInputBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.MACHINE_INPUT.get();
    }
}
