package it.bohdloss.tfmg.content.electricity.connection.diagonal;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.content.electricity.base.ElectricBlock;
import it.bohdloss.tfmg.content.electricity.base.ElectricBlockEntity;
import it.bohdloss.tfmg.content.electricity.base.IElectricBlock;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DiagonalCableBlock extends ElectricBlock implements SimpleWaterloggedBlock, IBE<ElectricBlockEntity>, IElectricBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING_PRIMARY = DirectionProperty.create("facing_primary");
    public static final DirectionProperty FACING_SECONDARY = DirectionProperty.create("facing_secondary");

    public DiagonalCableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, Boolean.FALSE)
                .setValue(FACING_PRIMARY, Direction.NORTH)
                .setValue(FACING_SECONDARY, Direction.DOWN));
    }

    @Override
    public Class<ElectricBlockEntity> getBlockEntityClass() {
        return ElectricBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ElectricBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.DIAGONAL_CABLE_BLOCK.get();
    }

    @Override
    public boolean hasConnectorTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING_PRIMARY) || face == state.getValue(FACING_SECONDARY);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, FACING_PRIMARY, FACING_SECONDARY);
    }

    @Override
    protected @NotNull FluidState getFluidState(@NotNull BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction primary = state.getValue(FACING_PRIMARY);
        Direction secondary = state.getValue(FACING_SECONDARY);

        // Determine which axis is vertical (if any)
        boolean primaryIsVertical = primary.getAxis() == Direction.Axis.Y;
        boolean secondaryIsVertical = secondary.getAxis() == Direction.Axis.Y;

        if (primaryIsVertical || secondaryIsVertical) {
            // Vertical cable case (UP/DOWN + horizontal)
            Direction vertical = primaryIsVertical ? primary : secondary;
            Direction horizontal = primaryIsVertical ? secondary : primary;

            return vertical == Direction.UP
                    ? TFMGShapes.DIAGONAL_CABLE_BLOCK_UP.get(horizontal)
                    : TFMGShapes.DIAGONAL_CABLE_BLOCK_DOWN.get(horizontal);
        } else {
            // Horizontal cable case (two horizontal directions)
            boolean isClockwise = isClockwisePair(primary, secondary);
            Direction shapeDirection = isClockwise ? primary : secondary;
            return TFMGShapes.DIAGONAL_CABLE_BLOCK_HORIZONTAL.get(shapeDirection);
        }
    }

    private boolean isClockwisePair(Direction a, Direction b) {
        // Check if the pair follows the clockwise NE, ES, SW, WN sequence
        return (a == Direction.NORTH && b == Direction.EAST) ||
                (a == Direction.EAST && b == Direction.SOUTH) ||
                (a == Direction.SOUTH && b == Direction.WEST) ||
                (a == Direction.WEST && b == Direction.NORTH);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        InteractionResult result = super.onWrenched(state, context);
        if (!result.consumesAction()) {
            return result;
        }

        Direction clickedFace = context.getClickedFace();
        Direction primary = state.getValue(FACING_PRIMARY);
        Direction secondary = state.getValue(FACING_SECONDARY);

        BlockState newState = calculateRotatedState(state, clickedFace, primary, secondary);

        ElectricBlockEntity.switchToBlockState(context.getLevel(), context.getClickedPos(), newState);
        IWrenchable.playRotateSound(context.getLevel(), context.getClickedPos());

        return result;
    }

    private BlockState calculateRotatedState(BlockState currentState, Direction clickedFace,
                                             Direction primary, Direction secondary) {
        // Flip primary if clicking it
        if (clickedFace == primary) {
            return currentState.setValue(FACING_PRIMARY, clickedFace.getOpposite());
        }

        // Flip secondary if clicking it
        if (clickedFace == secondary) {
            return currentState.setValue(FACING_SECONDARY, clickedFace.getOpposite());
        }

        // Rotate secondary around primary axis
        if (clickedFace == primary.getOpposite()) {
            return currentState.setValue(FACING_SECONDARY, rotateAroundAxis(secondary, primary.getAxis()));
        }

        // Rotate primary around secondary axis
        if (clickedFace == secondary.getOpposite()) {
            return currentState.setValue(FACING_PRIMARY, rotateAroundAxis(primary, secondary.getAxis()));
        }

        // Rotate both around clicked axis
        return currentState
                .setValue(FACING_PRIMARY, rotateAroundAxis(primary, clickedFace.getAxis()))
                .setValue(FACING_SECONDARY, rotateAroundAxis(secondary, clickedFace.getAxis()));
    }

    private Direction rotateAroundAxis(Direction direction, Direction.Axis axis) {
        // Skip rotation if already aligned with the axis
        if (direction.getAxis() == axis) {
            return direction;
        }
        return direction.getClockWise(axis);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;
        Direction facing = Objects.requireNonNull(context.getPlayer()).getDirection();
        Direction clickedFace = context.getClickedFace();

        if (context.getPlayer() != null) {
            if (clickedFace.getAxis() == Direction.Axis.Y)
                return defaultBlockState()
                        .setValue(FACING_PRIMARY, clickedFace.getOpposite())
                        .setValue(FACING_SECONDARY, facing)
                        .setValue(WATERLOGGED, flag);
            else {
                return defaultBlockState()
                        .setValue(FACING_PRIMARY, facing)
                        .setValue(FACING_SECONDARY, facing.getClockWise(Direction.Axis.Y))
                        .setValue(WATERLOGGED, flag);
            }
        }
        return defaultBlockState().setValue(FACING_PRIMARY, Direction.UP).setValue(FACING_SECONDARY,facing).setValue(WATERLOGGED, flag);
    }

    @Override
    protected @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }
}
