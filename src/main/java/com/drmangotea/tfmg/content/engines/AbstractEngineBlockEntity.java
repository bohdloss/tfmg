package com.drmangotea.tfmg.content.engines;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.base.TFMGUtils;
import com.drmangotea.tfmg.config.TFMGConfigs;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.drmangotea.tfmg.content.engines.EngineBlock.ENGINE_STATE;
import static com.drmangotea.tfmg.content.engines.EngineBlock.EngineState.NORMAL;
import static com.drmangotea.tfmg.content.engines.EngineBlock.EngineState.SHAFT;
import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public abstract class AbstractEngineBlockEntity extends KineticBlockEntity {

    //
    EngineComponentsInventory componentsInventory;
    FluidTank fuelTank;
    FluidTank exhaustTank;
    LazyOptional<IFluidHandler> fluidCapability;
    //
    BlockPos controller = getBlockPos();
    boolean connectNextTick = true;
    List<Long> engines = new ArrayList<>();
    int engineNumber = 0;
    //


    public AbstractEngineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        setLazyTickRate(10);
        fuelTank = TFMGUtils.createTank(4000, false, this::tankUpdated);
        exhaustTank = TFMGUtils.createTank(4000, true, false, this::tankUpdated);
        fluidCapability = LazyOptional.of(() -> new CombinedTankWrapper(fuelTank, exhaustTank));

        componentsInventory = new EngineComponentsInventory(this, EngineProperties.commonRegularComponents());

        refreshCapability();
    }

    public void tankUpdated(FluidStack stack) {
        sendData();
        setChanged();
    }

    public Ingredient nextComponent() {
        if (!isController())
            return Ingredient.EMPTY;
        for (int i = 0; i < componentsInventory.getSlots(); i++) {
            if (componentsInventory.getStackInSlot(i).isEmpty()) {
                return componentsInventory.components.get(i);
            }
        }

        return Ingredient.EMPTY;
    }

    @Override
    public void tick() {
        super.tick();


        if (connectNextTick) {
            connect();
            connectNextTick = false;
        }
    }

    public abstract int effectiveSpeed();

    public abstract float efficiencyModifier();

    public abstract float speedModifier();

    public abstract float torqueModifier();

    public abstract String engineId();


    private void refreshCapability() {
        LazyOptional<IFluidHandler> oldCap = fluidCapability;
        fluidCapability = LazyOptional.of(this::handlerForCapability);
        oldCap.invalidate();
    }

    private IFluidHandler handlerForCapability() {
        return isController() ? new CombinedTankWrapper(fuelTank, exhaustTank)
                : getControllerBE().handlerForCapability();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!fluidCapability.isPresent())
            refreshCapability();
        if (cap == ForgeCapabilities.FLUID_HANDLER)
            return fluidCapability.cast();
        return super.getCapability(cap, side);
    }

    public int getMaxLength() {
        return TFMGConfigs.common().machines.engineMaxLength.get();
    }

    public boolean insertItem(ItemStack itemStack, boolean shifting) {
        Direction facing = getBlockState().getValue(HORIZONTAL_FACING);

        if (itemStack.is(AllBlocks.SHAFT.asItem()) && getBlockState().getValue(ENGINE_STATE) == NORMAL && !(level.getBlockEntity(getBlockPos().relative(facing)) instanceof AbstractEngineBlockEntity)) {
            playInsertionSound();
            level.setBlock(getBlockPos(), getBlockState().setValue(ENGINE_STATE, SHAFT), 2);
            itemStack.shrink(1);
            setChanged();
            sendData();
            return true;
        }
        if (itemStack.is(TFMGItems.SCREWDRIVER.get())) {
                for (int i = componentsInventory.components.size()-1; i >= 0; i--) {
                    if (!componentsInventory.getItem(i).isEmpty()) {
                        dropItem(componentsInventory.getItem(i));
                        componentsInventory.setStackInSlot(i, ItemStack.EMPTY);
                        playRemovalSound();
                        setChanged();
                        sendData();
                        return true;
                    }
                }

        }
        if (!isController())
            return false;
        if (nextComponent().test(itemStack))
            if (componentsInventory.insertItem(itemStack)) {
                if (!itemStack.is(TFMGItems.SCREWDRIVER.get()))
                    itemStack.shrink(1);
                playInsertionSound();
                setChanged();
                sendData();
                return true;
            }
        return false;
    }

    public void dropItem(ItemStack stack) {
        Vec3 dropVec = VecHelper.getCenterOf(worldPosition).add(0, 0.3f, 0);
        ItemEntity dropped = new ItemEntity(level, dropVec.x, dropVec.y, dropVec.z, stack);
        dropped.setDefaultPickUpDelay();
        dropped.setDeltaMovement(0, 0.15f, 0);
        level.addFreshEntity(dropped);
    }

    public int engineLength() {
        return engines.size();
    }

    public void playInsertionSound() {
        level.playSound(null, getBlockPos(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4f, 0.5f);
    }
    public void playRemovalSound() {
        level.playSound(null, getBlockPos(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4f, 0.5f);
    }
    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        controller = BlockPos.of(compound.getLong("Controller"));
        if (isController()) {
            componentsInventory.deserializeNBT(compound.getCompound("Components"));
            fuelTank.readFromNBT(compound.getCompound("FuelTank"));
            exhaustTank.readFromNBT(compound.getCompound("ExhaustTank"));
        }
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putLong("Controller", controller.asLong());
        if (isController()) {
            compound.put("Components", componentsInventory.serializeNBT());
            compound.put("FuelTank", fuelTank.writeToNBT(new CompoundTag()));
            compound.put("ExhaustTank", exhaustTank.writeToNBT(new CompoundTag()));

        }
    }

    public boolean isController() {
        return controller.equals(getBlockPos());
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        Lang.number(controller.getX()).forGoggles(tooltip);
        Lang.number(controller.getY()).forGoggles(tooltip);
        Lang.number(controller.getZ()).forGoggles(tooltip);
        Lang.number(engineNumber).style(ChatFormatting.DARK_GREEN).forGoggles(tooltip);
        if(isController()&&!nextComponent().isEmpty())
            Lang.text(nextComponent().getItems()[0].getDisplayName().getString()).forGoggles(tooltip);

        TFMGUtils.createFluidTooltip(this, tooltip);

        return true;
    }

    public AbstractEngineBlockEntity getControllerBE() {
        if (isController())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof AbstractEngineBlockEntity)
            return (AbstractEngineBlockEntity) blockEntity;
        return null;
    }

    @Override
    public void remove() {
        super.remove();

        if (level.getBlockEntity(controller) instanceof AbstractEngineBlockEntity be) {
            be.updateOthers();
        } else updateOthers();
    }

    public void updateOthers() {
        for (long id : engines) {
            BlockPos pos = BlockPos.of(id);
            if (level.getBlockEntity(pos) instanceof AbstractEngineBlockEntity be) {
                be.connectNextTick = true;
            }
        }
    }

    public void connect() {
        Direction updateDirection = getBlockState().getValue(HORIZONTAL_FACING).getAxis() == Direction.Axis.X ? Direction.WEST : Direction.NORTH;
        if (level.getBlockEntity(getBlockPos().relative(updateDirection)) instanceof AbstractEngineBlockEntity be) {
            be.connect();
            return;
        }

        engines = new ArrayList<>();
        for (int i = 0; i < getMaxLength(); i++) {
            BlockPos pos = getBlockPos().relative(updateDirection.getOpposite(), i);
            if (level.getBlockEntity(pos) instanceof AbstractEngineBlockEntity be) {
                if (be.getBlockState().getValue(HORIZONTAL_FACING).getAxis() != updateDirection.getAxis())
                    return;
                be.detashEngines();
                engines.add(pos.asLong());
                be.engineNumber = i;
                be.engines = new ArrayList<>();
                be.controller = getBlockPos();
                be.refreshCapability();
                level.setBlock(pos, be.getBlockState().setValue(HORIZONTAL_FACING, i > 0 ? updateDirection.getOpposite() : updateDirection), 2);

                be.sendData();
                be.setChanged();
                if (be.getBlockState().getValue(ENGINE_STATE) != NORMAL) {
                    break;
                }
            }
        }
        setChanged();
        sendData();

    }

    public void detashEngines() {
        for (long id : engines) {
            BlockPos pos = BlockPos.of(id);
            if (level.getBlockEntity(pos) instanceof AbstractEngineBlockEntity be) {
                be.controller = be.getBlockPos();
                be.engineNumber = 0;
                be.refreshCapability();
                be.sendData();
                be.setChanged();
            }
        }
    }


}
