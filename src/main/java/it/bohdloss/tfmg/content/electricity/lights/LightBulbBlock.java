package it.bohdloss.tfmg.content.electricity.lights;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import it.bohdloss.tfmg.blocks.WallMountBlock;
import it.bohdloss.tfmg.content.electricity.base.ElectricBlock;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class LightBulbBlock extends ElectricBlock implements IBE<LightBulbBlockEntity>, IWrenchable {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final IntegerProperty LIGHT = BlockStateProperties.LEVEL;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public final BlockEntityEntry<? extends LightBulbBlockEntity> blockEntityType;
    public final VoxelShaper shape;

    public LightBulbBlock(Properties properties, BlockEntityEntry<? extends LightBulbBlockEntity> blockEntityType, VoxelShaper shape) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIGHT, 0).setValue(WATERLOGGED, false));
        this.blockEntityType = blockEntityType;
        this.shape = shape;
    }
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return shape.get(pState.getValue(FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(LIGHT, WATERLOGGED, FACING));
    }

    public @NotNull BlockState updateShape(@NotNull BlockState blockState, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
        if (blockState.getValue(WATERLOGGED)) {
            level.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(blockState, direction, neighborState, level, blockPos, neighborPos);
    }

    public FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockState blockstate = this.defaultBlockState();
        LevelReader levelreader = blockPlaceContext.getLevel();
        BlockPos blockpos = blockPlaceContext.getClickedPos();
        Direction[] adirection = blockPlaceContext.getNearestLookingDirections();
        for (Direction direction : adirection) {
            Direction direction1 = direction.getOpposite();
            blockstate = blockstate.setValue(FACING, direction1);
            if (blockstate.canSurvive(levelreader, blockpos)) {
                return blockstate;
            }
        }
        return null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand pHand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        ItemStack heldItem = player.getItemInHand(pHand);
        LightBulbBlockEntity be = getBlockEntity(level, pos);
        DyeColor dye = DyeColor.getColor(heldItem);
        if (be != null) {
            if (dye != null) {
                level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                be.setColor(dye);
                return ItemInteractionResult.SUCCESS;
            }
        }


        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public Class<LightBulbBlockEntity> getBlockEntityClass() {
        return LightBulbBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends LightBulbBlockEntity> getBlockEntityType() {
        return blockEntityType.get();
    }

    @Override
    public boolean hasConnectorTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING).getOpposite();
    }
}
