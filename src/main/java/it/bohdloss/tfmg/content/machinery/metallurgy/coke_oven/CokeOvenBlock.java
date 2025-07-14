package it.bohdloss.tfmg.content.machinery.metallurgy.coke_oven;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import it.bohdloss.tfmg.content.machinery.misc.air_intake.AirIntakeBlockEntity;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public class CokeOvenBlock extends HorizontalDirectionalBlock implements IBE<CokeOvenBlockEntity>, IWrenchable {
    public static final MapCodec<CokeOvenBlock> CODEC = simpleCodec(CokeOvenBlock::new);
    public static final EnumProperty<ControllerType> CONTROLLER_TYPE = EnumProperty.create("controller_type", ControllerType.class);

    public CokeOvenBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(CONTROLLER_TYPE, ControllerType.CASUAL));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
        builder.add(CONTROLLER_TYPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
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
            return IWrenchable.super.onWrenched(state, context);
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
        if(oldState.getBlock() == state.getBlock()) {
            return;
        }
        if(isMoving) {
            return;
        }
        withBlockEntityDo(worldIn, pos, CokeOvenBlockEntity::updateConnectivity);
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof CokeOvenBlockEntity cokeOvenBlockEntity))
                return;

            ItemHelper.dropContents(world, pos, cokeOvenBlockEntity.inventory.getHandler());
            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(cokeOvenBlockEntity);
        }
    }

    @Override
    public Class<CokeOvenBlockEntity> getBlockEntityClass() {
        return CokeOvenBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CokeOvenBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.COKE_OVEN.get();
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public enum ControllerType implements StringRepresentable {

        CASUAL("casual"),
        TOP_ON("top_on"),
        MIDDLE_ON("middle_on"),
        BOTTOM_ON("bottom_on");

        private final String name;

        ControllerType(String name) {
            this.name = name;
        }


        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
