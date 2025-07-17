package it.bohdloss.tfmg.content.machinery.metallurgy.coke_oven;

import com.mojang.serialization.DataResult;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.DebugStuff;
import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.base.AbstractMultiblock;
import it.bohdloss.tfmg.base.TFMGFluidBehavior;
import it.bohdloss.tfmg.base.TFMGItemBehavior;
import it.bohdloss.tfmg.recipes.CokingRecipe;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

@EventBusSubscriber
public class CokeOvenBlockEntity extends AbstractMultiblock implements IHaveGoggleInformation {
    protected static final int CAPACITY_MULTIPLIER = 4000;

    public TFMGFluidBehavior creosote; // These names refer to the default coking recipe. There may be different contents.
    public TFMGFluidBehavior waste;
    public TFMGItemBehavior inventory;
    public int timer = -1;
    public LerpedFloat doorAngle = LerpedFloat.angular();
    @Nullable
    public ResourceLocation currentRecipe;
    public RecipeHolder<CokingRecipe> recipeCache;
    public boolean forceOpen;

    public CokeOvenBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                TFMGBlockEntities.COKE_OVEN.get(),
                (be, ctx) -> {
                    CokeOvenBlockEntity cokeOven = (CokeOvenBlockEntity) be.getControllerBE();
                    if(ctx == Direction.UP) {
                        return cokeOven.waste.getCapability();
                    } else {
                        return cokeOven.creosote.getCapability();
                    }
                }
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TFMGBlockEntities.COKE_OVEN.get(),
                (be, ctx) -> ((CokeOvenBlockEntity) be.getControllerBE()).inventory.getCapability()
        );
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        waste = new TFMGFluidBehavior(TFMGFluidBehavior.SECONDARY_TYPE, "SecondaryFluid", this, CAPACITY_MULTIPLIER)
                .allowExtraction(true)
                .allowInsertion(false)
                .syncCapacity(true)
                .withCallback(() -> {
                    notifyUpdate();
                    onFluidChange();
                });
        creosote = new TFMGFluidBehavior(TFMGFluidBehavior.TYPE, "PrimaryFluid", this, CAPACITY_MULTIPLIER)
                .allowExtraction(true)
                .allowInsertion(false)
                .syncCapacity(true)
                .withCallback(() -> {
                    notifyUpdate();
                    onFluidChange();
                });
        inventory = new TFMGItemBehavior(TFMGItemBehavior.TYPE, "Inventory", this, 1)
                .allowExtraction(true)
                .allowInsertion(true)
                .withCallback(() -> {
                    notifyUpdate();
                    onItemChange();
                });

        behaviours.add(waste);
        behaviours.add(creosote);
        behaviours.add(inventory);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.translate("goggles.coke_oven.header")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);

        CokeOvenBlockEntity controller = (CokeOvenBlockEntity) getControllerBE();
        if(controller != null) {
            RecipeHolder<CokingRecipe> recipe = controller.getRecipe();
            if(controller.timer != -1 && recipe != null) {
                int processingTicks = recipe.value().getProcessingDuration();
                CreateLang.translate("goggles.coke_oven.progress", (processingTicks - controller.timer) / 20 + " / " + processingTicks / 20)
                        .style(ChatFormatting.GOLD)
                        .forGoggles(tooltip);
            }
        }


        TFMGUtils.createFluidTooltip(this, tooltip, Direction.EAST);
        TFMGUtils.createFluidTooltip(this, tooltip, Direction.UP);
        TFMGUtils.createItemTooltip(this, tooltip, Direction.UP);
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide && isController()) {
            doorAngle.chase((timer > 0 && timer < 50) || forceOpen ? 90 : 0, 0.1f, LerpedFloat.Chaser.EXP);
            doorAngle.tickChaser();
            if (!forceOpen) {
                for (int i = 0; i < width; i++) {
                    BlockPos pos = getBlockPos().above(i);

                    if (level.getBlockEntity(pos) instanceof CokeOvenBlockEntity be && pos != getBlockPos()) {
                        be.forceOpen = timer > 0 && timer < 50;
                    }
                }
            }
        }

        if(!level.isClientSide && isController()) {
            if(!recipeTick()) {
                timer = -1;
                updateRecipe();
            }
        }
    }

    protected void onFluidChange() {
        updateRecipe();
    }

    protected void onItemChange() {
        updateRecipe();
    }

    // False means something went wrong
    protected boolean recipeTick() {
        RecipeHolder<CokingRecipe> holder = getRecipe();
        if(holder == null) {
            return false;
        }
        CokingRecipe cokingRecipe = holder.value();
        Ingredient main = cokingRecipe.getIngredients().getFirst();
        int inputAmount = main.getItems()[0].getCount();


        if(!main.test(inventory.getHandler().getStackInSlot(0)) ||
                inventory.getHandler().getStackInSlot(0).getCount() < inputAmount)
        {
            return true; // Doesn't reset recipe, but keeps ticking until the input amount is enough
        }

        if(timer == 0) {
            FluidStack primary = cokingRecipe.getPrimaryResult();
            FluidStack secondary = cokingRecipe.getSecondaryResult();

            if(creosote.getHandler().getSpace() < primary.getAmount() ||
                    !(creosote.getHandler().isEmpty() || creosote.getHandler().getFluid().getFluid().isSame(primary.getFluid())) ||
                    waste.getHandler().getSpace() < secondary.getAmount() ||
                    !(waste.getHandler().isEmpty() || waste.getHandler().getFluid().getFluid().isSame(secondary.getFluid()))
            ) {
                return true; // Doesn't reset recipe, but keeps ticking until enough space is available
            }
            timer = cokingRecipe.getProcessingDuration();

            inventory.getHandler().extractItem(0, inputAmount, false);
            creosote.getHandler().fill(primary, IFluidHandler.FluidAction.EXECUTE);
            waste.getHandler().fill(secondary, IFluidHandler.FluidAction.EXECUTE);

            Direction direction = getBlockState().getValue(FACING);

            Vec3 dropVec = VecHelper.getCenterOf(getLowestDoorPos().relative(direction)).add(0, 0.4, 0);
            ItemEntity dropped = new ItemEntity(level, dropVec.x, dropVec.y, dropVec.z, cokingRecipe.getResultItem(level.registryAccess()).copy());
            dropped.setDefaultPickUpDelay();
            dropped.setDeltaMovement(direction.getAxis() == Direction.Axis.X ? direction == Direction.WEST ? -.01f : .01f : 0, 0.05f, direction.getAxis() == Direction.Axis.Z ? direction == Direction.NORTH ? -.01f : .01f : 0);
            level.addFreshEntity(dropped);

            updateRecipe();
        } else if(timer != -1) {
            timer = Math.max(0, timer - width);

            notifyUpdate();
        }
        return true;
    }

    protected void updateRecipe() {
        Optional<RecipeHolder<CokingRecipe>> recipe = TFMGRecipeTypes.COKING.find(new SingleRecipeInput(inventory.getHandler().getStackInSlot(0)), level);
        if(recipe.isPresent()) {
            // Recipe went from none to some || or it changed -> reset timer to processing time
            if(currentRecipe == null || !currentRecipe.equals(recipe.get().id())) {
                timer = recipe.get().value().getProcessingDuration();
            }
            currentRecipe = recipe.get().id();
            recipeCache = recipe.get();
        } else {
            // Recipe went from some to none -> we aren't processing anything so set timer to -1
            if(currentRecipe != null) {
                timer = -1;
            }
            currentRecipe = null;
        }
    }

    @SuppressWarnings("unchecked")
    protected RecipeHolder<CokingRecipe> getRecipe() {
        if(currentRecipe == null) {
            return null;
        }
        if(recipeCache != null && recipeCache.id().equals(currentRecipe)) {
            return recipeCache;
        }
        Optional<RecipeHolder<?>> recipe = level.getRecipeManager().byKey(currentRecipe);
        if(recipe.isPresent()) {
            recipeCache = (RecipeHolder<CokingRecipe>) recipe.get();
            return recipeCache;
        }

        return null;
    }

    @Override
    protected void onMultiblockChange() {
        updateBlockstates();
    }

    protected BlockPos getLowestDoorPos() {
        Direction facing = getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        if(facing.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            return getBlockPos().relative(facing, width - 1);
        } else {
            return getBlockPos();
        }
    }

    protected void updateBlockstates() {
        if(level == null || level.isClientSide || !isController()) {
            return;
        }

        BlockPos column = getLowestDoorPos();
        withAllBlocksDo(currentPos -> {
            if(currentPos.getX() != column.getX() || currentPos.getZ() != column.getZ()) {
                TFMGUtils.changeProperty(level, currentPos, CokeOvenBlock.CONTROLLER_TYPE, CokeOvenBlock.ControllerType.CASUAL);
            }
        });
        switch (width) {
            case 1 -> {
                TFMGUtils.changeProperty(level, column, CokeOvenBlock.CONTROLLER_TYPE, CokeOvenBlock.ControllerType.CASUAL);
            }
            case 2 -> {
                TFMGUtils.changeProperty(level, column, CokeOvenBlock.CONTROLLER_TYPE, CokeOvenBlock.ControllerType.BOTTOM_ON);
                TFMGUtils.changeProperty(level, column.above(), CokeOvenBlock.CONTROLLER_TYPE, CokeOvenBlock.ControllerType.TOP_ON);
            }
            case 3 -> {
                TFMGUtils.changeProperty(level, column, CokeOvenBlock.CONTROLLER_TYPE, CokeOvenBlock.ControllerType.BOTTOM_ON);
                TFMGUtils.changeProperty(level, column.above(), CokeOvenBlock.CONTROLLER_TYPE, CokeOvenBlock.ControllerType.MIDDLE_ON);
                TFMGUtils.changeProperty(level, column.above().above(), CokeOvenBlock.CONTROLLER_TYPE, CokeOvenBlock.ControllerType.TOP_ON);
            }
            default -> {}
        }
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        try {
            super.write(compound, registries, clientPacket);

            if (isController()) {
                compound.putInt("Timer", timer);
                if (currentRecipe != null) {
                    ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, currentRecipe).ifSuccess(tag -> {
                        compound.put("CurrentRecipe", tag);
                    });
                }
            }
        } catch (Exception ex) {
            TFMG.LOGGER.debug(ex.toString());
        }
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        try {
            super.read(compound, registries, clientPacket);

            if (isController()) {
                timer = compound.getInt("Timer");
                currentRecipe = null;
                if (compound.contains("CurrentRecipe")) {
                    ResourceLocation.CODEC.decode(NbtOps.INSTANCE, compound.get("CurrentRecipe")).ifSuccess(res -> {
                        currentRecipe = res.getFirst();
                    });
                }
            }
        } catch (Exception ex) {
            TFMG.LOGGER.debug(ex.toString());
        }
    }

    @Override
    protected BlockState rotateBlockToMatch(BlockState current, BlockState controller) {
        return current.setValue(HorizontalDirectionalBlock.FACING, controller.getValue(HorizontalDirectionalBlock.FACING));
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return TFMGUtils.rotateHorizontalAxis(getBlockState().getValue(HorizontalDirectionalBlock.FACING).getAxis());
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        if(longAxis == TFMGUtils.rotateHorizontalAxis(getBlockState().getValue(HorizontalDirectionalBlock.FACING).getAxis())) {
            return 1;
        } else {
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
        waste.getHandler().setCapacity(CAPACITY_MULTIPLIER * blocks);
        creosote.getHandler().setCapacity(CAPACITY_MULTIPLIER * blocks);
    }

    @Override
    public IFluidTank getTank(int tank) {
        return creosote.getHandler();
    }

    @Override
    public FluidStack getFluid(int tank) {
        return creosote.getHandler().getFluid();
    }

    @Override
    public boolean hasInventory() {
        return true;
    }
}
