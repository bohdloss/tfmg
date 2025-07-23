package it.bohdloss.tfmg.content.decoration.tanks.steel;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.mixin.fluid_handling.FluidTankBlockEntityAccessor;
import it.bohdloss.tfmg.mixin_interfaces.IOnRefreshCapability;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.List;

@EventBusSubscriber
public class SteelTankBlockEntity extends FluidTankBlockEntity implements IOnRefreshCapability {
    public DistillationData distillationData;

    public SteelTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        getDistillationData();
    }

    public DistillationData getDistillationData() {
        if(distillationData == null) {
            distillationData = new DistillationData();
        }
        return distillationData;
    }

    protected FluidTankBlockEntityAccessor access() {
        return (FluidTankBlockEntityAccessor) this;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        SteelTankBlockEntity controllerBE = (SteelTankBlockEntity) getControllerBE();
        if (controllerBE == null) {
            return false;
        }
        if (controllerBE.distillationData.addToGoggleTooltip(tooltip, isPlayerSneaking)) {
            return true;
        }
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                TFMGBlockEntities.STEEL_FLUID_TANK.get(),
                (be, context) -> {
                    if (be.fluidCapability == null)
                        be.access().tfmg$refreshCapability();
                    return be.fluidCapability;
                }
        );
    }

    @Override
    public void tick() {
        super.tick();

        if(isController()) {
            distillationData.tick(this);
        }
    }

    @Override
    public void removeController(boolean keepFluids) {
        if(!level.isClientSide) {
            distillationData.clear();
        }
        super.removeController(keepFluids);
    }

    @Override
    public void toggleWindows() {
        SteelTankBlockEntity controller = (SteelTankBlockEntity) getControllerBE();
        if(controller == null) {
            return;
        }
        if(controller.distillationData.isActive()) {
            return;
        }

        super.toggleWindows();
    }

    @Override
    public void updateBoilerTemperature() {
        SteelTankBlockEntity controller = (SteelTankBlockEntity) getControllerBE();
        if(controller == null) {
            return;
        }
        if(!controller.distillationData.isActive()) {
            super.updateBoilerTemperature(); // !! Distillation tower overrides boiler
            return;
        }
        controller.distillationData.needsHeatLevelUpdate = true;
    }

    @Override
    public void updateBoilerState() {
        if (!isController()) {
            return;
        }

        boolean wasDistillationTower = distillationData.isActive();
        distillationData.evaluate(this);

        if (wasDistillationTower != distillationData.isActive()) {
            if (distillationData.isActive()) {
                boiler.clear(); // !!
                setWindows(false);
            }

            for (int yOffset = 0; yOffset < height; yOffset++) {
                for (int xOffset = 0; xOffset < width; xOffset++) {
                    for (int zOffset = 0; zOffset < width; zOffset++) {
                        if (level.getBlockEntity(worldPosition.offset(xOffset, yOffset, zOffset)) instanceof SteelTankBlockEntity fbe) {
                            fbe.access().tfmg$refreshCapability();
                        }
                    }
                }
            }
        }

        // Because distillation behavior overrides boiler behavior
        if(!distillationData.isActive()) {
            super.updateBoilerState();
        }

        notifyUpdate();
    }

    @Override
    public boolean tfmg$onRefreshCapability() {
        SteelTankBlockEntity controller = isController() ? this : ((SteelTankBlockEntity) getControllerBE());
        if(controller == null) {
            fluidCapability = new FluidTank(0);
        } else if(controller.getDistillationData().isActive()) {
            fluidCapability = controller.distillationData.createHandler();
        } else {
            fluidCapability = controller.access().tfmg$handlerForCapability();
        }
        invalidateCapabilities();
        return true;
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);

        compound.put("DistillationTower", distillationData.write());
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        distillationData.read(compound.getCompound("DistillationTower"));
    }
}
