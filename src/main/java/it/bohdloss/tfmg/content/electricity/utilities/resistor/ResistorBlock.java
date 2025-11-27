package it.bohdloss.tfmg.content.electricity.utilities.resistor;

import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.blocks.WallMountBlock;
import it.bohdloss.tfmg.content.electricity.base.IElectricBlock;
import it.bohdloss.tfmg.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.stream;

public class ResistorBlock extends WallMountBlock implements IElectricBlock, IBE<ResistorBlockEntity> {
    public ResistorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<ResistorBlockEntity> getBlockEntityClass() {
        return ResistorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ResistorBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.RESISTOR.get();
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return Collections.emptyList();
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return TFMGShapes.RESISTOR.get(blockState.getValue(FACING));
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if(!player.isCreative() && level.getBlockEntity(pos) instanceof ResistorBlockEntity be) {
            ItemStack item = TFMGBlocks.RESISTOR.asItem().getDefaultInstance();
            item.set(TFMGDataComponents.RESISTANCE, new Resistance(be.resistance));
            ItemEntity itemToSpawn = new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, item);
            if (itemToSpawn.getItem().getCount() > 0)
                level.addFreshEntity(itemToSpawn);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        withBlockEntityDo(level, pos, be -> be.resistance = stack.getOrDefault(TFMGDataComponents.RESISTANCE, Resistance.DEFAULT).value());
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
}
