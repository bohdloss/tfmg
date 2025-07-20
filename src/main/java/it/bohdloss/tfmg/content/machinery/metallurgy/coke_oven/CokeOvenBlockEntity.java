package it.bohdloss.tfmg.content.machinery.metallurgy.coke_oven;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.base.*;
import it.bohdloss.tfmg.config.TFMGConfigs;
import it.bohdloss.tfmg.recipes.CokingRecipe;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
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

import java.util.List;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

@EventBusSubscriber
public class CokeOvenBlockEntity extends AbstractMultiblock implements IHaveGoggleInformation {
    protected static final int CAPACITY_MULTIPLIER = 4000;

    public TFMGFluidBehavior creosote; // These names refer to the default coking recipe. There may be different contents.
    public TFMGFluidBehavior waste;
    public CombinedTankWrapper allCaps;
    public TFMGItemBehavior inventory;
    public TFMGRecipeBehavior<TFMGRecipeWrapper, CokingRecipe> recipeExecutor;

    public LerpedFloat doorAngle = LerpedFloat.angular();
    public boolean forceOpen;

    public TFMGRecipeWrapper input;

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
                    if(ctx == null) {
                        return cokeOven.allCaps;
                    }
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
                    recipeExecutor.updateRecipe();
                });
        creosote = new TFMGFluidBehavior(TFMGFluidBehavior.TYPE, "PrimaryFluid", this, CAPACITY_MULTIPLIER)
                .allowExtraction(true)
                .allowInsertion(false)
                .syncCapacity(true)
                .withCallback(() -> {
                    notifyUpdate();
                    recipeExecutor.updateRecipe();
                });
        allCaps = new CombinedTankWrapper(waste.getCapability(), creosote.getCapability());
        inventory = new TFMGItemBehavior(TFMGItemBehavior.TYPE, "Inventory", this, 1)
                .allowExtraction(true)
                .allowInsertion(true)
                .withCallback(() -> {
                    notifyUpdate();
                    recipeExecutor.updateRecipe();
                });
        input = new TFMGRecipeWrapper(inventory.getHandler(), null);
        recipeExecutor = new TFMGRecipeBehavior<TFMGRecipeWrapper, CokingRecipe>(this, TFMGRecipeTypes.COKING.getType())
                .withInput(() -> input)
                .withDurationModifier(ticks -> ticks / Math.max(width / 2, 1))
                .withCheckFreeSpace(this::checkFreeSpace)
                .withResultsDo(this::acceptResults)
                .withCallback(this::notifyUpdate);

        behaviours.add(waste);
        behaviours.add(creosote);
        behaviours.add(inventory);
        behaviours.add(recipeExecutor);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.translate("goggles.coke_oven.header")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);

        CokeOvenBlockEntity controller = (CokeOvenBlockEntity) getControllerBE();
        if(controller != null) {
            int recipeDuration = controller.recipeExecutor.getRecipeDuration();
            if(controller.recipeExecutor.timer != -1 && recipeDuration != -1) {
                CreateLang.translate("goggles.coke_oven.progress", (recipeDuration - controller.recipeExecutor.timer) / 20 + " / " + recipeDuration / 20)
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
            int timer = recipeExecutor.timer;
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
            recipeExecutor.update();
        }
    }

    protected boolean checkFreeSpace(List<ItemStack> items, List<FluidStack> fluids) {
        // We know coking recipes have 2 outputs
        FluidStack primary = fluids.getFirst();
        FluidStack secondary = fluids.size() < 2 ? null : fluids.get(1);

        boolean fits = creosote.getHandler().getSpace() >= primary.getAmount() &&
                (creosote.getHandler().isEmpty() || creosote.getHandler().getFluid().getFluid().isSame(primary.getFluid()));

        if(secondary != null) {
            fits &= waste.getHandler().getSpace() >= secondary.getAmount() &&
                    (waste.getHandler().isEmpty() || waste.getHandler().getFluid().getFluid().isSame(secondary.getFluid()));
        }
        return fits;
    }

    protected void acceptResults(List<ItemStack> items, List<FluidStack> fluids) {
        ItemStack onlyItem = items.getFirst();
        FluidStack primaryFluid = fluids.getFirst();
        FluidStack secondaryFluid = fluids.size() < 2 ? null: fluids.get(1);

        creosote.getHandler().fill(primaryFluid, IFluidHandler.FluidAction.EXECUTE);
        if(secondaryFluid != null) {
            waste.getHandler().fill(secondaryFluid, IFluidHandler.FluidAction.EXECUTE);
        }

        Direction direction = getBlockState().getValue(FACING);

        Vec3 dropVec = VecHelper.getCenterOf(getLowestDoorPos().relative(direction)).add(0, 0.4, 0);
        ItemEntity dropped = new ItemEntity(level, dropVec.x, dropVec.y, dropVec.z, onlyItem);
        dropped.setDefaultPickUpDelay();
        dropped.setDeltaMovement(direction.getAxis() == Direction.Axis.X ? direction == Direction.WEST ? -.01f : .01f : 0, 0.05f, direction.getAxis() == Direction.Axis.Z ? direction == Direction.NORTH ? -.01f : .01f : 0);
        level.addFreshEntity(dropped);
    }

    @Override
    protected void onMultiblockChange() {
        updateBlockstates();
        if(!level.isClientSide && isController()) {
            recipeExecutor.reset();
            recipeExecutor.updateRecipe();
            notifyUpdate();
        }
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
            case 0 -> {}
            case 1 -> {
                TFMGUtils.changeProperty(level, column, CokeOvenBlock.CONTROLLER_TYPE, CokeOvenBlock.ControllerType.CASUAL);
            }
            default -> {
                TFMGUtils.changeProperty(level, column, CokeOvenBlock.CONTROLLER_TYPE, CokeOvenBlock.ControllerType.BOTTOM_ON);
                TFMGUtils.changeProperty(level, column.above(width - 1), CokeOvenBlock.CONTROLLER_TYPE, CokeOvenBlock.ControllerType.TOP_ON);

                BlockPos.MutableBlockPos columnMut = column.mutable();
                for(int i = column.getY() + 1; i < column.getY() + width - 1; i++) {
                    columnMut.setY(i);
                    TFMGUtils.changeProperty(level, columnMut, CokeOvenBlock.CONTROLLER_TYPE, CokeOvenBlock.ControllerType.MIDDLE_ON);
                }
            }
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
        return TFMGConfigs.common().machines.cokeOvenMaxSize.get();
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
