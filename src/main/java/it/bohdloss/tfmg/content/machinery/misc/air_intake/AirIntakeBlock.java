package it.bohdloss.tfmg.content.machinery.misc.air_intake;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class AirIntakeBlock extends DirectionalKineticBlock implements IBE<AirIntakeBlockEntity>, IWrenchable {
    public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");

    public AirIntakeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(INVISIBLE, false));
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        AirIntakeBlockEntity be = (AirIntakeBlockEntity) world.getBlockEntity(pos);

        if(be != null && be.hasShaft) {
            return face == state.getValue(FACING).getOpposite();
        } else {
            return false;
        }
    }

    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Direction direction = context.getClickedFace();
        Level level = context.getLevel();
        AirIntakeBlockEntity be = (AirIntakeBlockEntity) level.getBlockEntity(context.getClickedPos());
        if(be == null) {
            return InteractionResult.PASS;
        }

        if(direction == state.getValue(FACING).getOpposite()) {
            be.hasShaft = !be.hasShaft;
            IWrenchable.playRotateSound(level, context.getClickedPos());
            if(!be.hasShaft) {
                if (be.hasNetwork()) {
                    be.getOrCreateNetwork().remove(be);
                }
                be.detachKinetics();
                be.removeSource();
                be.setSpeed(0);
                be.remove();
                be.notifyUpdate();
            } else {
                be.attachKinetics();
                be.notifyUpdate();
            }
            return InteractionResult.SUCCESS;
        }

        if(be.isController() && be.getWidth() == 1) {
            return super.onWrenched(state, context);
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
        if(oldState.getBlock() == state.getBlock()) {
            return;
        }
        if(isMoving) {
            return;
        }
        withBlockEntityDo(worldIn, pos, AirIntakeBlockEntity::updateConnectivity);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof AirIntakeBlockEntity airIntakeBlockEntity))
                return;

            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(airIntakeBlockEntity);
        }
    }

    @Override
    public Class<AirIntakeBlockEntity> getBlockEntityClass() {
        return AirIntakeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AirIntakeBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.AIR_INTAKE.get();
    }
    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, INVISIBLE);
    }
}
