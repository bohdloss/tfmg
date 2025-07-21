package it.bohdloss.tfmg.content.machinery.misc.firebox;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.base.AbstractMultiblock;
import it.bohdloss.tfmg.base.TFMGFluidBehavior;
import it.bohdloss.tfmg.config.TFMGConfigs;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGFluids;
import it.bohdloss.tfmg.registry.TFMGTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

@EventBusSubscriber
public class FireboxBlockEntity extends AbstractMultiblock implements IHaveGoggleInformation {
    protected static final int CAPACITY_MULTIPLIER = 4000;
    protected static final int EXHAUST_PRODUCTION = 500;

    protected TFMGFluidBehavior fuel;
    protected TFMGFluidBehavior exhaust;
    protected CombinedTankWrapper allCaps;

    protected int timer;


    public FireboxBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        fuel = new TFMGFluidBehavior(TFMGFluidBehavior.TYPE, "Fuel", this, CAPACITY_MULTIPLIER)
                .withValidator(fluidStack -> fluidStack.getFluid().is(TFMGTags.TFMGFluidTags.FIREBOX_FUEL.tag))
                .allowExtraction(false)
                .allowInsertion(true)
                .syncCapacity(true)
                .withCallback(this::notifyUpdate);
        exhaust = new TFMGFluidBehavior(TFMGFluidBehavior.SECONDARY_TYPE, "Exhaust", this, CAPACITY_MULTIPLIER)
                .withValidator(fluidStack -> fluidStack.getFluid().isSame(TFMGFluids.CARBON_DIOXIDE.get()))
                .allowExtraction(true)
                .allowInsertion(false)
                .syncCapacity(true)
                .withCallback(this::notifyUpdate);
        allCaps = new CombinedTankWrapper(exhaust.getCapability(), fuel.getCapability());

        behaviours.add(fuel);
        behaviours.add(exhaust);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                TFMGBlockEntities.FIREBOX.get(),
                (be, ctx) -> ((FireboxBlockEntity) be.getControllerBE()).allCaps
        );
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        FireboxBlockEntity controllerBE = (FireboxBlockEntity) getControllerBE();
        if (controllerBE == null) {
            return false;
        }
        return containedFluidTooltip(tooltip, isPlayerSneaking, controllerBE.allCaps);
    }

    @Override
    public void tick() {
        super.tick();

        if(level.isClientSide || !isController()) {
            return;
        }

        if(timer > 0) {
            timer--;
            return;
        }

        int fuelConsumption = TFMGConfigs.common().machines.fireboxFuelConsumption.get();
        boolean canBurn = canBurn();

        if(canBurn) {
            fuel.getHandler().drain(fuelConsumption, IFluidHandler.FluidAction.EXECUTE);
            exhaust.getHandler().fill(new FluidStack(TFMGFluids.CARBON_DIOXIDE, EXHAUST_PRODUCTION), IFluidHandler.FluidAction.EXECUTE);
        }

        boolean isBurning = level.getBlockState(getBlockPos()).getValue(FireboxBlock.HEAT_LEVEL) != BlazeBurnerBlock.HeatLevel.NONE;

        if(canBurn != isBurning) {
            withAllBlocksDo(pos -> {
                TFMGUtils.changeProperty(level, pos, FireboxBlock.HEAT_LEVEL, canBurn ? BlazeBurnerBlock.HeatLevel.FADING : BlazeBurnerBlock.HeatLevel.NONE);
            });
        }

        timer = 3 * 20;
    }

    public boolean canBurn() {
        int fuelConsumption = TFMGConfigs.common().machines.fireboxFuelConsumption.get();
        FluidStack fuelExtract = fuel.getHandler().drain(fuelConsumption, IFluidHandler.FluidAction.SIMULATE);
        int exhaustFill = exhaust.getHandler().fill(new FluidStack(TFMGFluids.CARBON_DIOXIDE, EXHAUST_PRODUCTION), IFluidHandler.FluidAction.SIMULATE);

        return fuelExtract.is(TFMGTags.TFMGFluidTags.FIREBOX_FUEL.tag) &&
                fuelExtract.getAmount() == fuelConsumption &&
                exhaustFill == EXHAUST_PRODUCTION;
    }

    @Override
    protected void onMultiblockChange() {
        updateBlockstates();
    }

    protected void updateBlockstates() {
        if(level == null || level.isClientSide || !isController()) {
            return;
        }

        BlockPos controller = getBlockPos();

        switch (width) {
            case 1 -> {
                TFMGUtils.changeProperty(level, controller, FireboxBlock.SHAPE, FireboxBlock.Shape.SINGLE);
            }
            case 2 -> {
                withAllBlocksDo(pos -> {
                    TFMGUtils.changeProperty(level, pos, FireboxBlock.SHAPE, FireboxBlock.Shape.CORNER);
                });
            }
            case 3 -> {
                withAllBlocksDo(pos -> {
                    TFMGUtils.changeProperty(level, pos, FireboxBlock.SHAPE, FireboxBlock.Shape.SIDE);
                });

                TFMGUtils.changeProperty(level, controller, FireboxBlock.SHAPE, FireboxBlock.Shape.CORNER);
                TFMGUtils.changeProperty(level, controller.relative(Direction.SOUTH, 2), FireboxBlock.SHAPE, FireboxBlock.Shape.CORNER);
                TFMGUtils.changeProperty(level, controller.relative(Direction.EAST, 2), FireboxBlock.SHAPE, FireboxBlock.Shape.CORNER);
                TFMGUtils.changeProperty(level, controller.relative(Direction.SOUTH, 2).relative(Direction.EAST, 2), FireboxBlock.SHAPE, FireboxBlock.Shape.CORNER);

                TFMGUtils.changeProperty(level, controller.relative(Direction.SOUTH, 1).relative(Direction.EAST, 1), FireboxBlock.SHAPE, FireboxBlock.Shape.CENTER);
            }
            default -> {}
        }
    }

    @Override
    protected BlockState rotateBlockToMatch(BlockState current, BlockState controller) {
        return current;
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return Direction.Axis.Y;
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        if(longAxis == Direction.Axis.Y) {
            return 1;
        } {
            return getMaxWidth();
        }
    }

    @Override
    public int getMaxWidth() {
        return 3;
    }

    @Override
    public boolean hasTank() {
        return true;
    }

    @Override
    public int getTankSize(int tank) {
        return width * width;
    }

    @Override
    public void setTankSize(int tank, int blocks) {
        fuel.getHandler().setCapacity(CAPACITY_MULTIPLIER * blocks);
        exhaust.getHandler().setCapacity(CAPACITY_MULTIPLIER * blocks);
    }

    @Override
    public IFluidTank getTank(int tank) {
        return fuel.getHandler();
    }

    @Override
    public FluidStack getFluid(int tank) {
        return fuel.getHandler().getFluid();
    }

    @Override
    public boolean hasInventory() {
        return false;
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);

        compound.putInt("Timer", timer);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        timer = compound.getInt("Timer");
    }
}
