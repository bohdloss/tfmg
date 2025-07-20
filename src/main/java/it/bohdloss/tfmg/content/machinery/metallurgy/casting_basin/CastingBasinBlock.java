package it.bohdloss.tfmg.content.machinery.metallurgy.casting_basin;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class CastingBasinBlock extends HorizontalDirectionalBlock implements IBE<CastingBasinBlockEntity>, IWrenchable {
    public static final MapCodec<CastingBasinBlock> CODEC = simpleCodec(CastingBasinBlock::new);

    public CastingBasinBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter level, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return TFMGShapes.CASTING_BASIN.get(blockState.getValue(FACING));
    }
    @Override
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof CastingBasinBlockEntity castingBasinBlockEntity))
                return;

            ItemHelper.dropContents(world, pos, castingBasinBlockEntity.item.getHandler());
            world.removeBlockEntity(pos);
        }
    }
    @Override
    public Class<CastingBasinBlockEntity> getBlockEntityClass() {
        return CastingBasinBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CastingBasinBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.CASTING_BASIN.get();
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
