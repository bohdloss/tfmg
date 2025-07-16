package it.bohdloss.tfmg.content.machinery.oil_processing.distillation_tower;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import it.bohdloss.tfmg.content.decoration.concrete.SimpleConcreteloggedBlock;
import it.bohdloss.tfmg.registry.TFMGBlocks;
import it.bohdloss.tfmg.registry.TFMGShapes;
import it.bohdloss.tfmg.registry.TFMGTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class IndustrialPipeBlock extends SimpleConcreteloggedBlock implements IWrenchable {
    public IndustrialPipeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource randomSource) {
        tickDrying(level, state, TFMGBlocks.CONCRETE_ENCASED_INDUSTRIAL_PIPE.getDefaultState(), pos, randomSource);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return TFMGShapes.INDUSTRIAL_PIPE;
    }

    public static boolean isIndustrialPipe(BlockState blockState) {
        return TFMGTags.TFMGBlockTags.INDUSTRIAL_PIPE.matches(blockState.getBlock());
    }
}
