package it.bohdloss.tfmg.content.electricity.connection.tube;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.placement.PoleHelper;
import it.bohdloss.tfmg.content.decoration.concrete.ConcreteloggedBlock;
import it.bohdloss.tfmg.content.electricity.base.ElectricBlockEntity;
import it.bohdloss.tfmg.content.electricity.base.IElectricBlock;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGBlocks;
import it.bohdloss.tfmg.registry.TFMGShapes;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class CableTubeBlock extends RotatedPillarBlock implements IElectricBlock, IBE<ElectricBlockEntity>, ConcreteloggedBlock {
    public final int placementHelperId = PlacementHelpers.register(new PlacementHelper());
    public final boolean concreteEncased;
    public final Supplier<Block> nonEncased;

    public CableTubeBlock(Properties properties, boolean concreteEncased, Supplier<Block> nonEncased) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CONCRETELOGGED, false).setValue(AXIS, Direction.Axis.Y));
        this.concreteEncased = concreteEncased;
        this.nonEncased = nonEncased;
    }

    @Override
    protected @NotNull FluidState getFluidState(@NotNull BlockState state) {
        return fluidState(state);
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource randomSource) {
        if (state.is(TFMGBlocks.CABLE_TUBE.get())) {
            tickDrying(level, state, TFMGBlocks.CONCRETE_ENCASED_CABLE_TUBE.getDefaultState().setValue(AXIS, state.getValue(AXIS)), pos, randomSource);
        }

        if (state.is(TFMGBlocks.ELECTRIC_POST.get())) {
            tickDrying(level, state, TFMGBlocks.CONCRETE_ENCASED_ELECTRIC_POST.getDefaultState().setValue(AXIS, state.getValue(AXIS)), pos, randomSource);
        }
    }

    @Override
    protected boolean isRandomlyTicking(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONCRETELOGGED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        IElectricBlock.super.onPlace(state, worldIn, pos, oldState, isMoving);
    }

    @Override
    public void onRemove(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pNewState, boolean pIsMoving) {
        IElectricBlock.super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public void updateIndirectNeighbourShapes(@NotNull BlockState stateIn, @NotNull LevelAccessor worldIn, @NotNull BlockPos pos, int flags, int count) {
        IElectricBlock.super.updateIndirectNeighbourShapes(stateIn, worldIn, pos, flags, count);
    }

    @Override
    public boolean hasConnectorTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(AXIS);
    }

    @Override
    public Class<ElectricBlockEntity> getBlockEntityClass() {
        return ElectricBlockEntity.class;
    }

    @Override
    protected @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if(!concreteEncased) {
            updateConcrete(level, state, pos);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public @NotNull BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockState state = this.defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
        return concreteEncased ? state : withConcrete(state, context);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return concreteEncased ? super.getShape(state, level, pos, context) : TFMGShapes.CABLE_TUBE.get(state.getValue(AXIS));
    }

    @Override
    public BlockEntityType<? extends ElectricBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.CABLE_TUBE.get();
    }

    private CableTubeBlock getThis() {
        return this;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state,
                                                       @NotNull Level level, @NotNull BlockPos pos,
                                                       @NotNull Player player, @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hitResult) {
        ItemStack itemInHand = player.getItemInHand(hand);

        IPlacementHelper helper = PlacementHelpers.get(placementHelperId);
        if (helper.matchesItem(itemInHand)) {
            return helper.getOffset(player, level, state, pos, hitResult)
                    .placeInWorld(level, (BlockItem) itemInHand.getItem(), player, hand, hitResult);
        }


        return concreteEncased ? ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION : onClicked(level, pos, state, player, hand);
    }

    @MethodsReturnNonnullByDefault
    private class PlacementHelper extends PoleHelper<Direction.Axis> {
        private PlacementHelper() {
            super(state -> state.getBlock() == getThis(), state -> state.getValue(AXIS), AXIS);
        }

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return i -> i.getItem() instanceof BlockItem
                    && ((BlockItem) i.getItem()).getBlock() == (concreteEncased ? null : getThis());
        }

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos,
                                         BlockHitResult ray) {
            PlacementOffset offset = super.getOffset(player, world, state, pos, ray);
            if (offset.isSuccessful())
                offset.withTransform(offset.getTransform()
                        .andThen(s -> concreteEncased ? nonEncased.get().defaultBlockState() : s)
                        .andThen(s -> state.setValue(AXIS, state.getValue(AXIS))));
            return offset;
        }

    }
}
