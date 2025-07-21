package it.bohdloss.tfmg.content.machinery.misc.winding_machine;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class WindingMachineBlock extends HorizontalKineticBlock implements IBE<WindingMachineBlockEntity>, IWrenchable {
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public WindingMachineBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = getPreferredHorizontalFacing(context);
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            BlockState placement = super.getStateForPlacement(context);
            if(placement == null) {
                return null;
            }
            return placement.setValue(HORIZONTAL_FACING, placement.getValue(HORIZONTAL_FACING).getClockWise());
        } else if(preferred == null) {
            BlockState placement = super.getStateForPlacement(context);
            if(placement == null) {
                return null;
            }
            return placement.setValue(HORIZONTAL_FACING, placement.getValue(HORIZONTAL_FACING).getCounterClockWise());
        }
        return defaultBlockState().setValue(HORIZONTAL_FACING, preferred.getClockWise());
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof WindingMachineBlockEntity windingMachineBlockEntity))
                return;

            ItemHelper.dropContents(world, pos, windingMachineBlockEntity.item.getHandler());
            ItemHelper.dropContents(world, pos, windingMachineBlockEntity.spool.getHandler());
            world.removeBlockEntity(pos);
        }
    }

    @Override
    protected boolean isSignalSource(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return TFMGShapes.WINDING_MACHINE.get(state.getValue(HORIZONTAL_FACING));
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getCounterClockWise().getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(HORIZONTAL_FACING).getCounterClockWise();
    }

    @Override
    public Class<WindingMachineBlockEntity> getBlockEntityClass() {
        return WindingMachineBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends WindingMachineBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.WINDING_MACHINE.get();
    }

    // Workaround for sneak-clicking the block with an item
    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if(!event.getHand().equals(InteractionHand.MAIN_HAND)) {
            return;
        }

        Level level = event.getLevel();
        BlockPos pos = event.getHitVec().getBlockPos();
        Player player = event.getEntity();

        if(level.getBlockState(pos).getBlock() instanceof WindingMachineBlock &&
            level.getBlockEntity(pos) instanceof WindingMachineBlockEntity be
        ) {
            ItemStack itemInHand = event.getItemStack().copy();

            ItemStack tryInsertItem = be.item.getHandler().insertItem(0, itemInHand, true);
            if(tryInsertItem.getCount() < itemInHand.getCount() && player.isShiftKeyDown()) {
                ItemStack notInserted = be.item.getHandler().insertItem(0, itemInHand, false);

                player.setItemInHand(event.getHand(), notInserted.copy());
                IWrenchable.playRotateSound(level, pos);

                // At this point we cancel the event
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(
            @NotNull ItemStack stack,
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult hitResult
    ) {
        if(!hand.equals(InteractionHand.MAIN_HAND)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if(level.getBlockEntity(pos) instanceof WindingMachineBlockEntity be) {
            ItemStack itemInHand = player.getItemInHand(hand).copy();

            ItemStack tryInsertSpool = be.spool.getHandler().insertItem(0, itemInHand, true);
            if(tryInsertSpool.getCount() < itemInHand.getCount() && !player.isShiftKeyDown()) {
                ItemStack notInserted = be.spool.getHandler().insertItem(0, itemInHand, false);
                player.setItemInHand(hand, notInserted.copy());
                IWrenchable.playRotateSound(level, pos);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull BlockHitResult hitResult
    ) {
        if(level.getBlockEntity(pos) instanceof WindingMachineBlockEntity be) {
            InteractionHand emptyHand = InteractionHand.MAIN_HAND;
            if(!player.getItemInHand(emptyHand).isEmpty()) {
                return InteractionResult.PASS;
            }

            ItemStack item = be.item.getHandler().extractItem(0, 1, true);
            if(!item.isEmpty() && player.isShiftKeyDown()) {
                ItemStack extracted = be.item.getHandler().extractItem(0, 1, false);
                player.setItemInHand(emptyHand, extracted);
                IWrenchable.playRotateSound(level, pos);
                return InteractionResult.SUCCESS;
            }

            ItemStack spool = be.spool.getHandler().extractItem(0, 1, true);
            if(!spool.isEmpty() && !player.isShiftKeyDown()) {
                ItemStack extracted = be.spool.getHandler().extractItem(0, 1, false);
                player.setItemInHand(emptyHand, extracted);
                IWrenchable.playRotateSound(level, pos);
                return InteractionResult.SUCCESS;
            }
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
}
