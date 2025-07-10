package it.bohdloss.tfmg.content.machinery.misc.flarestack;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class FlarestackBlock extends Block implements IBE<FlarestackBlockEntity>, IWrenchable {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public FlarestackBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter worldIn,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return TFMGShapes.FLARESTACK;
    }
    @Nullable
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(LIT, Boolean.valueOf(false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public Class<FlarestackBlockEntity> getBlockEntityClass() {
        return FlarestackBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FlarestackBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.FLARESTACK.get();
    }
}
