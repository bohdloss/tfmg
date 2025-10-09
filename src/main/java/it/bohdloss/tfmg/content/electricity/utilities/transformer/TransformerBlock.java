package it.bohdloss.tfmg.content.electricity.utilities.transformer;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import it.bohdloss.tfmg.content.electricity.base.ElectricBlock;
import it.bohdloss.tfmg.content.machinery.misc.winding_machine.WindingMachineBlockEntity;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber
public class TransformerBlock extends ElectricBlock implements IBE<TransformerBlockEntity> {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public TransformerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext collisionContext) {
        return TFMGShapes.TRANSFORMER.get(blockState.getValue(FACING));
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof TransformerBlockEntity transformerBlockEntity))
                return;

            ItemHelper.dropContents(world, pos, transformerBlockEntity.primaryCoil.getHandler());
            ItemHelper.dropContents(world, pos, transformerBlockEntity.secondaryCoil.getHandler());
            super.onRemove(state, world, pos, newState, isMoving);
            world.removeBlockEntity(pos);
        }
    }

    // Workaround for sneak-clicking the block with an item
    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if(!event.getHand().equals(InteractionHand.MAIN_HAND) || event.getItemStack().isEmpty()) {
            return;
        }

        Level level = event.getLevel();
        BlockPos pos = event.getHitVec().getBlockPos();
        Player player = event.getEntity();

        if(level.getBlockState(pos).getBlock() instanceof TransformerBlock &&
                level.getBlockEntity(pos) instanceof TransformerBlockEntity be
        ) {
            ItemStack itemInHand = event.getItemStack().copy();

            ItemStack tryInsertItem = be.secondaryCoil.getHandler().insertItem(0, itemInHand, true);
            if(tryInsertItem.getCount() < itemInHand.getCount() && player.isShiftKeyDown()) {
                ItemStack notInserted = be.secondaryCoil.getHandler().insertItem(0, itemInHand, false);

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
        if(level.getBlockEntity(pos) instanceof TransformerBlockEntity be) {
            ItemStack itemInHand = player.getItemInHand(hand).copy();

            ItemStack tryInsertPrimary = be.primaryCoil.getHandler().insertItem(0, itemInHand, true);
            if(tryInsertPrimary.getCount() < itemInHand.getCount() && !player.isShiftKeyDown()) {
                ItemStack notInserted = be.primaryCoil.getHandler().insertItem(0, itemInHand, false);
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
        if(level.getBlockEntity(pos) instanceof TransformerBlockEntity be) {
            InteractionHand emptyHand = InteractionHand.MAIN_HAND;
            if(!player.getItemInHand(emptyHand).isEmpty()) {
                return InteractionResult.PASS;
            }

            ItemStack item = be.secondaryCoil.getHandler().extractItem(0, 1, true);
            if(!item.isEmpty() && player.isShiftKeyDown()) {
                ItemStack extracted = be.secondaryCoil.getHandler().extractItem(0, 1, false);
                player.setItemInHand(emptyHand, extracted);
                IWrenchable.playRotateSound(level, pos);
                return InteractionResult.SUCCESS;
            }

            ItemStack spool = be.primaryCoil.getHandler().extractItem(0, 1, true);
            if(!spool.isEmpty() && !player.isShiftKeyDown()) {
                ItemStack extracted = be.primaryCoil.getHandler().extractItem(0, 1, false);
                player.setItemInHand(emptyHand, extracted);
                IWrenchable.playRotateSound(level, pos);
                return InteractionResult.SUCCESS;
            }
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public Class<TransformerBlockEntity> getBlockEntityClass() {
        return TransformerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TransformerBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.TRANSFORMER.get();
    }
}
