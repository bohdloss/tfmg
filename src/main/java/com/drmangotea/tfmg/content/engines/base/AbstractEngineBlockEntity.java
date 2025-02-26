package com.drmangotea.tfmg.content.engines.base;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.base.TFMGUtils;
import com.drmangotea.tfmg.config.TFMGConfigs;
import com.drmangotea.tfmg.content.electricity.base.KineticElectricBlockEntity;
import com.drmangotea.tfmg.content.engines.fuels.BaseFuelTypes;
import com.drmangotea.tfmg.content.engines.fuels.EngineFuelTypeManager;
import com.drmangotea.tfmg.content.engines.fuels.FuelType;
import com.drmangotea.tfmg.content.engines.regular_engine.RegularEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.upgrades.EnginePipingUpgrade;
import com.drmangotea.tfmg.content.engines.upgrades.EngineUpgrade;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import com.drmangotea.tfmg.registry.TFMGFluids;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.simibubi.create.AllBlocks;
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
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.drmangotea.tfmg.content.engines.base.EngineBlock.ENGINE_STATE;
import static com.drmangotea.tfmg.content.engines.base.EngineBlock.EngineState.NORMAL;
import static com.drmangotea.tfmg.content.engines.base.EngineBlock.EngineState.SHAFT;
import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public abstract class AbstractEngineBlockEntity extends KineticElectricBlockEntity {

    //
    public EngineComponentsInventory componentsInventory;
    public EngineFluidTank fuelTank;
    public EngineFluidTank exhaustTank;
    public LazyOptional<IFluidHandler> fluidCapability;
    //
    public BlockPos controller = getBlockPos();
    boolean connectNextTick = true;
    public List<Long> engines = new ArrayList<>();
    public int engineNumber = 0;
    //
    int oil = 0;
    int coolingFluid = 0;
    //
    public float rpm = 0;
    float torque = 0;
    float fuelInjectionRate = 0;
    //
    boolean reverse = false;
    //
    int highestSignal;
    int signal;
    boolean signalChanged;
    //
    int fuelConsumptionTimer = 0;
    //
    public Optional<? extends EngineUpgrade> upgrade = Optional.empty();


    public AbstractEngineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        setLazyTickRate(10);
        fuelTank = new EngineFluidTank(4000, false, true, this::tankUpdated);
        exhaustTank = new EngineFluidTank(8000, true, false, this::tankUpdated);
        fluidCapability = LazyOptional.of(() -> new CombinedTankWrapper(fuelTank, exhaustTank));

        componentsInventory = new EngineComponentsInventory(this, EngineProperties.commonRegularComponents());

        refreshCapability();
    }

    public boolean hasUpgrade() {
        return upgrade.isPresent();
    }

    public void tankUpdated(FluidStack stack) {


        if (stack.getFluid().isSame(TFMGFluids.CARBON_DIOXIDE.get()) && stack.getAmount() >= exhaustTank.getSpace())
            updateRotation();

        sendData();
        setChanged();
    }


    public boolean hasAllComponents() {

        if (level.getBlockEntity(controller) instanceof AbstractEngineBlockEntity be) {
            return be.nextComponent() == Ingredient.EMPTY;
        }

        return false;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        neighbourChanged();

        upgrade.ifPresent(engineUpgrade -> engineUpgrade.lazyTickUpgrade(this));

        exhaustTank.forceFill(new FluidStack(TFMGFluids.CARBON_DIOXIDE.get(), Math.min(300, getFuelConsumption() * 7)), IFluidHandler.FluidAction.EXECUTE);

        if (fuelConsumptionTimer <= 2) {
            fuelConsumptionTimer++;
        } else {
            fuelConsumptionTimer = 0;
            fuelTank.forceDrain(getFuelConsumption(), IFluidHandler.FluidAction.EXECUTE);

            if (fuelTank.isEmpty())
                updateRotation();

        }
    }

    @Override
    public int voltageGeneration() {

        if (upgrade.isPresent() && upgrade.get().getItem() == TFMGBlocks.GENERATOR.asItem())
            return (int) (20 * (rpm / 3000));

        return 0;
    }

    @Override
    public int powerGeneration() {
        if (upgrade.isPresent() && upgrade.get().getItem() == TFMGBlocks.GENERATOR.asItem())
            return (int) rpm;

        return 0;
    }

    @Override
    public boolean makeElectricityTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return false;
    }

    public int getFuelConsumption() {

        if (getGeneratedSpeed() == 0)
            return 0;


        return (int) (12.5f * (1 / efficiencyModifier()) * getSpeedEfficiency() * fuelInjectionRate) * engineLength();
    }

    public float getSpeedEfficiency() {
        if (rpm >= 6000)
            return 1;

        return 1 / (0.08f * (rpm / 1000) + 0.5f);
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

    public abstract List<TagKey<Fluid>> getSupportedFuels();

    public void onUpdated() {
    }

    public void updateRotation() {

        if (!isController()) {
            if (level.getBlockEntity(controller) instanceof AbstractEngineBlockEntity be)
                be.updateRotation();
            return;
        }

        List<Long> allEngines = new ArrayList<>(engines);
        allEngines.add(controller.asLong());

        for (TagKey<Fluid> fluidTag : getSupportedFuels()) {
            if (fuelTank.getFluid().getFluid().is(fluidTag)) {
                if (!canWork()) {
                    allEngines.forEach(l -> {
                        BlockPos pos = BlockPos.of(l);
                        if (level.getBlockEntity(pos) instanceof AbstractEngineBlockEntity be) {
                            be.rpm = 0;
                            be.torque = 0;
                            be.updateGeneratedRotation();
                        }

                    });

                    return;
                }
                allEngines.forEach(l -> {
                    BlockPos pos = BlockPos.of(l);
                    if (level.getBlockEntity(pos) instanceof AbstractEngineBlockEntity be) {
                        be.rpm = 4000 * speedModifier() * fuelInjectionRate;
                        be.torque = 15 * torqueModifier() * fuelInjectionRate * engineLength();
                        be.updateGeneratedRotation();
                    }
                });
                return;
            }
        }
        updateGeneratedRotation();
        getAllEngines().forEach(l -> {
            if (hasLevel())
                if (level.getBlockEntity(BlockPos.of(l)) instanceof AbstractEngineBlockEntity be) {
                    be.updateGeneratedRotation();
                }

        });
    }


    @Override
    public float getGeneratedSpeed() {

        float speed;

        if (getBlockState().getValue(ENGINE_STATE) != SHAFT)
            return 0;

        if (hasLevel())

            if (level.getBlockEntity(controller) instanceof AbstractEngineBlockEntity controller) {

                if (controller.fuelTank.isEmpty())
                    return 0;
                if (!controller.canWork())
                    return 0;

                speed = rpm / 23;

                if (reverse)
                    speed = speed * -1;

                return convertToDirection(Math.min((int) speed, 256), getBlockState().getValue(HORIZONTAL_FACING));
            }
        return 0;
    }

    public boolean canWork() {

        if (exhaustTank.getSpace() == 0)
            return false;

        if (!nextComponent().isEmpty())
            return false;

        return true;
    }

    @Override
    public void tick() {
        super.tick();
        upgrade.ifPresent(engineUpgrade -> engineUpgrade.tickUpgrade(this));
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

        int newSignal = level.getBestNeighborSignal(getBlockPos());

        signal = newSignal;

        if (!isController()) {
            if (level.getBlockEntity(controller) instanceof AbstractEngineBlockEntity be) {
                be.analogSignalChanged();
                return;
            }
        }

        for (long posLong : engines) {
            BlockPos pos = BlockPos.of(posLong);
            newSignal = Math.max(level.getBestNeighborSignal(pos), newSignal);

        }
        newSignal = Math.max(level.getBestNeighborSignal(controller), newSignal);
        highestSignal = newSignal;
        fuelInjectionRate = highestSignal / 15f;
        updateRotation();

    }


    public abstract float efficiencyModifier();

    public abstract float speedModifier();

    public abstract float torqueModifier();

    public abstract String engineId();


    public FuelType getFuelType() {

        AtomicReference<FuelType> matchingType = new AtomicReference<>(BaseFuelTypes.FALLBACK);

        EngineFuelTypeManager.GLOBAL_TYPE_MAP.forEach((r, t) -> {
            TagKey<Fluid> fluidTag = t.getFluid();
            FluidStack fluid = fuelTank.getFluid();
            if (fluid.getFluid().is(fluidTag)) {
                matchingType.set(t);
            }

        });
        return matchingType.get();
    }

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
            updateRotation();

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
                    updateRotation();
                    setChanged();
                    sendData();
                    return true;
                }
            }
        }

        if (itemStack.is(TFMGItems.COOLING_FLUID_BOTTLE.get())) {

            if (level.getBlockEntity(controller) instanceof AbstractEngineBlockEntity be) {

                int toDrain = Math.min(2000 - coolingFluid, itemStack.getOrCreateTag().getInt("amount"));
                itemStack.getOrCreateTag().putInt("amount", itemStack.getOrCreateTag().getInt("amount") - toDrain);
                be.coolingFluid += toDrain;
                level.playSound(null, getBlockPos(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
                return true;
            }
        }
        if (itemStack.is(TFMGItems.OIL_CAN.get())) {
            if (level.getBlockEntity(controller) instanceof AbstractEngineBlockEntity be) {
                int toDrain = Math.min(2000 - oil, itemStack.getOrCreateTag().getInt("amount"));
                itemStack.getOrCreateTag().putInt("amount", itemStack.getOrCreateTag().getInt("amount") - toDrain);
                be.oil += toDrain;
                level.playSound(null, getBlockPos(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
                updateRotation();
                return true;
            }
        }
        if (itemStack.is(TFMGFluids.COOLING_FLUID.getBucket().get())) {
            if (coolingFluid <= 1000) {
                coolingFluid += 1000;
                player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
                level.playSound(null, getBlockPos(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
                updateRotation();
                return true;
            }
        }
        if (itemStack.is(TFMGFluids.LUBRICATION_OIL.getBucket().get())) {
            if (oil <= 1000) {
                oil += 1000;
                player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
                level.playSound(null, getBlockPos(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
                updateRotation();
                return true;
            }
        }
        if (EngineUpgrade.getUpgrades().containsKey(itemStack.getItem())) {
            Optional<? extends EngineUpgrade> itemUpgrade = EngineUpgrade.getUpgrades().get(itemStack.getItem()).createUpgrade();

            if (itemUpgrade.isPresent() && isUpgradeFirst(itemUpgrade.get())) {
                upgrade = itemUpgrade;
                playInsertionSound();
                updateRotation();
                upgrade.ifPresent(u->u.updateUpgrade(this));
                itemStack.shrink(1);
                setChanged();
                sendData();
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
                updateRotation();
                setChanged();
                sendData();
                return true;
            }
        }
        return false;
    }

    public boolean isUpgradeFirst(EngineUpgrade itemUpgrade) {

        for (AbstractEngineBlockEntity be : getEngines()) {

            if (be.upgrade.isPresent() && be.upgrade.get().getItem() == itemUpgrade.getItem())
                return false;

        }

        return true;
    }

    public List<Long> getAllEngines() {
        List<Long> list = new ArrayList<>(engines);
        list.add(controller.asLong());
        return list;
    }

    public void changeDirection() {


        playInsertionSound();
        reverse = !reverse;
        updateRotation();
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
    public void onLoad() {
        super.onLoad();
        if(this.hasUpgrade()&&this.upgrade.get().getItem() == TFMGBlocks.INDUSTRIAL_PIPE.asItem()){
            ((EnginePipingUpgrade)this.upgrade.get()).findTank(this);
        }
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        controller = BlockPos.of(compound.getLong("Controller"));
        reverse = compound.getBoolean("Reverse");

        if (EngineUpgrade.getUpgrades().get(ItemStack.of(compound.getCompound("UpgradeItem")).getItem()) != null)
            upgrade = Optional.of(EngineUpgrade.getUpgrades().get(ItemStack.of(compound.getCompound("UpgradeItem")).getItem()));
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
        compound.putBoolean("Reverse", reverse);
        if (upgrade.isPresent())
            compound.put("UpgradeItem", upgrade.get().getItem().getDefaultInstance().serializeNBT());
        if (isController()) {
            compound.put("Components", componentsInventory.serializeNBT());
            compound.put("FuelTank", fuelTank.writeToNBT(new CompoundTag()));
            compound.put("ExhaustTank", exhaustTank.writeToNBT(new CompoundTag()));
            compound.putInt("Oil", oil);
            compound.putInt("CoolingFluid", coolingFluid);
        }
    }

    public boolean isController() {
        return controller.equals(getBlockPos());
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        Lang.text("Speed Efficiency " + getSpeedEfficiency()).forGoggles(tooltip);
        Lang.text("Efficiency " + efficiencyModifier()).forGoggles(tooltip);
        Lang.text("Fuel Consumption " + getFuelConsumption()).forGoggles(tooltip);
        Lang.text("Rpm " + rpm).forGoggles(tooltip);
        Lang.text("Torque " + torque).forGoggles(tooltip);
        Lang.text("Injection Rate " + fuelInjectionRate).forGoggles(tooltip);
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

        int power = level.getBestNeighborSignal(getBlockPos());

        if (power != this.signal)
            this.signalChanged = true;

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
                if (be instanceof RegularEngineBlockEntity be1 && this instanceof RegularEngineBlockEntity be2 && be1.type != be2.type)
                    return;
                be.detashEngines();
                engines.add(pos.asLong());
                be.engineNumber = i;
                be.engines = new ArrayList<>();
                be.controller = getBlockPos();
                be.refreshCapability();
                if (i > 0)
                    level.setBlock(pos, be.getBlockState().setValue(HORIZONTAL_FACING, i > 0 ? updateDirection.getOpposite() : updateDirection), 2);
                onUpdated();
                be.sendData();
                be.setChanged();
                if (be.getBlockState().getValue(ENGINE_STATE) != NORMAL && i != 0) {
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


    public List<AbstractEngineBlockEntity> getEngines() {

        List<AbstractEngineBlockEntity> values = new ArrayList<>();

        for (Long position : getAllEngines()) {
            BlockPos pos = BlockPos.of(position);
            if (level.getBlockEntity(pos) instanceof AbstractEngineBlockEntity be)
                values.add(be);
        }
        return values;

    }

    public float getUpgradeSpeedModifier() {
        float modifier = 1;
        for (AbstractEngineBlockEntity be : getEngines()) {
            if (be.upgrade.isPresent())
                modifier *= be.upgrade.get().getSpeedModifier(this);
        }
        return modifier;
    }

    public float getUpgradeTorqueModifier() {
        float modifier = 1;
        for (AbstractEngineBlockEntity be : getEngines()) {
            if (be.upgrade.isPresent())
                modifier *= be.upgrade.get().getTorqueModifier(this);
        }
        return modifier;
    }

    public float getUpgradeEfficiencyModifier() {
        float modifier = 1;
        for (AbstractEngineBlockEntity be : getEngines()) {
            if (be.upgrade.isPresent())
                modifier *= be.upgrade.get().getEfficiencyModifier(this);
        }
        return modifier;
    }

}
