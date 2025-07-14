package it.bohdloss.tfmg.content.machinery.oil_processing;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import it.bohdloss.tfmg.DebugStuff;
import it.bohdloss.tfmg.base.TFMGFluidBehavior;
import it.bohdloss.tfmg.registry.TFMGFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

public class OilDepositBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    private TFMGFluidBehavior tank;

    public OilDepositBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public IFluidHandler getReservoir() {
        return tank.getCapability();
    }

    public void generateOil() {
        // TODO mimic original oil deposit availability
        var handler = tank.getHandler();
        int capacity = handler.getCapacity();
        var fluid = new FluidStack(TFMGFluids.CRUDE_OIL, level.getRandom().nextIntBetweenInclusive(capacity / 10, capacity));
        handler.setFluid(fluid);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = new TFMGFluidBehavior(TFMGFluidBehavior.TYPE, "Fluid", this, 1000000)
                .withValidator(fluidStack -> fluidStack.getFluid().isSame(TFMGFluids.CRUDE_OIL.getSource()))
                .allowInsertion(false)
                .allowExtraction(false)
                .withCallback(this::notifyUpdate);
        behaviours.add(tank);
    }

    // TODO For testing purposes only, remove in prod
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return containedFluidTooltip(tooltip, isPlayerSneaking, tank.getCapability());
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
    }
}
