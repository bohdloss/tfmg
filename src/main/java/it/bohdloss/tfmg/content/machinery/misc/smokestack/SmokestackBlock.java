package it.bohdloss.tfmg.content.machinery.misc.smokestack;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class SmokestackBlock extends Block implements IBE<SmokestackBlockEntity>, IWrenchable {
    public static final BooleanProperty TOP = BooleanProperty.create("top");

    public SmokestackBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                        @NotNull BlockState state1, boolean movedByPiston) {
        super.onPlace(state, level, pos, state1, movedByPiston);

        if(level.getBlockState(pos.above()).getBlock() == state.getBlock()) {
            level.setBlock(pos, defaultBlockState().setValue(TOP,false), 2);
        }

        if(level.getBlockState(pos.below()).getBlock() == state.getBlock()) {
            level.setBlock(pos.below(), defaultBlockState().setValue(TOP,false), 2);
        }
    }

    @Override
    public void destroy(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state) {
        super.destroy(level, pos, state);

        if(level.getBlockState(pos.below()).getBlock() == state.getBlock()) {
            level.setBlock(pos.below(), defaultBlockState().setValue(TOP,true), 2);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TOP);
    }

    @Override
    public Class<SmokestackBlockEntity> getBlockEntityClass() {
        return SmokestackBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SmokestackBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.SMOKESTACK.get();
    }
}
