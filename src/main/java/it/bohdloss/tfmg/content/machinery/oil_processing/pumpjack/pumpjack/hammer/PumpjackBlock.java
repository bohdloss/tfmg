package it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.hammer;

import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGBlocks;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class PumpjackBlock extends BearingBlock implements IBE<PumpjackBlockEntity>, IWrenchable {
    public static final BooleanProperty WIDE = BooleanProperty.create("wide");

    public PumpjackBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(WIDE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(WIDE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if(!context.getClickedFace().getAxis().isVertical()) {
            return InteractionResult.FAIL;
        }
        return super.onWrenched(state, context);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean wide = context.getLevel().getBlockState(context.getClickedPos().above()).is(TFMGBlocks.LARGE_PUMPJACK_HAMMER_PART.get());

        Direction preferredDirection = getPreferredHorizontalFacing(context);
        if (preferredDirection != null)
            return this.defaultBlockState().setValue(FACING, preferredDirection).setValue(WIDE,wide);
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(WIDE,wide);
    }

    public static Direction getPreferredHorizontalFacing(BlockPlaceContext context) {
        Direction prefferedSide = null;
        for (Direction side : Iterate.horizontalDirections) {
            BlockState blockState = context.getLevel().getBlockState(context.getClickedPos().relative(side));
            if (blockState.getBlock() instanceof IRotate) {
                if (((IRotate) blockState.getBlock()).hasShaftTowards(context.getLevel(), context.getClickedPos().relative(side),
                        blockState, side.getOpposite()))
                    if (prefferedSide != null && prefferedSide.getAxis() != side.getAxis()) {
                        prefferedSide = null;
                        break;
                    } else {
                        prefferedSide = side;
                    }
            }
        }
        return prefferedSide == null ? null : prefferedSide;
    }

    @Override
    public Class<PumpjackBlockEntity> getBlockEntityClass() {
        return PumpjackBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PumpjackBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.PUMPJACK_HAMMER.get();
    }
}
