package it.bohdloss.tfmg.content.machinery.misc.firebox;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public class FireboxBlock extends Block implements IBE<FireboxBlockEntity>, IWrenchable {
    public static final EnumProperty<BlazeBurnerBlock.HeatLevel> HEAT_LEVEL = BlazeBurnerBlock.HEAT_LEVEL;
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

    public FireboxBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.NONE)
                .setValue(SHAPE, Shape.SINGLE)
        );
    }

    public static boolean isFirebox(BlockState state) {
        return state.getBlock() instanceof FireboxBlock;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HEAT_LEVEL);
        builder.add(SHAPE);
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
        withBlockEntityDo(worldIn, pos, FireboxBlockEntity::updateConnectivity);
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof FireboxBlockEntity fireboxBlockEntity)) {
                return;
            }

            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(fireboxBlockEntity);
        }
    }

    @Override
    public int getLightEmission(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 0;
    }

    @Override
    public Class<FireboxBlockEntity> getBlockEntityClass() {
        return FireboxBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FireboxBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.FIREBOX.get();
    }

    public enum Shape implements StringRepresentable {
        SINGLE("single"),
        CORNER("corner"),
        CENTER("center"),
        SIDE("side");

        private final String name;

        Shape(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
