package it.bohdloss.tfmg.content.electricity.base;

import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class ElectricKineticBlock extends KineticBlock implements IElectricBlock {
    public ElectricKineticBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        // Kinetic handling
        if (blockEntity instanceof KineticBlockEntity kineticBlockEntity) {
            kineticBlockEntity.preventSpeedUpdate = 0;

            if (oldState.getBlock() != state.getBlock())
                return;
            if (state.hasBlockEntity() != oldState.hasBlockEntity())
                return;
            if (!areStatesKineticallyEquivalent(oldState, state))
                return;

            kineticBlockEntity.preventSpeedUpdate = 2;
        }
        // Electric handling
        IElectricBlock.super.onPlace(state, worldIn, pos, oldState, isMoving);
    }

    @Override
    public void updateIndirectNeighbourShapes(@NotNull BlockState stateIn, @NotNull LevelAccessor worldIn, @NotNull BlockPos pos, int flags, int count) {
        if (worldIn.isClientSide()) {
            return;
        }
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        // Kinetic
        if (blockEntity instanceof IElectric ebe) {
            if (ebe.getPreventVoltageUpdate() > 0) {
                return;
            }

            ebe.warnOfMovementElectrical();
            ebe.clearElectricInformation();
            ebe.setNeedsVoltageUpdate(true);
        }

        // Electric
        IElectricBlock.super.updateIndirectNeighbourShapes(stateIn, worldIn, pos, flags, count);
    }
}
