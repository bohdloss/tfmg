package it.bohdloss.tfmg.content.decoration.concrete;

import it.bohdloss.tfmg.registry.TFMGBlocks;
import it.bohdloss.tfmg.registry.TFMGShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class RebarFloorBlock extends SimpleConcreteloggedBlock {
    public RebarFloorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter level,
                                        @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return TFMGShapes.REBAR_FLOOR;
    }

    @Override
    public BlockState driesInto() {
        return TFMGBlocks.REBAR_CONCRETE.block().getDefaultState();
    }
}
