package it.bohdloss.tfmg.content.machinery.misc.concrete_hose;

import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import it.bohdloss.tfmg.DebugStuff;
import it.bohdloss.tfmg.content.decoration.concrete.ConcreteloggedBlock;
import it.bohdloss.tfmg.registry.TFMGFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ConcreteHoseFluidHandler implements IFluidHandler {

    @Override
    public int fill(FluidStack resource, @NotNull FluidAction action) {
        if(!resource.getFluid().isSame(TFMGFluids.LIQUID_CONCRETE.getSource()))
            return 0;
        if (!internalTank.isEmpty() && !resource.getFluid().isSame(internalTank.getFluid().getFluid()))
            return 0;
        if (resource.isEmpty() || !FluidHelper.hasBlockState(resource.getFluid()))
            return 0;

        int diff = resource.getAmount();
        int totalAmountAfterFill = diff + internalTank.getFluidAmount();
        FluidStack remaining = resource.copy();
        boolean deposited = false;

        if (predicate.get() && totalAmountAfterFill >= 1000) {
//            BlockState rootPosState = filler.blockEntity.getLevel().getBlockState(rootPosGetter.get());
//            if(rootPosState.hasProperty(ConcreteloggedBlock.CONCRETELOGGED)) {
//                DebugStuff.show("Currently in concrete loggable block");
//            }
//            DebugStuff.show();

            if (filler.tryDeposit(resource.getFluid(), rootPosGetter.get(), action.simulate())) {
                remaining.shrink(1000);
                diff -= 1000;
                deposited = true;
            }
        }

        if (action.simulate())
            return diff <= 0 ? resource.getAmount() : internalTank.fill(remaining, action);
        if (diff <= 0) {
            internalTank.drain(-diff, FluidAction.EXECUTE);
            return resource.getAmount();
        }

        return internalTank.fill(remaining, action) + (deposited ? 1000 : 0);
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return internalTank.getFluidInTank(tank);
    }

    @Override
    public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
        return FluidStack.EMPTY;
    }

    //

    private final SmartFluidTank internalTank;
    private final ConcreteFillingBehavior filler;
    private final Supplier<BlockPos> rootPosGetter;
    private final Supplier<Boolean> predicate;

    public ConcreteHoseFluidHandler(SmartFluidTank internalTank, ConcreteFillingBehavior filler, Supplier<BlockPos> rootPosGetter, Supplier<Boolean> predicate) {
        this.internalTank = internalTank;
        this.filler = filler;
        this.rootPosGetter = rootPosGetter;
        this.predicate = predicate;
    }

    @Override
    public int getTanks() {
        return internalTank.getTanks();
    }

    @Override
    public int getTankCapacity(int tank) {
        return internalTank.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return internalTank.isFluidValid(tank, stack);
    }
}
