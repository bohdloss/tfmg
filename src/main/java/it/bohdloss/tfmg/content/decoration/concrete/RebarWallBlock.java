package it.bohdloss.tfmg.content.decoration.concrete;

import it.bohdloss.tfmg.registry.TFMGBlocks;
import it.bohdloss.tfmg.registry.TFMGShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class RebarWallBlock extends SimpleConcreteloggedBlock {
    public RebarWallBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                           @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return TFMGShapes.CABLE_TUBE.get(Direction.UP);
    }

    @Override
    public BlockState driesInto() {
        return TFMGBlocks.REBAR_CONCRETE.wall().getDefaultState();
    }
}
