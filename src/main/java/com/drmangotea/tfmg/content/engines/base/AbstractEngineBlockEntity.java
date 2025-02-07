package com.drmangotea.tfmg.content.engines.base;

import com.drmangotea.tfmg.base.TFMGUtils;
import com.drmangotea.tfmg.config.TFMGConfigs;
import com.drmangotea.tfmg.content.engines.FluidContainingItem;
import com.drmangotea.tfmg.content.engines.regular_engine.RegularEngineBlockEntity;
import com.drmangotea.tfmg.registry.TFMGFluids;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

import static com.drmangotea.tfmg.content.engines.base.EngineBlock.ENGINE_STATE;
import static com.drmangotea.tfmg.content.engines.base.EngineBlock.EngineState.NORMAL;
import static com.drmangotea.tfmg.content.engines.base.EngineBlock.EngineState.SHAFT;
import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public abstract class AbstractEngineBlockEntity extends KineticBlockEntity {

    //
    public EngineComponentsInventory componentsInventory;
    public FluidTank fuelTank;
    public FluidTank exhaustTank;
    public LazyOptional<IFluidHandler> fluidCapability;
    //
    public BlockPos controller = getBlockPos();
    boolean connectNextTick = true;
    List<Long> engines = new ArrayList<>();
    int engineNumber = 0;
    //
    int oil = 0;
    int coolingFluid =0;
    //
    float rpm =0;
    float fuelInjectionRate = 0;
    //
    int signal;
    boolean signalChanged;


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

    public boolean hasAllComponents(){

        if(level.getBlockEntity(controller) instanceof AbstractEngineBlockEntity be){
            return be.nextComponent() == Ingredient.EMPTY;
        }

        return false;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        neighbourChanged();
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

    public void onUpdated(){}

    public void updateRotation(){

        if(!canWork()||!isController()){
            rpm = 0;
            return;
        }
        rpm = 4000*speedModifier()*fuelInjectionRate;

    }
    public boolean canWork(){

        if(!nextComponent().isEmpty())
            return false;

        return true;
    }

    @Override
    public void tick() {
        super.tick();


        if (connectNextTick) {
            connect();
            connectNextTick = false;
        }
        if (signalChanged) {
            signalChanged = false;
            analogSignalChanged();
        }
    }

    protected void analogSignalChanged() {

        int newSignal = level.getBestNeighborSignal(controller);

        for(long posLong : engines){
            BlockPos pos = BlockPos.of(posLong);
            newSignal = Math.max(level.getBestNeighborSignal(pos), newSignal);

        }
        if(isController()){
            signal = newSignal;
            fuelInjectionRate = signal / 15f;
            updateRotation();
        }
        if(level.getBlockEntity(controller) instanceof AbstractEngineBlockEntity be) {

            be.signal = newSignal;
            be.fuelInjectionRate = signal / 15f;
            be.updateRotation();
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

    public boolean insertItem(ItemStack itemStack, boolean shifting, Player player, InteractionHand hand) {
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
            for (int i = componentsInventory.components.size() - 1; i >= 0; i--) {
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

        if(itemStack.is(TFMGItems.COOLING_FLUID_BOTTLE.get())){
            int toDrain = Math.min(2000-coolingFluid,itemStack.getOrCreateTag().getInt("amount"));
            itemStack.getOrCreateTag().putInt("amount",itemStack.getOrCreateTag().getInt("amount")-toDrain);
            coolingFluid+=toDrain;
            level.playSound(null, getBlockPos(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
            return true;
        }
        if(itemStack.is(TFMGItems.OIL_CAN.get())){
            int toDrain = Math.min(2000-oil,itemStack.getOrCreateTag().getInt("amount"));
            itemStack.getOrCreateTag().putInt("amount",itemStack.getOrCreateTag().getInt("amount")-toDrain);
            oil+=toDrain;
            level.playSound(null, getBlockPos(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
            return true;
        }
        if(itemStack.is(TFMGFluids.COOLING_FLUID.getBucket().get())){
            if(coolingFluid<=1000){
                coolingFluid+=1000;
                player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
                level.playSound(null, getBlockPos(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
                return true;
            }
        }
        if(itemStack.is(TFMGFluids.LUBRICATION_OIL.getBucket().get())){
            if(oil<=1000){
                oil+=1000;
                player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
                level.playSound(null, getBlockPos(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
                return true;
            }
        }



        if (!isController())
            return false;
        if (nextComponent().test(itemStack)) {
            if (componentsInventory.insertItem(itemStack)) {
                if (!itemStack.is(TFMGItems.SCREWDRIVER.get()))
                    itemStack.shrink(1);
                playInsertionSound();
                setChanged();
                sendData();
                return true;
            }
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
            oil = compound.getInt("Oil");
            coolingFluid = compound.getInt("CoolingFluid");
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
            compound.putInt("Oil",oil);
            compound.putInt("CoolingFluid", coolingFluid);
        }
    }

    public boolean isController() {
        return controller.equals(getBlockPos());
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        Lang.number(fuelInjectionRate).forGoggles(tooltip);
        Lang.number(rpm).style(ChatFormatting.AQUA).forGoggles(tooltip);


        Lang.number(engineNumber).style(ChatFormatting.DARK_GREEN).forGoggles(tooltip);
        if (isController() && !nextComponent().isEmpty())
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
    //
    public void neighbourChanged() {

        if (!hasLevel())
            return;
        if(isController()){
            int power = level.getBestNeighborSignal(worldPosition);


            if (power != signal)
                signalChanged = true;
        }
        if(level.getBlockEntity(controller) instanceof AbstractEngineBlockEntity be) {
            int power = level.getBestNeighborSignal(worldPosition);


            if (power != be.signal)
                be.signalChanged = true;
        }
    }
    //
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
                if(be instanceof RegularEngineBlockEntity be1&&this instanceof RegularEngineBlockEntity be2 && be1.type!=be2.type)
                    return;
                be.detashEngines();
                engines.add(pos.asLong());
                be.engineNumber = i;
                be.engines = new ArrayList<>();
                be.controller = getBlockPos();
                be.refreshCapability();
                if(i>0)
                    level.setBlock(pos, be.getBlockState().setValue(HORIZONTAL_FACING, i > 0 ? updateDirection.getOpposite() : updateDirection), 2);
                onUpdated();
                be.sendData();
                be.setChanged();
                if (be.getBlockState().getValue(ENGINE_STATE) != NORMAL&&i!=0) {
                    break;
                }
            } else return;
        }
        updateRotation();
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
