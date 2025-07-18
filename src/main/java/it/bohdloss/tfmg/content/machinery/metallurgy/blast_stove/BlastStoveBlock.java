package it.bohdloss.tfmg.content.machinery.metallurgy.blast_stove;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public class BlastStoveBlock extends Block implements IBE<BlastStoveBlockEntity>, IWrenchable {
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

    public BlastStoveBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(SHAPE, Shape.BOTTOM));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHAPE);
    }

    public static boolean isBlastStove(BlockState state) {
        return state.getBlock() instanceof BlastStoveBlock;
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
        withBlockEntityDo(worldIn, pos, BlastStoveBlockEntity::updateConnectivity);
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof BlastStoveBlockEntity blastStoveBlockEntity)) {
                return;
            }

            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(blastStoveBlockEntity);
        }
    }

    @Override
    public Class<BlastStoveBlockEntity> getBlockEntityClass() {
        return BlastStoveBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BlastStoveBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.BLAST_STOVE.get();
    }

    public enum Shape implements StringRepresentable {
        BOTTOM("bottom"),
        TOP("top"),
        BOTTOM_CONNECTED("bottom_connected"),
        TOP_CONNECTED("top_connected");

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
