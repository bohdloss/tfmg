package it.bohdloss.tfmg.content.machinery.misc.winding_machine;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.DebugStuff;
import it.bohdloss.tfmg.base.IWind;
import it.bohdloss.tfmg.base.TFMGItemBehavior;
import it.bohdloss.tfmg.base.TFMGRecipeBehavior;
import it.bohdloss.tfmg.base.TFMGRecipeWrapper;
import it.bohdloss.tfmg.recipes.WindingRecipe;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import java.util.List;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

@EventBusSubscriber
public class WindingMachineBlockEntity extends KineticBlockEntity {
    protected TFMGItemBehavior item;
    protected TFMGItemBehavior spool;
    protected CombinedInvWrapper allCaps;
    protected TFMGRecipeWrapper input;

    protected TFMGRecipeBehavior<TFMGRecipeWrapper, WindingRecipe> recipeExecutor;

    protected ScrollValueBehaviour turnPercentage;

    // Client data
    protected float angle;
    protected LerpedFloat lerpedAngle = LerpedFloat.angular();

    public WindingMachineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);

        item = new TFMGItemBehavior(TFMGItemBehavior.TYPE, "Item", this, 1)
                .withStackSize(1)
                .allowInsertion(true)
                .allowExtraction(true)
                .withCallback(this::onIOChange);
        spool = new TFMGItemBehavior(TFMGItemBehavior.SECONDARY_TYPE, "Spool", this, 1)
                .withValidator((slot, item) -> item.getItem() instanceof IWind)
                .withStackSize(1)
                .allowInsertion(true)
                .allowExtraction(true)
                .withCallback(this::onIOChange);
        allCaps = new CombinedInvWrapper(item.getCapability(), spool.getCapability());
        input = new TFMGRecipeWrapper(new CombinedInvWrapper(item.getHandler(), spool.getHandler()), null);

        recipeExecutor = new TFMGRecipeBehavior<TFMGRecipeWrapper, WindingRecipe>(this, TFMGRecipeTypes.WINDING.getType())
                .withInput(() -> input)
                .withAdditionalIngredientCheck(this::hasIngredients)
                .withCheckFreeSpace(this::checkFreeSpace)
                .withAdditionalInputConsumption(this::consumeItems)
                .withResultsDo(this::acceptResults)
                .withDurationModifier(this::calculateWindings)
                .withCallback(this::notifyUpdate);
        recipeExecutor.overrideItemConsumption = true;

        turnPercentage = new ScrollValueBehaviour(CreateLang.translateDirect("winding_machine.turn_percentage"),
                this, new WindingMachineValueBox()).withCallback(x -> notifyUpdate());
        turnPercentage.between(1, 100);
        turnPercentage.value = 20;

        behaviours.add(item);
        behaviours.add(spool);
        behaviours.add(recipeExecutor);
        behaviours.add(turnPercentage);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TFMGBlockEntities.WINDING_MACHINE.get(),
                (be, ctx) -> {
                    if(ctx == null) {
                        return be.allCaps;
                    }

                    if(ctx.getAxis() == Direction.Axis.Y) {
                        return be.spool.getCapability();
                    }

                    return be.item.getCapability();
                }
        );
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.translate("goggles.winding_machine.header")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 1);

        ItemStack spoolItem = spool.getHandler().getStackInSlot(0);
        if (!spoolItem.isEmpty()) {
            IWind spoolWind = (IWind) spoolItem.getItem();
            CreateLang.text(spoolItem.getDisplayName().getString().replace("[","").replace("]",""))
                    .color(spoolWind.getRenderedColor(spoolItem))
                    .forGoggles(tooltip);
            CreateLang.translate("goggles.winding_machine.turns")
                    .add(CreateLang.number(spoolWind.getWindings(spoolItem) - getAmountWinded()))
                    .color(spoolWind.getRenderedColor(spoolItem))
                    .forGoggles(tooltip);

            if (recipeExecutor.timer != -1) {
                CreateLang.text("")
                        .add(CreateLang.translate("goggles.winding_machine.progress"))
                        .add(CreateLang.number(getAmountWinded()))
                        .add(Component.literal("/" + calculateWindings(recipeExecutor.getRecipe().value().getProcessingDuration())))
                        .color(spoolWind.getRenderedColor(spoolItem))
                        .forGoggles(tooltip);
            }
        }
        return true;
    }

    protected void onIOChange() {
        recipeExecutor.updateRecipe();
        notifyUpdate();
    }

    protected int calculateWindings(int duration) {
        float percent = (float) turnPercentage.getValue() / 100f;
        return Math.max((int) ((float) duration * percent), 1);
    }

    protected boolean hasIngredients(TFMGRecipeWrapper input, WindingRecipe recipe) {
        int necessaryWindings = calculateWindings(recipe.getProcessingDuration());
        ItemStack spoolItem = spool.getHandler().getStackInSlot(0);
        IWind wind = (IWind) spoolItem.getItem();
        return !spoolItem.isEmpty() && wind.getWindings(spoolItem) >= necessaryWindings;
    }

    protected boolean checkFreeSpace(List<ItemStack> items, List<FluidStack> fluids) {
        return true;
    }

    protected void consumeItems(TFMGRecipeWrapper input, WindingRecipe recipe) {
        int necessaryWindings = calculateWindings(recipe.getProcessingDuration());
        ItemStack spoolItem = spool.getHandler().getStackInSlot(0);
        IWind spoolWind = (IWind) spoolItem.getItem();
        int spoolWindings = spoolWind.getWindings(spoolItem);
        spoolWind.setWindings(spoolItem, spoolWindings - necessaryWindings);
        spool.getHandler().setStackInSlot(0, spoolItem);
    }

    protected void acceptResults(List<ItemStack> items, List<FluidStack> fluids) {
        int necessaryWindings = calculateWindings(recipeExecutor.getRecipe().value().getProcessingDuration());
        ItemStack result = items.getFirst();
        IWind resultWind = (IWind) result.getItem();
        resultWind.setWindings(result, necessaryWindings);
        item.getHandler().setStackInSlot(0, result);
    }

    public int getAmountWinded() {
        if(recipeExecutor.timer == -1) {
            return 0;
        }
        float percent = ((float) recipeExecutor.timer / (float) recipeExecutor.getRecipeDuration());
        float windings = calculateWindings(recipeExecutor.getRecipe().value().getProcessingDuration());
        return (int) (windings - (windings * percent));
    }

    @Override
    public void tick() {
        super.tick();

        if(level.isClientSide) {
            float targetSpeed = (float) Math.min(Math.abs(getSpeed() * 1.5), 32);
            angle += targetSpeed;
            angle %= 360;
            lerpedAngle.chase(angle, 1, LerpedFloat.Chaser.EXP);
            lerpedAngle.tickChaser();
        }

        if(!level.isClientSide) {
            if(speed != 0) {
                recipeExecutor.update();
            }
        }
    }

    public static class WindingMachineValueBox extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 4, 16.05);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction == state.getValue(HORIZONTAL_FACING);
        }
    }
}
