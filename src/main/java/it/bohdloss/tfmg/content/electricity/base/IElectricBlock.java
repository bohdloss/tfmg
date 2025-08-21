package it.bohdloss.tfmg.content.electricity.base;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public interface IElectricBlock extends IWrenchable {
    @Override
    default InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState rotated = getRotatedBlockState(state, context.getClickedFace());
        if (!rotated.canSurvive(level, context.getClickedPos())) {
            return InteractionResult.PASS;
        }

        if(this instanceof IRotate) {
            KineticBlockEntity.switchToBlockState(level, pos, updateAfterWrenched(rotated, context));
            level.setBlock(pos, state, 0); // Reset it so ElectricBlockEntity can do its deed
        }

        ElectricData.switchToBlockState(level, pos, updateAfterWrenched(rotated, context));

        if (level.getBlockState(pos) != state) {
            IWrenchable.playRotateSound(level, pos);
        }

        return InteractionResult.SUCCESS;
    }

    default boolean areStatesElectricallyEquivalent(BlockState oldState, BlockState newState) {
        return false;
    }

    default void onPlace(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof IElectric electricBlockEntity) {
            ElectricData data = electricBlockEntity.getElectricData();
            data.preventConnection = false;

            if (oldState.getBlock() != state.getBlock())
                return;
            if (state.hasBlockEntity() != oldState.hasBlockEntity())
                return;
            if (!areStatesElectricallyEquivalent(oldState, state))
                return;

            data.preventConnection = true;
        }
    }

    default void onRemove(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pNewState, boolean pIsMoving) {
        IBE.onRemove(pState, pLevel, pPos, pNewState);
    }

    default void updateIndirectNeighbourShapes(@NotNull BlockState stateIn, @NotNull LevelAccessor worldIn, @NotNull BlockPos pos, int flags, int count) {
        if (worldIn.isClientSide()) {
            return;
        }

        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (!(blockEntity instanceof IElectric ebe)) {
            return;
        }

        // Remove previous information when block is added
        ElectricData data = ebe.getElectricData();
        if(data.preventConnection) {
            return;
        }

        data.wasMoved = true;
        data.clear();
        data.connectNextTick = true;
    }

    default void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        AdvancementBehaviour.setPlacedBy(worldIn, pos, placer);
        if (worldIn.isClientSide)
            return;

        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof IElectric ebe) {
            ElectricData data = ebe.getElectricData();
            data.effects.queuePowerIndicators();
        }
    }

    default float getElectricParticleTargetRadius() {
        return .65f;
    }

    default float getElectricParticleInitialRadius() {
        return .75f;
    }
}
