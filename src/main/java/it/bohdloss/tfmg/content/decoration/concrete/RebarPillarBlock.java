package it.bohdloss.tfmg.content.decoration.concrete;

import com.mojang.serialization.MapCodec;
import it.bohdloss.tfmg.registry.TFMGBlocks;
import it.bohdloss.tfmg.registry.TFMGShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class RebarPillarBlock extends DirectionalBlock implements ConcreteloggedBlock {
    public static final MapCodec<RebarPillarBlock> CODEC = simpleCodec(RebarPillarBlock::new);

    public RebarPillarBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.getStateDefinition().any().setValue(CONCRETELOGGED, false));
    }

    @Override
    protected @NotNull MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockState state) {
        return fluidState(state);
    }
    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pDirection,
                                           @NotNull BlockState pNeighborState, @NotNull LevelAccessor pLevel,
                                           @NotNull BlockPos pCurrentPos, @NotNull BlockPos pNeighborPos) {
        updateConcrete(pLevel, pState, pCurrentPos);
        return pState;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter level,
                                        @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return TFMGShapes.REBAR_PILLAR.get(blockState.getValue(FACING));
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state,
                                                       @NotNull Level level, @NotNull BlockPos pos,
                                                       @NotNull Player player, @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hitResult) {
        return onClicked(level, pos, level.getBlockState(pos), player, hand);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext pContext) {
        return withConcrete(super.getStateForPlacement(pContext), pContext).setValue(FACING, pContext.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos,
                           @NotNull RandomSource randomSource) {
        tickDrying(level, state, TFMGBlocks.REBAR_CONCRETE.block().getDefaultState(), pos, randomSource);
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState blockState) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONCRETELOGGED);
        builder.add(FACING);
    }
}
