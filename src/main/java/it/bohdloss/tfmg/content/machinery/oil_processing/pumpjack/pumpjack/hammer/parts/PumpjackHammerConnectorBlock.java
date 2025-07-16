package it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.hammer.parts;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import it.bohdloss.tfmg.registry.TFMGShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class PumpjackHammerConnectorBlock extends HorizontalDirectionalBlock implements IWrenchable {
    public static final MapCodec<PumpjackHammerConnectorBlock> CODEC = simpleCodec(PumpjackHammerConnectorBlock::new);

    public PumpjackHammerConnectorBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState pState, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {

        return TFMGShapes.PUMPJACK_HAMMER_PART.get(pState.getValue(FACING).getClockWise());
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }
}
