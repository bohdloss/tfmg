package it.bohdloss.tfmg.content.machinery.oil_processing;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import it.bohdloss.tfmg.registry.TFMGFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class OilDepositBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    private SmartFluidTankBehaviour tank;

    public OilDepositBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public SmartFluidTankBehaviour getReservoir() {
        return tank;
    }

    public void generateOil() {
        // TODO mimic original oil deposit availability
        var handler = tank.getPrimaryHandler();
        int capacity = handler.getCapacity();
        var fluid = new FluidStack(TFMGFluids.CRUDE_OIL, level.getRandom().nextIntBetweenInclusive(capacity / 10, capacity));
        handler.setFluid(fluid);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        var tank = SmartFluidTankBehaviour.single(this, 1000000).forbidInsertion().allowExtraction().whenFluidUpdates(this::notifyUpdate);
        behaviours.add(tank);
        this.tank = tank;
    }

    // TODO For testing purposes only, remove in prod
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return containedFluidTooltip(tooltip, isPlayerSneaking, tank.getCapability());
    }
}
