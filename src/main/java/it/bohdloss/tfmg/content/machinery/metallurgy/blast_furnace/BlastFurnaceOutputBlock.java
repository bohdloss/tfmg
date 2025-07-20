package it.bohdloss.tfmg.content.machinery.metallurgy.blast_furnace;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import it.bohdloss.tfmg.content.machinery.metallurgy.coke_oven.CokeOvenBlockEntity;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlastFurnaceOutputBlock extends HorizontalDirectionalBlock implements IBE<BlastFurnaceOutputBlockEntity>, IWrenchable {
    public static final MapCodec<BlastFurnaceOutputBlock> CODEC = simpleCodec(BlastFurnaceOutputBlock::new);

    public BlastFurnaceOutputBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof BlastFurnaceOutputBlockEntity blastFurnaceOutputBlockEntity))
                return;

            ItemHelper.dropContents(world, pos, blastFurnaceOutputBlockEntity.inventory.getHandler());
            ItemHelper.dropContents(world, pos, blastFurnaceOutputBlockEntity.fuel.getHandler());
            ItemHelper.dropContents(world, pos, blastFurnaceOutputBlockEntity.flux.getHandler());
            world.removeBlockEntity(pos);
        }
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public Class<BlastFurnaceOutputBlockEntity> getBlockEntityClass() {
        return BlastFurnaceOutputBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BlastFurnaceOutputBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.BLAST_FURNACE_OUTPUT.get();
    }
}
