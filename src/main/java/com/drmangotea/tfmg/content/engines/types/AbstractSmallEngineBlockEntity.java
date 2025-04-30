package com.drmangotea.tfmg.content.engines.types;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.base.TFMGUtils;
import com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.base.EngineComponentsInventory;
import com.drmangotea.tfmg.content.engines.base.EngineProperties;
import com.drmangotea.tfmg.content.engines.engine_controller.EngineControllerBlockEntity;
import com.drmangotea.tfmg.content.engines.upgrades.EnginePipingUpgrade;
import com.drmangotea.tfmg.content.engines.upgrades.EngineUpgrade;
import com.drmangotea.tfmg.content.engines.upgrades.TransmissionUpgrade;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import com.drmangotea.tfmg.registry.TFMGFluids;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.drmangotea.tfmg.content.engines.base.EngineBlock.ENGINE_STATE;
import static com.drmangotea.tfmg.content.engines.base.EngineBlock.EngineState.NORMAL;
import static com.drmangotea.tfmg.content.engines.base.EngineBlock.EngineState.SHAFT;
import static com.drmangotea.tfmg.content.engines.base.EngineBlock.SHAFT_FACING;
import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public abstract class AbstractSmallEngineBlockEntity extends AbstractEngineBlockEntity {

    public Optional<? extends EngineUpgrade> upgrade = Optional.empty();
    int oil = 0;
    int coolingFluid = 0;

    public EngineComponentsInventory componentsInventory;

    public BlockPos controller = getBlockPos();
    public boolean connectNextTick = true;
    public boolean delayedConnect = false;
    public List<Long> engines = new ArrayList<>();
    public int engineNumber = 0;

    public AbstractSmallEngineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        componentsInventory = new EngineComponentsInventory(this, EngineProperties.commonRegularComponents());
    }

    public int getFuelConsumption() {

        if (getGeneratedSpeed() == 0)
            return 0;


        return (int) (12.5f * (1 / efficiencyModifier()) * getSpeedEfficiency() * fuelInjectionRate) * (engineLength() + 1);
    }

    public void detashEngines() {
        //for (long id : engines) {
        //    BlockPos pos = BlockPos.of(id);
        //    if (level.getBlockEntity(pos) instanceof AbstractEngineBlockEntity be) {
        //        be.controller = be.getBlockPos();
        //        be.engineNumber = 0;
        //        be.refreshCapability();
        //        be.sendData();
        //        be.setChanged();
        //    }
        //}
    }

    public void setBlockStates(AbstractSmallEngineBlockEntity be, BlockPos last) {


        if (!be.isController()) {
            level.setBlock(be.getBlockPos(), level.getBlockState(be.getBlockPos()).setValue(SHAFT_FACING, getBlockState().getValue(SHAFT_FACING).getOpposite()), 2);
        }

    }

    public boolean hasAllComponents() {

        if (level.getBlockEntity(controller) instanceof AbstractSmallEngineBlockEntity be) {
            return be.nextComponent() == Ingredient.EMPTY;
        }

        return false;
    }

    public boolean hasUpgrade() {
        return upgrade.isPresent();
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
    public void lazyTick() {
        super.lazyTick();



        upgrade.ifPresent(engineUpgrade -> engineUpgrade.lazyTickUpgrade(this));
    }

    @Override
    public void neighbourChanged() {
        if (controller == null)
            return;

        super.neighbourChanged();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.hasUpgrade() && this.upgrade.get().getItem() == TFMGBlocks.INDUSTRIAL_PIPE.asItem()) {
            ((EnginePipingUpgrade) this.upgrade.get()).findTank(this);
        }
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        if (EngineUpgrade.getUpgrades().get(ItemStack.of(compound.getCompound("UpgradeItem")).getItem()) != null)
            upgrade = Optional.of(EngineUpgrade.getUpgrades().get(ItemStack.of(compound.getCompound("UpgradeItem")).getItem()));

        oil = compound.getInt("Oil");
        coolingFluid = compound.getInt("CoolingFluid");
        componentsInventory.deserializeNBT(compound.getCompound("Components"));
        super.read(compound, clientPacket);
        controller = BlockPos.of(compound.getLong("Controller"));
    }

    public int engineLength() {
        return engines.size();
    }

    @Override
    public boolean canWork() {

        if (!nextComponent().isEmpty())
            return false;

        return super.canWork();
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

    protected void analogSignalChanged() {

        if (controller == null)
            return;

        if (controlled) {
            fuelInjectionRate = highestSignal / 15f;
            return;
        }

        getControllerBE().updateRotation();
        getControllerBE().updateGeneratedRotation();
        int newSignal = level.getBestNeighborSignal(getBlockPos());

        signal = newSignal;

        if (!isController()) {
            if (level.getBlockEntity(controller) instanceof AbstractSmallEngineBlockEntity be) {
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

    @Override
    public IFluidHandler handlerForCapability() {


        return isController() || getControllerBE() == this ? new CombinedTankWrapper(fuelTank, exhaustTank)
                : getControllerBE().handlerForCapability();
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        compound.putLong("Controller", controller.asLong());

        if (controller != null) {
            compound.putLong("ControllerPos", controller.asLong());
        } else compound.remove("ControllerPos");

        if (upgrade.isPresent())
            compound.put("UpgradeItem", upgrade.get().getItem().getDefaultInstance().serializeNBT());
        compound.put("Components", componentsInventory.serializeNBT());
        compound.putInt("Oil", oil);
        compound.putInt("CoolingFluid", coolingFluid);
        super.write(compound, clientPacket);
    }

    public void updateRotation() {

        if (!isController()) {
            if (level.getBlockEntity(controller) instanceof AbstractSmallEngineBlockEntity be)
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
                speed = rpm / 40;
                if (reverse)
                    speed = speed * -1;


                return convertToDirection(Math.min((int) speed, 256), getBlockState().getValue(HORIZONTAL_FACING));
            }
        return 0;
    }

    @Override
    public void tankUpdated(FluidStack stack) {
        if (stack.getFluid().isSame(TFMGFluids.CARBON_DIOXIDE.get()) && stack.getAmount() >= exhaustTank.getSpace())
            updateRotation();
        super.tankUpdated(stack);
    }

    public boolean insertItem(ItemStack itemStack, boolean shifting, Player player, InteractionHand hand) {
        Direction shaft_facing = getBlockState().getValue(SHAFT_FACING);
        if (itemStack.is(AllBlocks.SHAFT.asItem()) && getBlockState().getValue(ENGINE_STATE) == NORMAL && !(level.getBlockEntity(getBlockPos().relative(shaft_facing)) instanceof AbstractEngineBlockEntity)) {
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

            if (level.getBlockEntity(controller) instanceof AbstractSmallEngineBlockEntity be) {

                int toDrain = Math.min(2000 - coolingFluid, itemStack.getOrCreateTag().getInt("amount"));
                itemStack.getOrCreateTag().putInt("amount", itemStack.getOrCreateTag().getInt("amount") - toDrain);
                be.coolingFluid += toDrain;
                level.playSound(null, getBlockPos(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
                return true;
            }
        }
        if (itemStack.is(TFMGItems.OIL_CAN.get())) {
            if (level.getBlockEntity(controller) instanceof AbstractSmallEngineBlockEntity be) {
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
                upgrade.ifPresent(u -> u.updateUpgrade(this));
                itemStack.shrink(1);
                if (upgrade.isPresent())
                    if (upgrade.get() instanceof TransmissionUpgrade transmissionUpgrade) {
                        if (itemStack.getOrCreateTag().contains("Position") && itemStack.getOrCreateTag().get("Position") != null) {
                            BlockPos pos = BlockPos.of(itemStack.getOrCreateTag().getLong("Position"));
                            if (level.getBlockEntity(pos) instanceof EngineControllerBlockEntity engineControllerBE) {

                                this.getControllerBE().updateGeneratedRotation();

                                getControllerBE().controller = pos;
                                engineControllerBE.enginePos = this.getBlockPos();
                                TFMG.LOGGER.debug("INSERT ITEM");
                                getControllerBE().highestSignal = 0;
                            }
                        }
                    }
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

    public List<AbstractSmallEngineBlockEntity> getEngines() {

        List<AbstractSmallEngineBlockEntity> values = new ArrayList<>();

        for (Long position : getAllEngines()) {
            BlockPos pos = BlockPos.of(position);
            if (level.getBlockEntity(pos) instanceof AbstractSmallEngineBlockEntity be)
                values.add(be);
        }
        return values;

    }

    public boolean isController() {

        if (controller == null)
            controller = getBlockPos();

        if (engineNumber == 0)
            controller = getBlockPos();

        return controller.equals(getBlockPos());
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        if (controller.asLong() == getBlockPos().asLong())
            CreateLang.text("CONTROLLER").forGoggles(tooltip);


        CreateLang.text("Speed Efficiency " + getSpeedEfficiency()).forGoggles(tooltip);
        CreateLang.text("Efficiency " + efficiencyModifier()).forGoggles(tooltip);
        CreateLang.text("Fuel Consumption " + getFuelConsumption()).forGoggles(tooltip);
        CreateLang.text("Rpm " + rpm).forGoggles(tooltip);
        CreateLang.text("length " + engineLength()).forGoggles(tooltip);
        CreateLang.text("Torque " + torque).forGoggles(tooltip);
        CreateLang.text("Injection Rate " + fuelInjectionRate).forGoggles(tooltip);
        CreateLang.text("Signal " + highestSignal).forGoggles(tooltip);


        CreateLang.number(engineNumber).style(ChatFormatting.DARK_GREEN).forGoggles(tooltip);
        if (isController() && !nextComponent().isEmpty())
            CreateLang.text(nextComponent().getItems()[0].getDisplayName().getString()).forGoggles(tooltip);

        TFMGUtils.createFluidTooltip(this, tooltip);

        return true;
    }

    public boolean isUpgradeFirst(EngineUpgrade itemUpgrade) {

        for (AbstractSmallEngineBlockEntity be : getEngines()) {

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

    public AbstractSmallEngineBlockEntity getControllerBE() {
        if (isController())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof AbstractSmallEngineBlockEntity)
            return (AbstractSmallEngineBlockEntity) blockEntity;
        return this;
    }

    @Override
    public void tick() {

        upgrade.ifPresent(engineUpgrade -> engineUpgrade.tickUpgrade(this));

        if (connectNextTick) {
            if (isController()) {
                connect();
                connectNextTick = false;
            }
        }

        super.tick();
    }

    public void connect() {


        Direction facing = getBlockState().getValue(HORIZONTAL_FACING);
        Direction updateDirection = facing.getOpposite();

        if (level.getBlockEntity(getBlockPos().relative(facing)) instanceof AbstractSmallEngineBlockEntity be && be.getBlockState().getBlock() == this.getBlockState().getBlock()) {
            be.connect();
            return;
        }
        TFMG.LOGGER.debug("CONNECT");

        engines = new ArrayList<>();

        for (int i = 0; i < getMaxLength(); i++) {
            BlockPos pos = getBlockPos().relative(updateDirection, i);
            if (level.getBlockEntity(pos) instanceof AbstractSmallEngineBlockEntity be) {
                if (be.getBlockState().getValue(HORIZONTAL_FACING) != facing) {
                    return;
                }

                level.setBlock(be.getBlockPos(), be.getBlockState().setValue(SHAFT_FACING, be.getBlockPos() == this.getBlockPos() ? facing : updateDirection), 2);

                //if (be instanceof RegularEngineBlockEntity be1 && this instanceof RegularEngineBlockEntity be2 && be1.type != be2.type) {
                //    setBlockStates(this, getBlockPos().relative(updateDirection, i - 1));
                //    TFMG.LOGGER.debug("set blockstates");
                //    return;
                //}
                be.detashEngines();
                engines.add(pos.asLong());

                // level.setBlock(be.getBlockPos().above(), Blocks.GOLD_BLOCK.defaultBlockState(),3);

                be.engineNumber = i;
                TFMG.LOGGER.debug("ADD " + be.engineNumber);
                be.engines = new ArrayList<>();
                be.controller = getBlockPos();
                be.refreshCapability();

                setBlockStates(be, null);
                updateGeneratedRotation();
                onUpdated();
                be.sendData();
                be.setChanged();

                if (be.getBlockState().getValue(ENGINE_STATE) != NORMAL && i != 0) {
                    setBlockStates(this, getBlockPos().relative(updateDirection, i - 1));
                    break;
                }
                if (i == getMaxLength() - 1)
                    setBlockStates(this, getBlockPos().relative(updateDirection, i));


            } else {
                TFMG.LOGGER.debug("END");
                setBlockStates(this, getBlockPos().relative(updateDirection, i - 1));
                return;
            }
        }

        updateGeneratedRotation();
        updateRotation();
        setChanged();
        sendData();

    }

    @Override
    public void remove() {
        super.remove();
        updateOthers();
    }

    public void updateOthers() {

        if (!isController()) {
            getControllerBE().connectNextTick = true;
        }


        Direction facing = getBlockState().getValue(HORIZONTAL_FACING);

        for (Direction direction : Direction.values()) {

            if (direction.getAxis() != facing.getAxis())
                continue;

            if (level.getBlockEntity(getBlockPos().relative(direction)) instanceof AbstractSmallEngineBlockEntity be) {
                level.setBlockAndUpdate(be.getBlockPos(), be.getBlockState().setValue(SHAFT_FACING, direction.getOpposite()));
                be.delayedConnect = true;
                be.connectNextTick = true;
                be.connect();
            }

        }

    }


    public float getUpgradeSpeedModifier() {
        float modifier = 1;
        for (AbstractSmallEngineBlockEntity be : getEngines()) {
            if (be.upgrade.isPresent())
                modifier *= be.upgrade.get().getSpeedModifier(this);
        }
        return modifier;
    }

    public float getUpgradeTorqueModifier() {
        float modifier = 1;
        for (AbstractSmallEngineBlockEntity be : getEngines()) {
            if (be.upgrade.isPresent())
                modifier *= be.upgrade.get().getTorqueModifier(this);
        }
        return modifier;
    }

    public float getUpgradeEfficiencyModifier() {
        float modifier = 1;
        for (AbstractSmallEngineBlockEntity be : getEngines()) {
            if (be.upgrade.isPresent())
                modifier *= be.upgrade.get().getEfficiencyModifier(this);
        }
        return modifier;
    }

}
