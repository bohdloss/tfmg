package com.drmangotea.tfmg.content.machinery.vat.base;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.mixin.accessor.TankSegmentAccessor;
import com.drmangotea.tfmg.recipes.VatMachineRecipe;
import com.drmangotea.tfmg.registry.TFMGPackets;
import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.theme.Color;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Math.abs;

public class VatBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IMultiBlockEntityContainer.Fluid {

    private static final int MAX_SIZE = 3;
    public VatInventory inputInventory;
    public VatInventory outputInventory;
    public SmartFluidTankBehaviour inputTank;
    public SmartFluidTankBehaviour outputTank;
    protected LazyOptional<IFluidHandler> fluidCapability;
    protected LazyOptional<IItemHandlerModifiable> itemCapability;
    protected boolean forceFluidLevelUpdate;
    protected BlockPos controller;
    protected BlockPos lastKnownPos;
    protected boolean updateConnectivity;
    protected boolean updateCapability;
    protected boolean window = false;
    protected int luminosity;
    protected int width;
    protected int height;
    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;
    //
    public LerpedFloat[] fluidLevel = new LerpedFloat[8];
    /// /
    public List<String> machines = new ArrayList<>();
    boolean evaluateNextTick = true;
    float efficiency = 1;
    int timer = 0;
    public VatMachineRecipe recipe;
    int heatLevel = 0;
    HeatCondition heatCondition = HeatCondition.NONE;
    private static final Object vatRecipeKey = new Object();

    public VatBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(10);
        for (int i = 0; i < 8; i++) {
            fluidLevel[i] = LerpedFloat.linear();
        }
        window = false;
        // if(Objects.equals(((VatBlock) getBlockState().getBlock()).vatType, "tfmg:firebrick_lined_vat"))
        //     window = false;
        inputInventory = new VatInventory(4, this);
        outputInventory = new VatInventory(4, this);
        itemCapability = LazyOptional.of(() -> new CombinedInvWrapper(inputInventory, outputInventory));
        forceFluidLevelUpdate = true;
        updateConnectivity = false;
        updateCapability = false;
        //window = ((VatBlock)getBlockState().getBlock()).vatType != "tfmg:firebrick_lined_vat";
        height = 1;
        width = 1;
        refreshCapability();


    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        inputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 4, 4000, true)
                .whenFluidUpdates(this::onInventoryChanged);
        outputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 4, 4000, true)
                .whenFluidUpdates(this::onInventoryChanged)
                .forbidInsertion();
        behaviours.add(inputTank);
        behaviours.add(outputTank);

        fluidCapability = LazyOptional.of(() -> {
            LazyOptional<? extends IFluidHandler> inputCap = inputTank.getCapability();
            LazyOptional<? extends IFluidHandler> outputCap = outputTank.getCapability();
            return new CombinedTankWrapper(outputCap.orElse(null), inputCap.orElse(null));
        });
    }

    protected Object getRecipeCacheKey() {
        return vatRecipeKey;
    }

    protected void updateConnectivity() {
        updateConnectivity = false;
        if (level.isClientSide)
            return;
        if (!isController())
            return;
        ConnectivityHandler.formMulti(this);


    }

    @Override
    public void lazyTick() {
        super.lazyTick();

        if (recipe == null && isController()) {
            recipe = getMatchingRecipe();
        }

        updateTemperature();
        if (level.isClientSide && !(level instanceof PonderLevel)) {
            int tankNumber = 0;
            for (int i = 0; i < 8; i++) {
                IFluidHandler fluidHandler = this.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);

                if(fluidHandler != null) {

                    fluidLevel[i].chase((double) (fluidHandler.getFluidInTank(tankNumber).getAmount()) / inputTank.getPrimaryHandler().getCapacity(), .5f, LerpedFloat.Chaser.EXP);
                    getFillState();
                    tankNumber++;
                }
            }
        }
    }

    public void updateTemperature() {

        if (!isController())
            return;

        int prevHeat = heatLevel;
        heatLevel = 0;
        heatCondition = HeatCondition.NONE;
        BlockPos pos1 = controller == null ? getBlockPos() : controller;
        VatBlockEntity be = getControllerBE() == null ? this : getControllerBE();

        for (int xOffset = 0; xOffset < be.width; xOffset++) {
            for (int zOffset = 0; zOffset < be.width; zOffset++) {
                BlockPos pos = pos1.offset(xOffset, -1, zOffset);
                BlockState blockState = level.getBlockState(pos);
                float heat = BoilerHeater.findHeat(level, pos, blockState);
                if (heat > 0) {
                    heatLevel += (int) heat;
                }
            }
        }
        if (heatLevel >= 2) {
            heatCondition = HeatCondition.HEATED;
        }
        if (heatLevel >= 4) {
            heatCondition = HeatCondition.SUPERHEATED;
        }
        if (heatLevel != prevHeat)
            notifyUpdate();
    }

    /**
     * finds a recipe with matching inputs and machines connected
     */
    public VatMachineRecipe getMatchingRecipe() {
        List<Recipe<?>> list = RecipeFinder.get(getRecipeCacheKey(), level, r -> r instanceof VatMachineRecipe);

        for (Recipe<?> recipe1 : list) {
            VatMachineRecipe testedRecipe = (VatMachineRecipe) recipe1;
            if (getTotalTankSize() < testedRecipe.minSize)
                continue;
            boolean doesntMatch = false;

            if (!Objects.equals(testedRecipe.machines, machines)) {
                continue;
            }


            if (!testedRecipe.allowedVatTypes.contains(((VatBlock) getBlockState().getBlock()).vatType)) {
                continue;
            }

            //for(int i =0;i<machines.size();i++){
            //    if(!Objects.equals(machines.get(i),testedRecipe.machines)){
            //        doesntMatch = true;
            //        break;
            //    }
            //}


            //for (String string : testedRecipe.machines) {
//
            //    if (!machines.contains(string)) {
            //        doesntMatch = true;
            //        break;
            //    }
            //}


            IFluidHandler fluidHandler = getCapability(ForgeCapabilities.FLUID_HANDLER).orElseGet(null);
            IItemHandler itemHandler = getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);

            //checks if vat contains needed fluids
            Map<Integer, Integer> isFluidFound = new HashMap<>();
            for (int i = 0; i < testedRecipe.getFluidIngredients().size(); i++) {
                FluidIngredient ingredient = testedRecipe.getFluidIngredients().get(i);
                Integer foundAt = null;
                if (ingredient.getMatchingFluidStacks().isEmpty())
                    break;

                for (int y = 0; y < fluidHandler.getTanks(); y++) {
                    if (isFluidFound.containsValue(y))
                        continue;
                    FluidStack stack = fluidHandler.getFluidInTank(y);
                    if (ingredient.test(stack)) {
                        foundAt = y;
                        break;
                    }
                }
                if (foundAt != null) {
                    isFluidFound.put(i, foundAt);
                } else doesntMatch = true;
            }

            //same but with items
            SmartInventory testInventory = new SmartInventory(8, this);

            for (int i = 0; i < 4; i++) {
                testInventory.setStackInSlot(i, inputInventory.getStackInSlot(i).copy());
            }
            for (int i = 0; i < 4; i++) {
                testInventory.setStackInSlot(i + 4, outputInventory.getStackInSlot(i).copy());
            }

            for (int i = 0; i < testedRecipe.getIngredients().size(); i++) {
                Ingredient ingredient = testedRecipe.getIngredients().get(i);
                boolean found = false;
                for (int y = 0; y < 8; y++) {
                    ItemStack stack = testInventory.getStackInSlot(y).copy();
                    if (ingredient.test(stack)) {
                        found = true;
                        testInventory.getItem(y).shrink(1);
                        break;
                    }
                }
                if (!found) {
                    doesntMatch = true;
                    break;
                }
            }

            if (false) {
                Map<Integer, Integer> isFound = new HashMap<>();
                for (int i = 0; i < testedRecipe.getIngredients().size(); i++) {
                    Integer foundAt = null;
                    if (testedRecipe.getIngredients().get(i).isEmpty())
                        break;

                    for (int y = 0; y < itemHandler.getSlots(); y++) {
                        if (isFound.containsValue(y))
                            continue;
                        ItemStack stack = itemHandler.getStackInSlot(y);
                        if (testedRecipe.getIngredients().get(i).test(stack)) {
                            foundAt = y;
                            break;
                        }
                    }
                    if (foundAt != null) {
                        isFound.put(i, foundAt);
                    } else doesntMatch = true;
                }
            }

            //////////////////////////////////////////
            if (doesntMatch)
                continue;
            //checks if there's enough space for a recipe to happen
            Map<net.minecraft.world.level.material.Fluid, Integer> fluids = new HashMap<>();

            List<FluidStack> totalFluidStacks = new ArrayList<>();

            for (int i = 0; i < outputTank.getPrimaryHandler().getTanks(); i++) {
                totalFluidStacks.add(outputTank.getPrimaryHandler().getFluidInTank(i));
            }
            totalFluidStacks.addAll(testedRecipe.getFluidResults());

            for (FluidStack stack : totalFluidStacks) {
                if (stack.isEmpty())
                    continue;
                if (fluids.containsKey(stack.getFluid())) {
                    fluids.replace(stack.getFluid(), fluids.get(stack.getFluid()) + stack.getAmount());
                } else fluids.put(stack.getFluid(), stack.getAmount());

            }
            AtomicBoolean cantOutput = new AtomicBoolean(false);
            fluids.forEach((f, a) -> {
                if (a > 4000)
                    cantOutput.set(true);
            });
            //
            Map<Item, Integer> items = new HashMap<>();

            List<ItemStack> totalItemStacks = new ArrayList<>();

            for (int i = 0; i < outputInventory.getSlots(); i++) {
                totalItemStacks.add(outputInventory.getStackInSlot(i));
            }
            totalItemStacks.addAll(testedRecipe.getRollableResultsAsItemStacks());

            for (ItemStack stack : totalItemStacks) {
                if (stack.isEmpty())
                    continue;
                if (items.containsKey(stack.getItem())) {
                    items.replace(stack.getItem(), items.get(stack.getItem()) + stack.getCount());
                } else items.put(stack.getItem(), stack.getCount());
//
            }
            items.forEach((f, a) -> {
                if (a > 64)
                    cantOutput.set(true);
            });
            if (cantOutput.get())
                continue;
            ///////////////////////////////////////

            return testedRecipe;
        }

        return null;
    }

    @Override
    public void tick() {
        super.tick();


        handleRecipe();

        if (syncCooldown > 0) {
            syncCooldown--;
            if (syncCooldown == 0 && queuedSync)
                sendData();
        }
        if (evaluateNextTick) {
            if (!level.isClientSide())
                TFMGPackets.getChannel().send(PacketDistributor.ALL.noArg(), new VatEvaluationPacket(this.getBlockPos()));
            evaluate();
            evaluateNextTick = false;
        }

        if (lastKnownPos == null)
            lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition) && worldPosition != null) {
            onPositionChanged();
            return;
        }

        if (updateCapability) {
            updateCapability = false;
            refreshCapability();
        }
        if (updateConnectivity)
            updateConnectivity();
        for (int i = 0; i < 8; i++) {
            fluidLevel[i].tickChaser();
        }

    }

    /**
     * performs recipe,
     * ticks the processing timer
     */
    public void handleRecipe() {
        if (recipe == null)
            return;
        if (!isController())
            return;
        if (heatLevel < recipe.heatLevel)
            return;
        if (recipe.getRequiredHeat() == HeatCondition.HEATED && heatCondition == HeatCondition.NONE)
            return;
        if (recipe.getRequiredHeat() == HeatCondition.SUPERHEATED && heatCondition != HeatCondition.SUPERHEATED)
            return;

        if (timer >= recipe.getProcessingDuration()) {


            SmartFluidTank outputFluidHandler = outputTank.getPrimaryHandler();
            IFluidHandler fluidHandler = getCapability(ForgeCapabilities.FLUID_HANDLER).orElseGet(null);
            IItemHandler itemHandler = getCapability(ForgeCapabilities.ITEM_HANDLER).orElseGet(null);


            //fluid input
            for (FluidIngredient ingredient : recipe.getFluidIngredients()) {
                for (int i = 0; i < fluidHandler.getTanks(); i++) {
                    FluidStack fluidInTank = fluidHandler.getFluidInTank(i);
                    if (ingredient.test(new FluidStack(fluidInTank, 4000))) {
                        fluidHandler.getFluidInTank(i).setAmount(fluidInTank.getAmount() - ingredient.getRequiredAmount());
                        break;
                    }
                }
            }
            //item output

            for (ProcessingOutput output : recipe.getRollableResults()) {

                ItemStack itemStack = output.rollOutput();


                boolean handled = false;
                for (int i = 0; i < outputInventory.getSlots(); i++) {
                    ItemStack stackInSlot = outputInventory.getStackInSlot(i);
                    if (stackInSlot.isEmpty())
                        continue;

                    if (stackInSlot.is(itemStack.getItem())) {
                        outputInventory.getStackInSlot(i).setCount(stackInSlot.getCount() + (itemStack.getCount()));

                        handled = true;
                        break;
                    }
                }
                if (handled)
                    break;
                for (int i = 0; i < outputInventory.getSlots(); i++) {
                    ItemStack itemInSlot = outputInventory.getStackInSlot(i);
                    if (itemInSlot.isEmpty()) {
                        outputInventory.setStackInSlot(i, itemStack);
                        break;
                    }
                }
            }
            //item input
            if (recipe != null)
                for (Ingredient ingredient : recipe.getIngredients()) {
                    for (int i = 0; i < fluidHandler.getTanks(); i++) {
                        ItemStack stackInInv = itemHandler.getStackInSlot(i);
                        if (ingredient.test(new ItemStack(stackInInv.getItem(), 64))) {
                            stackInInv.setCount(stackInInv.getCount() - ingredient.getItems()[0].getCount());
                            break;
                        }
                    }
                }
            //fluid output
            List<Integer> handledFluidResults = new ArrayList<>();

            for (FluidStack fluidStack : recipe.getFluidResults()) {
                for (int i = 0; i < outputFluidHandler.getTanks(); i++) {
                    FluidStack fluidInTank = outputFluidHandler.getFluidInTank(i);
                    if (fluidInTank.getFluid().isSame(fluidStack.getFluid())) {
                        outputFluidHandler.fill(new FluidStack(fluidStack.copy(), fluidStack.getAmount()), IFluidHandler.FluidAction.EXECUTE);
                        handledFluidResults.add(i);
                        break;
                    }
                }
                for (int i = 0; i < outputFluidHandler.getTanks(); i++) {
                    FluidStack fluidInTank = outputFluidHandler.getFluidInTank(i);
                    if (!handledFluidResults.contains(i) && fluidInTank.isEmpty()) {
                        outputFluidHandler.fill(new FluidStack(fluidStack.copy(), fluidStack.getAmount()), IFluidHandler.FluidAction.EXECUTE);
                        break;
                    }
                }
            }
            recipe = null;
            timer = 0;
        } else {
            timer++;
        }
    }





    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    @Override
    public boolean isController() {
        return controller == null || worldPosition.getX() == controller.getX()
                && worldPosition.getY() == controller.getY() && worldPosition.getZ() == controller.getZ();
    }

    @Override
    public void initialize() {
        super.initialize();
        sendData();
        if (level.isClientSide)
            invalidateRenderBoundingBox();
    }

    private void onPositionChanged() {
        removeController(true);
        lastKnownPos = worldPosition;
    }

    protected void onInventoryChanged() {


        if (!hasLevel())
            return;

        recipe = getMatchingRecipe();
        FluidStack newFluidStack = inputTank.getPrimaryHandler().getFluid();
        FluidType attributes = newFluidStack.getFluid()
                .getFluidType();
        int luminosity = (int) (attributes.getLightLevel(newFluidStack) / 1.2f);
        boolean reversed = attributes.isLighterThanAir();
        int maxY = (int) ((getFillState() * height) + 1);

        for (int yOffset = 0; yOffset < height; yOffset++) {
            boolean isBright = reversed ? (height - yOffset <= maxY) : (yOffset < maxY);
            int actualLuminosity = isBright ? luminosity : luminosity > 0 ? 1 : 0;

            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {
                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    VatBlockEntity vatAt = ConnectivityHandler.partAt(getType(), level, pos);
                    if (vatAt == null)
                        continue;
                    level.updateNeighbourForOutputSignal(pos, vatAt.getBlockState()
                            .getBlock());
                    if (vatAt.luminosity == actualLuminosity)
                        continue;
                    vatAt.setLuminosity(actualLuminosity);
                }
            }
        }

        if (!level.isClientSide) {
            setChanged();
            sendData();
        }

    }

    protected void setLuminosity(int luminosity) {
        if (level.isClientSide)
            return;
        if (this.luminosity == luminosity)
            return;
        this.luminosity = luminosity;
        sendData();
    }

    @SuppressWarnings("unchecked")
    @Override
    public VatBlockEntity getControllerBE() {
        if (isController())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof VatBlockEntity)
            return (VatBlockEntity) blockEntity;
        return null;
    }

    public void evaluate() {

        if (!isController()) {
            if (getControllerBE() == null) {
                return;
            }
            getControllerBE().evaluate();
            return;
        }

        List<String> oldMachines = machines;
        machines = new ArrayList<>();
        heatLevel = 0;
        heatCondition = HeatCondition.NONE;

        int superheatedCount = 0;

        float speed = 1;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                for (int yOffset = 0; yOffset < getHeight() + 2; yOffset++) {
                    BlockPos pos = getBlockPos().below().offset(xOffset, yOffset, zOffset);
                    BlockState blockState = level.getBlockState(pos);

                    if (VatBlock.isVat(blockState))
                        continue;

                    BlockEntity blockEntity = level.getBlockEntity(pos);

                    if (blockEntity instanceof IVatMachine be) {
                        if (be.getOperationId().isEmpty())
                            continue;

                        if (!isAtValidLocation(be.getPositionRequirement(), pos))
                            continue;

                        be.vatUpdated(this);
                        machines.add(be.getOperationId());
                        efficiency *= (be.getWorkPercentage() / 100);
                    }


                }
            }
        }
        efficiency = speed;
        if (oldMachines != machines)
            recipe = null;

        notifyUpdate();
    }


    public boolean isAtValidLocation(IVatMachine.PositionRequirement requirement, BlockPos pos) {
        return switch (requirement) {
            case ANY -> true;
            case BOTTOM -> pos.getY() == getController().getY() - 1;
            case TOP -> pos.getY() == getController().getY() + height;
            case ANY_CENTER -> isAtCenter(pos);
            case BOTTOM_CENTER -> isAtCenter(pos) && pos.getY() == getController().getY() - 1;
            case TOP_CENTER -> isAtCenter(pos) && pos.getY() == getController().getY() + height;
        };
    }


    public boolean isAtCenter(BlockPos pos) {
        return width < 3 || (pos.getX() == getController().getX() + 1 && pos.getZ() == getController().getZ() + 1);
    }

    public void applyVatSize(int blocks) {


        //inputTank.getPrimaryHandler().setCapacity(blocks * getCapacityMultiplier());
        //outputTank.getPrimaryHandler().setCapacity(blocks * getCapacityMultiplier());

        inputTank.forEach(s -> {
            ((TankSegmentAccessor) s).tfmg$tank().setCapacity(blocks * getCapacityMultiplier());
        });
        outputTank.forEach(s -> {
            ((TankSegmentAccessor) s).tfmg$tank().setCapacity(blocks * getCapacityMultiplier());
        });
        int overflow = inputTank.getPrimaryHandler().getFluidAmount() - inputTank.getPrimaryHandler().getCapacity();
        if (overflow > 0)
            inputTank.getPrimaryHandler().drain(overflow, IFluidHandler.FluidAction.EXECUTE);


        int overflow2 = outputTank.getPrimaryHandler().getFluidAmount() - outputTank.getPrimaryHandler().getCapacity();
        if (overflow2 > 0)
            outputTank.getPrimaryHandler().drain(overflow2, IFluidHandler.FluidAction.EXECUTE);

        forceFluidLevelUpdate = true;

        evaluateNextTick = true;
    }

    public void removeController(boolean keepFluids) {
        if (level.isClientSide)
            return;
        updateConnectivity = true;
        if (!keepFluids)
            applyVatSize(1);
        controller = null;
        width = 1;
        height = 1;
        onInventoryChanged();

        BlockState state = getBlockState();
        if (VatBlock.isVat(state)) {
            state = state.setValue(VatBlock.BOTTOM, true);
            state = state.setValue(VatBlock.TOP, true);
            state = state.setValue(VatBlock.SHAPE, window ? VatBlock.Shape.WINDOW : VatBlock.Shape.PLAIN);
            getLevel().setBlock(worldPosition, state, 22);
        }

        evaluateNextTick = true;

        refreshCapability();
        setChanged();
        sendData();
    }

    public void toggleWindows() {
        VatBlockEntity be = getControllerBE();
        if (be == null)
            return;

        if (((VatBlock) getBlockState().getBlock()).vatType == "tfmg:firebrick_lined_vat")
            return;

        be.setWindows(!be.window);
    }

    public void sendDataImmediately() {
        syncCooldown = 0;
        queuedSync = false;
        sendData();
    }

    @Override
    public void sendData() {
        if (syncCooldown > 0) {
            queuedSync = true;
            return;
        }
        super.sendData();
        queuedSync = false;
        syncCooldown = SYNC_RATE;
    }

    public void setWindows(boolean window) {
        this.window = window;
        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {

                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    BlockState blockState = level.getBlockState(pos);
                    if (!VatBlock.isVat(blockState))
                        continue;

                    VatBlock.Shape shape = VatBlock.Shape.PLAIN;
                    if (window) {
                        // SIZE 1: Every tank has a window
                        if (width == 1)
                            shape = VatBlock.Shape.WINDOW;
                        // SIZE 2: Every tank has a corner window
                        if (width == 2)
                            shape = xOffset == 0 ? zOffset == 0 ? VatBlock.Shape.WINDOW_NW : VatBlock.Shape.WINDOW_SW
                                    : zOffset == 0 ? VatBlock.Shape.WINDOW_NE : VatBlock.Shape.WINDOW_SE;
                        // SIZE 3: Tanks in the center have a window
                        if (width == 3 && abs(abs(xOffset) - abs(zOffset)) == 1)
                            shape = VatBlock.Shape.WINDOW;
                    }

                    level.setBlock(pos, blockState.setValue(VatBlock.SHAPE, shape), 22);
                    level.getChunkSource()
                            .getLightEngine()
                            .checkBlock(pos);
                }
            }
        }
    }

    public void updateState() {
        if (!isController())
            return;
        for (int yOffset = 0; yOffset < height; yOffset++)
            for (int xOffset = 0; xOffset < width; xOffset++)
                for (int zOffset = 0; zOffset < width; zOffset++)
                    if (level.getBlockEntity(
                            worldPosition.offset(xOffset, yOffset, zOffset)) instanceof VatBlockEntity fbe)
                        fbe.refreshCapability();
    }

    @Override
    public void setController(BlockPos controller) {
        if (level.isClientSide && !isVirtual())
            return;
        if (controller.equals(this.controller))
            return;
        this.controller = controller;
        refreshCapability();
        setChanged();
        sendData();
    }

    private void refreshCapability() {
        LazyOptional<IFluidHandler> oldFluidCap = fluidCapability;
        LazyOptional<IItemHandlerModifiable> oldItemCap = itemCapability;
        fluidCapability = getNewFluidCapability();
        itemCapability = getNewItemCapability();
        oldFluidCap.invalidate();
        oldItemCap.invalidate();
    }

    private LazyOptional<IFluidHandler> getNewFluidCapability() {
        LazyOptional<? extends IFluidHandler> inputCap = inputTank.getCapability();
        LazyOptional<? extends IFluidHandler> outputCap = outputTank.getCapability();

        IFluidHandler inputHandler = inputCap.orElse(null);
        IFluidHandler outputHandler = outputCap.orElse(null);

        if (inputHandler == null || outputHandler == null)
            return LazyOptional.empty();

        return isController() ? LazyOptional.of(() -> new CombinedTankWrapper(inputHandler, outputHandler))
                : getControllerBE() != null ? getControllerBE().getNewFluidCapability() : LazyOptional.empty();
    }

    private LazyOptional<IItemHandlerModifiable> getNewItemCapability() {
        return isController() ? LazyOptional.of(() -> new CombinedInvWrapper(inputInventory, outputInventory))
                : getControllerBE() != null ? getControllerBE().getNewItemCapability() : LazyOptional.empty();
    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        if (isController())
            return super.createRenderBoundingBox().expandTowards(width - 1, height - 1, width - 1);
        else
            return super.createRenderBoundingBox();
    }

    @Nullable
    public VatBlockEntity getOtherVatBE(Direction direction) {
        BlockEntity otherBE = level.getBlockEntity(worldPosition.relative(direction));
        if (otherBE instanceof VatBlockEntity)
            return (VatBlockEntity) otherBE;
        return null;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        if(!isController())
            return getControllerBE().addToGoggleTooltip(tooltip,isPlayerSneaking);
        CreateLang.translate("goggles.vat.header")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);

        CreateLang.translate("goggles.vat.contents")
                .forGoggles(tooltip);

        CreateLang.translate("goggles.vat.attachments")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        for (String operation : machines)
            CreateLang.translate("goggles.vat."+operation.replace(":","."))
                    .forGoggles(tooltip);


        CreateLang.translate("goggles.vat.heat_status")
                .add(CreateLang.translate(heatCondition == HeatCondition.NONE ? "goggles.vat.no_heat" : heatCondition == HeatCondition.HEATED ? "goggles.vat.heated" : "goggles.vat.superheated"))
                .color(heatCondition == HeatCondition.NONE ? 0x7a7a77 : heatCondition == HeatCondition.HEATED ? 0xdea216 : 0x16c7de)
                .forGoggles(tooltip);

        CreateLang.translate("goggles.vat.contents")
                .forGoggles(tooltip);

        ///

        IItemHandlerModifiable items = itemCapability.orElse(new ItemStackHandler());
        IFluidHandler fluids = fluidCapability.orElse(new FluidTank(0));
        boolean isEmpty = true;

        for (int i = 0; i < items.getSlots(); i++) {
            ItemStack stackInSlot = items.getStackInSlot(i);
            if (stackInSlot.isEmpty())
                continue;
            CreateLang.text("")
                    .add(Component.translatable(stackInSlot.getDescriptionId())
                            .withStyle(ChatFormatting.GRAY))
                    .add(CreateLang.text(" x" + stackInSlot.getCount())
                            .style(ChatFormatting.GREEN))
                    .forGoggles(tooltip, 1);
            isEmpty = false;
        }

        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
        for (int i = 0; i < fluids.getTanks(); i++) {
            FluidStack fluidStack = fluids.getFluidInTank(i);
            if (fluidStack.isEmpty())
                continue;
            CreateLang.text("")
                    .add(CreateLang.fluidName(fluidStack)
                            .add(CreateLang.text(" "))
                            .style(ChatFormatting.GRAY)
                            .add(CreateLang.number(fluidStack.getAmount())
                                    .add(mb)
                                    .style(ChatFormatting.BLUE)))
                    .forGoggles(tooltip, 1);
            isEmpty = false;
        }

        if (isEmpty)
            tooltip.remove(0);

        return true;
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        BlockPos controllerBefore = controller;
        int prevSize = width;
        int prevHeight = height;
        int prevLum = luminosity;

        updateConnectivity = compound.contains("Uninitialized");
        luminosity = compound.getInt("Luminosity");
        controller = null;
        lastKnownPos = null;

        if (compound.contains("LastKnownPos"))
            lastKnownPos = NbtUtils.readBlockPos(compound.getCompound("LastKnownPos"));
        if (compound.contains("Controller"))
            controller = NbtUtils.readBlockPos(compound.getCompound("Controller"));

        if (isController()) {
            window = compound.getBoolean("Window");
            width = compound.getInt("Size");
            height = compound.getInt("Height");
            inputTank.getPrimaryHandler().setCapacity(getTotalTankSize() * getCapacityMultiplier());
            outputTank.getPrimaryHandler().setCapacity(getTotalTankSize() * getCapacityMultiplier());
            if (inputTank.getPrimaryHandler().getSpace() < 0)
                inputTank.getPrimaryHandler().drain(-inputTank.getPrimaryHandler().getSpace(), IFluidHandler.FluidAction.EXECUTE);
            if (outputTank.getPrimaryHandler().getSpace() < 0)
                outputTank.getPrimaryHandler().drain(-outputTank.getPrimaryHandler().getSpace(), IFluidHandler.FluidAction.EXECUTE);
            inputInventory.deserializeNBT(compound.getCompound("InputItems"));
            outputInventory.deserializeNBT(compound.getCompound("OutputItems"));
            //
            //machines = new ArrayList<>();
            //for(int i = 0; i<compound.getInt("MachineCount");i++){
            //    machines.add(compound.getString("Machine"+i));
            //}
        }

        // if (compound.contains("ForceFluidLevel") || fluidLevel == null)
        //     fluidLevel = LerpedFloat.linear()
        //             .startWithValue(getFillState());

        updateCapability = true;

        if (!clientPacket)
            return;

        boolean changeOfController = !Objects.equals(controllerBefore, controller);
        if (changeOfController || prevSize != width || prevHeight != height) {
            if (hasLevel())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
            if (isController()) {
                inputTank.getPrimaryHandler().setCapacity(getCapacityMultiplier() * getTotalTankSize());
                outputTank.getPrimaryHandler().setCapacity(getCapacityMultiplier() * getTotalTankSize());
            }
            invalidateRenderBoundingBox();
        }
        if (isController()) {
            float fillState = getFillState();
            //if (compound.contains("ForceFluidLevel") || fluidLevel == null)
            //    fluidLevel = LerpedFloat.linear()
            //            .startWithValue(fillState);
            //fluidLevel.chase(fillState, 0.5f, LerpedFloat.Chaser.EXP);
        }
        if (luminosity != prevLum && hasLevel())
            level.getChunkSource()
                    .getLightEngine()
                    .checkBlock(worldPosition);

        //if (compound.contains("LazySync"))
        //    fluidLevel.chase(fluidLevel.getChaseTarget(), 0.125f, LerpedFloat.Chaser.EXP);
    }

    public float getFillState() {
        IFluidHandler fluidHandler = this.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
        for (int i = 0; i < fluidHandler.getTanks(); i++)
            if (!fluidHandler.getFluidInTank(i).isEmpty())
                return (float) fluidHandler.getFluidInTank(i).getAmount() / fluidHandler.getTankCapacity(0);

        return 0;

    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        if (updateConnectivity)
            compound.putBoolean("Uninitialized", true);

        if (lastKnownPos != null)
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));
        if (!isController())
            compound.put("Controller", NbtUtils.writeBlockPos(controller));
        if (isController()) {
            compound.putBoolean("Window", window);
            compound.putInt("Size", width);
            compound.putInt("Height", height);
            compound.put("InputItems", inputInventory.serializeNBT());
            compound.put("OutputItems", outputInventory.serializeNBT());


            //for(int i = 0; i<machines.size();i++){
            //    compound.putString("Machine"+i, machines.get(i));
            //}
            //compound.putInt("MachineCount",machines.size());

        }
        compound.putInt("Luminosity", luminosity);
        super.write(compound, clientPacket);

        if (!clientPacket)
            return;
        if (forceFluidLevelUpdate)
            compound.putBoolean("ForceFluidLevel", true);
        if (queuedSync)
            compound.putBoolean("LazySync", true);
        forceFluidLevelUpdate = false;
    }

    public int getTotalTankSize() {
        return width * width * height;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!fluidCapability.isPresent() || !itemCapability.isPresent())
            refreshCapability();
        if (cap == ForgeCapabilities.FLUID_HANDLER)
            return fluidCapability.cast();
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return itemCapability.cast();


        return super.getCapability(cap, side);
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }


    public static int getCapacityMultiplier() {
        return AllConfigs.server().fluids.fluidTankCapacity.get() * 1000;
    }

    public static int getMaxHeight() {
        return 3;
    }

    public LerpedFloat[] getFluidLevel() {
        return fluidLevel;
    }

    //   public void setFluidLevel(LerpedFloat fluidLevel) {
    //      this.fluidLevel = fluidLevel;
    //  }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    @Override
    public void notifyMultiUpdated() {
        BlockState state = this.getBlockState();
        if (VatBlock.isVat(state)) { // safety
            state = state.setValue(VatBlock.BOTTOM, getController().getY() == getBlockPos().getY());
            state = state.setValue(VatBlock.TOP, getController().getY() + height - 1 == getBlockPos().getY());
            level.setBlock(getBlockPos(), state, 6);
        }
        if (isController()) {
            setWindows(window);

        }
        evaluateNextTick = true;
        onInventoryChanged();
        setChanged();
    }

    @Override
    public void setExtraData(@Nullable Object data) {
        if (data instanceof Boolean)
            window = (boolean) data;
    }

    @Override
    @Nullable
    public Object getExtraData() {
        return window;
    }

    @Override
    public Object modifyExtraData(Object data) {
        if (data instanceof Boolean windows) {
            windows |= window;
            return windows;
        }
        return data;
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return Direction.Axis.Y;
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        if (longAxis == Direction.Axis.Y)
            return getMaxHeight();
        return getMaxWidth();
    }

    @Override
    public int getMaxWidth() {
        return MAX_SIZE;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public boolean hasTank() {
        return true;
    }

    @Override
    public int getTankSize(int tank) {
        return getCapacityMultiplier();
    }

    @Override
    public void setTankSize(int tank, int blocks) {
        applyVatSize(blocks);
    }

    @Override
    public IFluidTank getTank(int tank) {

        return new FluidTank(1);
    }

    @Override
    public FluidStack getFluid(int tank) {
        return inputTank.getPrimaryHandler().getFluid();
    }

}
