package it.bohdloss.tfmg.content.machinery.oil_processing.distillation_tower.controller;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DistillationControllerBlock extends HorizontalDirectionalBlock implements IBE<DistillationControllerBlockEntity>, IWrenchable {
    public static final MapCodec<DistillationControllerBlock> CODEC = simpleCodec(DistillationControllerBlock::new);

    public DistillationControllerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection()
                        .getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public Class<DistillationControllerBlockEntity> getBlockEntityClass() {
        return DistillationControllerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DistillationControllerBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.STEEL_DISTILLATION_CONTROLLER.get();
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        FluidTankBlock.updateBoilerState(state, world, pos.relative(state.getValue(FACING).getOpposite()));
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean pIsMoving) {
        IBE.onRemove(state, world, pos, newState);
        FluidTankBlock.updateBoilerState(state, world, pos.relative(state.getValue(FACING).getOpposite()));
    }
}
