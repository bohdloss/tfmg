package com.drmangotea.tfmg.content.electricity.storage;

import com.drmangotea.tfmg.content.electricity.base.ConnectNeightborsPacket;
import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import com.drmangotea.tfmg.registry.TFMGPackets;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collections;
import java.util.List;

public class AccumulatorBlock extends DirectionalBlock implements IBE<AccumulatorBlockEntity> {


    public AccumulatorBlock(Properties p_49795_) {
        super(p_49795_);

    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getClickedFace());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        withBlockEntityDo(level, pos, be -> be.setCapacity(stack));
    }

    @Override
    public List<ItemStack> getDrops(BlockState p_287732_, LootParams.Builder p_287596_) {
        return Collections.emptyList();
    }



    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if(!player.isCreative()&&level.getBlockEntity(pos) instanceof AccumulatorBlockEntity be) {
            ItemStack item = TFMGBlocks.ACCUMULATOR.asItem().getDefaultInstance();
            item.getOrCreateTag().putInt("Storage", be.energy.getEnergyStored());
            ItemEntity itemToSpawn = new ItemEntity((Level) level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, item);
            if (itemToSpawn.getItem().getCount() > 0)
                level.addFreshEntity(itemToSpawn);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public void onPlace(BlockState pState, Level level, BlockPos pos, BlockState pOldState, boolean pIsMoving) {
        withBlockEntityDo(level, pos, IElectric::onPlaced);
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);

    }

    @Override
    public Class<AccumulatorBlockEntity> getBlockEntityClass() {
        return AccumulatorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AccumulatorBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.ACCUMULATOR.get();
    }
}

