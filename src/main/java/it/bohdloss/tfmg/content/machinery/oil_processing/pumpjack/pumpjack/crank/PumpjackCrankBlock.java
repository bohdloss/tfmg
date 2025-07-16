package it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.crank;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;

public class PumpjackCrankBlock extends HorizontalDirectionalBlock implements IBE<PumpjackCrankBlockEntity>, IWrenchable {
    public static final MapCodec<PumpjackCrankBlock> CODEC = simpleCodec(PumpjackCrankBlock::new);

    public PumpjackCrankBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        return this.defaultBlockState().setValue(FACING, placeContext.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HorizontalDirectionalBlock.FACING);
    }

    @Override
    public Class<PumpjackCrankBlockEntity> getBlockEntityClass() {
        return PumpjackCrankBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PumpjackCrankBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.PUMPJACK_CRANK.get();
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
