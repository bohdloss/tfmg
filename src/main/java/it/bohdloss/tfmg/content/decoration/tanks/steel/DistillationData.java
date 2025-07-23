package it.bohdloss.tfmg.content.decoration.tanks.steel;

import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import it.bohdloss.tfmg.content.machinery.oil_processing.distillation_tower.controller.DistillationControllerBlock;
import it.bohdloss.tfmg.content.machinery.oil_processing.distillation_tower.controller.DistillationControllerBlockEntity;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// Parallel of BoilerData made specifically for the distillation tower
public class DistillationData {
    protected SteelTankBlockEntity lastKnownController;

    // Heat
    public boolean needsHeatLevelUpdate;
    public int activeHeat;

    // Distillation controllers
    public List<BlockPos> attachedControllers = new ArrayList<>();

    // Client data
    public float gaugeRotation;
    public LerpedFloat visualGaugeRotation = LerpedFloat.angular();

    public void tick(SteelTankBlockEntity controller) {
        lastKnownController = controller;
        if(!isActive()) {
            return;
        }
        Level level = controller.getLevel();
        if(level.isClientSide) {
            gaugeRotation = Math.min(90, activeHeat * 15);
            visualGaugeRotation.chase(gaugeRotation, 0.2f, LerpedFloat.Chaser.EXP);
            visualGaugeRotation.tickChaser();
        }
        if(needsHeatLevelUpdate && updateTemperature(controller)) {
            controller.notifyUpdate();
        }
    }

    public boolean isActive() {
        return !attachedControllers.isEmpty();
    }

    public boolean isActuallyActive() {
        return isActive() && activeHeat != 0;
    }

    public boolean updateTemperature(SteelTankBlockEntity controller) {
        BlockPos controllerPos = controller.getBlockPos();
        Level level = controller.getLevel();

        int prevActive = activeHeat;
        activeHeat = 0;
        for (int xOffset = 0; xOffset < controller.getWidth(); xOffset++) {
            for (int zOffset = 0; zOffset < controller.getWidth(); zOffset++) {
                BlockPos pos = controllerPos.offset(xOffset, -1, zOffset);
                BlockState blockState = level.getBlockState(pos);
                float heat = BoilerHeater.findHeat(level, pos, blockState);
                if (heat > 0) {
                    activeHeat += (int) heat;
                }
            }
        }

        return prevActive != activeHeat;
    }

    public void evaluate(SteelTankBlockEntity controller) {
        BlockPos controllerPos = controller.getBlockPos();
        Level level = controller.getLevel();
        attachedControllers.clear();

        for (int xOffset = 0; xOffset < controller.getWidth(); xOffset++) {
            for (int zOffset = 0; zOffset < controller.getWidth(); zOffset++) {

                BlockPos pos = controllerPos.offset(xOffset, 0, zOffset);
                BlockState blockState = level.getBlockState(pos);

                if (!FluidTankBlock.isTank(blockState)) {
                    continue;
                }
                for (Direction d : Iterate.directions) {
                    BlockPos attachedPos = pos.relative(d);
                    BlockState attachedState = level.getBlockState(attachedPos);
                    if (attachedState.getBlock() instanceof DistillationControllerBlock &&
                            attachedState.getValue(DistillationControllerBlock.FACING) == d) {
                        attachedControllers.add(attachedPos);
                    }
                }
            }
        }

        needsHeatLevelUpdate = true;
    }

    public void clear() {
        activeHeat = 0;
        attachedControllers.clear();
    }

    public CompoundTag write() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("ActiveHeat", activeHeat);
        nbt.putInt("Controllers", attachedControllers.size());
        for(int i = 0; i < attachedControllers.size(); i++) {
            nbt.put("Controller" + i, NbtUtils.writeBlockPos(attachedControllers.get(i)));
        }
        nbt.putBoolean("Update", needsHeatLevelUpdate);
        return nbt;
    }

    public void read(CompoundTag nbt) {
        activeHeat = nbt.getInt("ActiveHeat");
        attachedControllers.clear();
        int size = nbt.getInt("Controllers");
        for(int i = 0; i < size; i++) {
            attachedControllers.add(NbtUtils.readBlockPos(nbt, "Controller" + i).orElseThrow());
        }
        needsHeatLevelUpdate = nbt.getBoolean("Update");
    }

    public IFluidHandler createHandler() {
        return new TowerFluidHandler();
    }

    public class TowerFluidHandler implements IFluidHandler {

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            return 10000;
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return true;
        }

        @Override
        public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
            if (lastKnownController == null ||
                    resource.isEmpty() ||
                    lastKnownController.getLevel() == null ||
                    attachedControllers.isEmpty() ||
                    !isFluidValid(0, resource)) {
                return 0;
            }
            // Find any existing fluid - and cancel this operation if any controller contains a different fluid
            if(!DistillationControllerBlockEntity.scanFluidValid(lastKnownController, resource.getFluid())) {
                return resource.getAmount();
            }

            int remaining = resource.getAmount();
            int fraction = remaining / attachedControllers.size();

            if(remaining >= attachedControllers.size()) { // Because the fraction would always be 0
                // Try to split it across controllers
                for (BlockPos controller : attachedControllers) {
                    if (lastKnownController.getLevel().getBlockEntity(controller) instanceof DistillationControllerBlockEntity be) {
                        int filled = be.fluid.getHandler().fill(resource.copyWithAmount(fraction), FluidAction.EXECUTE);
                        remaining -= filled;
                        if (remaining == 0) {
                            break;
                        }
                    }
                }
            }

            if(remaining > 0) {
                // Then round-robin for the remaining amount
                for (BlockPos controller : attachedControllers) {
                    if (lastKnownController.getLevel().getBlockEntity(controller) instanceof DistillationControllerBlockEntity be) {
                        int filled = be.fluid.getHandler().fill(resource.copyWithAmount(remaining), FluidAction.EXECUTE);
                        remaining -= filled;
                        if(remaining == 0) {
                            break;
                        }
                    }
                }
            }

            return resource.getAmount() - remaining;
        }

        @Override
        public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
            return FluidStack.EMPTY;
        }

    }
}
