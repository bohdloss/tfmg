package it.bohdloss.tfmg.base.fluid;

import it.bohdloss.tfmg.registry.TFMGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import org.jetbrains.annotations.NotNull;

public class ConcreteFluid extends BaseFlowingFluid {
    protected ConcreteFluid(Properties properties) {
        super(properties);
    }
    @Override
    public boolean isSource(@NotNull FluidState fluidState) {
        return true;
    }
    @Override
    public int getAmount(@NotNull FluidState fluidState) {
        return 8;
    }
    @Override
    public void randomTick(Level level, @NotNull BlockPos pos, @NotNull FluidState fluidState, @NotNull RandomSource randomSource) {
        if(!(level.getBlockState(pos).getBlock() instanceof LiquidBlock))
            return;

        int random = randomSource.nextInt(7) ;
        if(random==2) {
            level.setBlock(pos, TFMGBlocks.CONCRETE.block().get().defaultBlockState(), 3);
        }
    }
    protected boolean isRandomlyTicking() {
        return true;
    }

    public static class Flowing extends ConcreteFluid {
        public Flowing(Properties properties) {
            super(properties);
        }

        protected void createFluidStateDefinition(StateDefinition.@NotNull Builder<Fluid, FluidState> fluidStateDefinition) {
            super.createFluidStateDefinition(fluidStateDefinition);
            fluidStateDefinition.add(LEVEL);
        }

        public int getAmount(@NotNull FluidState fluidState) {
            return fluidState.getValue(LEVEL);
        }

        public boolean isSource(@NotNull FluidState fluidState) {
            return false;
        }
    }

    public static class Source extends ConcreteFluid {
        public Source(Properties properties) {
            super(properties);
        }

        public int getAmount(@NotNull FluidState fluidState) {
            return 8;
        }

        public boolean isSource(@NotNull FluidState fluidState) {
            return true;
        }
    }
}
