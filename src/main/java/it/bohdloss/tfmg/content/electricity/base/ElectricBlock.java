package it.bohdloss.tfmg.content.electricity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class ElectricBlock extends Block implements IElectricBlock {
    public ElectricBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void updateIndirectNeighbourShapes(@NotNull BlockState stateIn, @NotNull LevelAccessor worldIn, @NotNull BlockPos pos, int flags, int count) {
        IElectricBlock.super.updateIndirectNeighbourShapes(stateIn, worldIn, pos, flags, count);
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        IElectricBlock.super.onPlace(state, worldIn, pos, oldState, isMoving);
    }

    @Override
    public void onRemove(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pNewState, boolean pIsMoving) {
        IElectricBlock.super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
}
