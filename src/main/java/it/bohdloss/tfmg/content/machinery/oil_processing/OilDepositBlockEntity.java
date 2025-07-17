package it.bohdloss.tfmg.content.machinery.oil_processing;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import it.bohdloss.tfmg.config.TFMGConfigs;
import it.bohdloss.tfmg.registry.TFMGFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class OilDepositBlockEntity extends SmartBlockEntity implements IDeposit {
    private OilDepositTank tank;

    public OilDepositBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        tank = new OilDepositTank(this, TFMGConfigs.common().worldgen.depositMaxReserves.get(), fluidStack -> fluidStack.getFluid().isSame(TFMGFluids.CRUDE_OIL.getSource()));
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    public IFluidHandler getReservoir() {
        return tank;
    }

    public void generateOil() {
        var maxReserves = TFMGConfigs.common().worldgen.depositMaxReserves;
        int oil = level.getRandom().nextIntBetweenInclusive(1000, maxReserves.get());
        var fluid = new FluidStack(TFMGFluids.CRUDE_OIL, oil);
        tank.setFluid(fluid);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        tank.writeToNBT(registries, tag);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        tank.readFromNBT(registries, tag);
    }

    protected static class OilDepositTank extends FluidTank {
        OilDepositBlockEntity owner;

        public OilDepositTank(OilDepositBlockEntity owner, int capacity, Predicate<FluidStack> validator) {
            super(capacity, validator);
            this.owner = owner;
        }

        @Override
        public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
            return 0;
        }

        @Override
        public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
            if(resource.getFluid().isSame(TFMGFluids.CRUDE_OIL.getSource())) {
                return drain(resource.getAmount(), action);
            } else {
                return FluidStack.EMPTY;
            }
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
            if(maxDrain == 0) {
                return FluidStack.EMPTY;
            }

            if(TFMGConfigs.common().worldgen.infiniteDeposits.get()) {
                return new FluidStack(TFMGFluids.CRUDE_OIL, maxDrain);
            }

            var randomSource = owner.level.getRandom();
            if (randomSource.nextInt(((900000) / maxDrain) + 1) == 0) {
                super.drain(1, action);
            }

            return new FluidStack(TFMGFluids.CRUDE_OIL, maxDrain);
        }

        @Override
        protected void onContentsChanged() {
            owner.notifyUpdate();
        }
    }
}
