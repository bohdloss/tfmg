package it.bohdloss.tfmg.content.electricity.storage;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.content.electricity.base.IElectricBlock;
import it.bohdloss.tfmg.content.items.AccumulatorStorage;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGBlocks;
import it.bohdloss.tfmg.registry.TFMGDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AccumulatorBlock extends DirectionalBlock implements IElectricBlock, IBE<AccumulatorBlockEntity> {
    public static final MapCodec<AccumulatorBlock> CODEC = simpleCodec(AccumulatorBlock::new);

    public AccumulatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected MapCodec<? extends AccumulatorBlock> codec() {
        return CODEC;
    }

    @Override
    public Class<AccumulatorBlockEntity> getBlockEntityClass() {
        return AccumulatorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AccumulatorBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.ACCUMULATOR.get();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (!(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof AccumulatorBlockEntity be)) {
            return InteractionResult.PASS;
        }
        if(!be.isController() || be.getHeight() != 1) {
            return InteractionResult.PASS;
        }

        return IElectricBlock.super.onWrenched(state, context);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if(!player.isCreative() && level.getBlockEntity(pos) instanceof AccumulatorBlockEntity be) {
            ItemStack item = TFMGBlocks.ACCUMULATOR.asItem().getDefaultInstance();
            item.set(TFMGDataComponents.ACCUMULATOR_STORAGE, new AccumulatorStorage(be.energy.getHandler().getEnergyStored()));
            ItemEntity itemToSpawn = new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, item);
            if (itemToSpawn.getItem().getCount() > 0)
                level.addFreshEntity(itemToSpawn);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        withBlockEntityDo(level, pos, be -> be.energy.getHandler().receiveEnergy(stack.getOrDefault(TFMGDataComponents.ACCUMULATOR_STORAGE, AccumulatorStorage.DEFAULT).value(), false));
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        do {
            if (oldState.getBlock() == state.getBlock()) {
                break;
            }
            if (isMoving) {
                break;
            }
            withBlockEntityDo(worldIn, pos, AccumulatorBlockEntity::updateConnectivity);
        } while(false);
        IElectricBlock.super.onPlace(state, worldIn, pos, oldState, isMoving);
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        do {
            if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
                BlockEntity be = level.getBlockEntity(pos);
                if (!(be instanceof AccumulatorBlockEntity accumulatorBlockEntity)) {
                    break;
                }

                level.removeBlockEntity(pos);
                ConnectivityHandler.splitMulti(accumulatorBlockEntity);
            }
        } while (false);
        IElectricBlock.super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void updateIndirectNeighbourShapes(@NotNull BlockState stateIn, @NotNull LevelAccessor worldIn, @NotNull BlockPos pos, int flags, int count) {
        IElectricBlock.super.updateIndirectNeighbourShapes(stateIn, worldIn, pos, flags, count);
    }
}
