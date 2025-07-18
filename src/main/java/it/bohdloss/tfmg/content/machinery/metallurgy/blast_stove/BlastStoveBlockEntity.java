package it.bohdloss.tfmg.content.machinery.metallurgy.blast_stove;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import it.bohdloss.tfmg.DebugStuff;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.base.AbstractMultiblock;
import it.bohdloss.tfmg.base.TFMGFluidBehavior;
import it.bohdloss.tfmg.base.TFMGRecipeBehavior;
import it.bohdloss.tfmg.base.TFMGRecipeWrapper;
import it.bohdloss.tfmg.recipes.HotBlastRecipe;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.List;

@EventBusSubscriber
public class BlastStoveBlockEntity extends AbstractMultiblock implements IHaveGoggleInformation {
    protected static final int CAPACITY_MULTIPLIER = 2000;

    protected TFMGFluidBehavior air;
    protected TFMGFluidBehavior fuel;
    protected TFMGFluidBehavior hotAir;
    protected TFMGFluidBehavior waste;
    protected CombinedTankWrapper allCaps;
    protected TFMGRecipeBehavior<TFMGRecipeWrapper, HotBlastRecipe> recipeExecutor;

    protected CombinedTankWrapper verticalCapability;
    protected CombinedTankWrapper horizontalCapability;

    protected TFMGRecipeWrapper input;

    public BlastStoveBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        air = new TFMGFluidBehavior(TFMGFluidBehavior.TYPE, "Air", this, CAPACITY_MULTIPLIER)
                .allowExtraction(true)
                .allowInsertion(true)
                .syncCapacity(true)
                .withCallback(() -> {
                    recipeExecutor.updateRecipe();
                    this.notifyUpdate();
                });
        fuel = new TFMGFluidBehavior(TFMGFluidBehavior.SECONDARY_TYPE, "Fuel", this, CAPACITY_MULTIPLIER)
                .allowExtraction(true)
                .allowInsertion(true)
                .syncCapacity(true)
                .withCallback(() -> {
                    recipeExecutor.updateRecipe();
                    this.notifyUpdate();
                });
        hotAir = new TFMGFluidBehavior(TFMGFluidBehavior.TERTIARY_TYPE, "HotAir", this, CAPACITY_MULTIPLIER)
                .allowExtraction(true)
                .allowInsertion(false)
                .syncCapacity(true)
                .withCallback(() -> {
                    recipeExecutor.updateRecipe();
                    this.notifyUpdate();
                });
        waste = new TFMGFluidBehavior(TFMGFluidBehavior.QUATERNARY_TYPE, "Waste", this, CAPACITY_MULTIPLIER)
                .allowExtraction(true)
                .allowInsertion(false)
                .syncCapacity(true)
                .withCallback(() -> {
                    recipeExecutor.updateRecipe();
                    this.notifyUpdate();
                });
        allCaps = new CombinedTankWrapper(
                air.getCapability(),
                fuel.getCapability(),
                hotAir.getCapability(),
                waste.getCapability()
        );
        input = new TFMGRecipeWrapper(null, new CombinedTankWrapper(air.getHandler(), fuel.getHandler()));
        recipeExecutor = new TFMGRecipeBehavior<TFMGRecipeWrapper, HotBlastRecipe>(this, TFMGRecipeTypes.HOT_BLAST.getType())
                .withInput(() -> input)
                .withDurationModifier(ticks -> (int) ((float) ticks / Math.max(width * width * height * 0.3f, 1f)))
                .withCheckFreeSpace(this::checkFreeSpace)
                .withResultsDo(this::acceptResults)
                .withCallback(this::notifyUpdate);

        verticalCapability = new CombinedTankWrapper(hotAir.getCapability(), fuel.getCapability());
        horizontalCapability = new CombinedTankWrapper(waste.getCapability(), air.getCapability());

        behaviours.add(air);
        behaviours.add(fuel);
        behaviours.add(hotAir);
        behaviours.add(waste);
        behaviours.add(recipeExecutor);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                TFMGBlockEntities.BLAST_STOVE.get(),
                (be, ctx) -> {
                    BlastStoveBlockEntity controllerStove = (BlastStoveBlockEntity) be.getControllerBE();
                    if(ctx == null) {
                        return controllerStove.allCaps;
                    }
                    if(ctx.getAxis() == Direction.Axis.Y) {
                        return controllerStove.verticalCapability;
                    } else if(be.getBlockPos().getY() == controllerStove.getBlockPos().getY()) {
                        return controllerStove.horizontalCapability;
                    } else {
                        return null;
                    }
                }
        );
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");

        BlastStoveBlockEntity controller = (BlastStoveBlockEntity) getControllerBE();

        if(controller != null) {
            FluidTank air = controller.air.getHandler();
            FluidTank fuel = controller.fuel.getHandler();
            FluidTank hotAir = controller.hotAir.getHandler();
            FluidTank waste = controller.waste.getHandler();
            CreateLang.translate("goggles.blast_stove.header")
                    .forGoggles(tooltip);

            int recipeDuration = controller.recipeExecutor.getRecipeDuration();
            if(controller.recipeExecutor.timer != -1 && recipeDuration != -1) {
                CreateLang.translate("goggles.coke_oven.progress", (recipeDuration - controller.recipeExecutor.timer) / 20 + " / " + recipeDuration / 20)
                        .style(ChatFormatting.GOLD)
                        .forGoggles(tooltip);
            }

            CreateLang.builder()
                    .add(CreateLang.translate("goggles.blast_stove.tank1"))
                    .add(CreateLang.number(air.getFluid().getAmount())
                            .add(mb)
                            .add(air.isEmpty() ? CreateLang.text("") : CreateLang.text(" " + air.getFluid().getHoverName().getString()))
                            .style(ChatFormatting.DARK_GREEN))
                    .text(ChatFormatting.GRAY, " / ")
                    .add(CreateLang.number(air.getCapacity())
                            .add(mb)
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
            CreateLang.builder()
                    .add(CreateLang.translate("goggles.blast_stove.tank2"))
                    .add(CreateLang.number(fuel.getFluid().getAmount())
                            .add(mb)
                            .add(fuel.isEmpty() ? CreateLang.text("") : CreateLang.text(" " + fuel.getFluid().getHoverName().getString()))
                            .style(ChatFormatting.DARK_GREEN))
                    .text(ChatFormatting.GRAY, " / ")
                    .add(CreateLang.number(fuel.getCapacity())
                            .add(mb)
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
            CreateLang.builder()
                    .add(CreateLang.translate("goggles.blast_stove.tank3"))
                    .add(CreateLang.number(hotAir.getFluid().getAmount())
                            .add(mb)
                            .add(hotAir.isEmpty() ? CreateLang.text("") : CreateLang.text(" " + hotAir.getFluid().getHoverName().getString()))
                            .style(ChatFormatting.YELLOW))
                    .text(ChatFormatting.GRAY, " / ")
                    .add(CreateLang.number(hotAir.getCapacity())
                            .add(mb)
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
            CreateLang.builder()
                    .add(CreateLang.translate("goggles.blast_stove.tank4"))
                    .add(CreateLang.number(waste.getFluid().getAmount())
                            .add(mb)
                            .add(waste.isEmpty() ? CreateLang.text("") : CreateLang.text(" " + waste.getFluid().getHoverName().getString()))
                            .style(ChatFormatting.YELLOW))
                    .text(ChatFormatting.GRAY, " / ")
                    .add(CreateLang.number(waste.getCapacity())
                            .add(mb)
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
        }
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if(!level.isClientSide && isController()) {
            recipeExecutor.update();
        }
    }

    protected void updateBlockstates() {
        if(level == null || level.isClientSide || !isController()) {
            return;
        }

        BlockPos controller = getBlockPos();

        switch (width) {
            case 1 -> {
                withAllBlocksDo(pos -> {
                    if(pos.getY() == controller.getY()) {
                        TFMGUtils.changeProperty(level, pos, BlastStoveBlock.SHAPE, BlastStoveBlock.Shape.BOTTOM);
                    } else {
                        TFMGUtils.changeProperty(level, pos, BlastStoveBlock.SHAPE, BlastStoveBlock.Shape.TOP);
                    }
                });
            }
            case 2 -> {
                withAllBlocksDo(pos -> {
                    if(pos.getY() == controller.getY()) {
                        TFMGUtils.changeProperty(level, pos, BlastStoveBlock.SHAPE, BlastStoveBlock.Shape.BOTTOM_CONNECTED);
                    } else {
                        TFMGUtils.changeProperty(level, pos, BlastStoveBlock.SHAPE, BlastStoveBlock.Shape.TOP_CONNECTED);
                    }
                });
            }
            default -> {}
        }
    }

    protected boolean checkFreeSpace(Pair<List<ItemStack>, List<FluidStack>> results) {
        // We know hot blast recipes have 2 outputs
        FluidStack primary = results.getSecond().getFirst();
        FluidStack secondary = results.getSecond().get(1);

        return hotAir.getHandler().getSpace() >= primary.getAmount() &&
                (hotAir.getHandler().isEmpty() || hotAir.getHandler().getFluid().getFluid().isSame(primary.getFluid())) &&
                waste.getHandler().getSpace() >= secondary.getAmount() &&
                (waste.getHandler().isEmpty() || waste.getHandler().getFluid().getFluid().isSame(secondary.getFluid()));
    }

    protected void acceptResults(Pair<List<ItemStack>, List<FluidStack>> results) {
        FluidStack primaryFluid = results.getSecond().getFirst();
        FluidStack secondaryFluid = results.getSecond().get(1);

        hotAir.getHandler().fill(primaryFluid, IFluidHandler.FluidAction.EXECUTE);
        waste.getHandler().fill(secondaryFluid, IFluidHandler.FluidAction.EXECUTE);
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

    @Override
    protected BlockState rotateBlockToMatch(BlockState current, BlockState controller) {
        return controller;
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return Direction.Axis.Y;
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        if(longAxis == Direction.Axis.Y) {
            return AllConfigs.server().fluids.fluidTankMaxHeight.get();
        } else {
            return getMaxWidth();
        }
    }

    @Override
    public int getMaxWidth() {
        return 2;
    }

    @Override
    public boolean hasTank() {
        return true;
    }

    @Override
    public int getTankSize(int tank) {
        return width * width * height;
    }

    @Override
    public void setTankSize(int tank, int blocks) {
        air.getHandler().setCapacity(CAPACITY_MULTIPLIER * blocks);
        fuel.getHandler().setCapacity(CAPACITY_MULTIPLIER * blocks);
        hotAir.getHandler().setCapacity(CAPACITY_MULTIPLIER * blocks);
        waste.getHandler().setCapacity(CAPACITY_MULTIPLIER * blocks);
    }

    @Override
    public IFluidTank getTank(int tank) {
        return hotAir.getHandler();
    }

    @Override
    public FluidStack getFluid(int tank) {
        return hotAir.getHandler().getFluid();
    }

    @Override
    public boolean hasInventory() {
        return false;
    }
}
