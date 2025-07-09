package it.bohdloss.tfmg.content.engines;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.DebugStuff;
import it.bohdloss.tfmg.content.items.FluidAmount;
import it.bohdloss.tfmg.registry.TFMGDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public abstract class FluidContainingItem extends Item {
    public final Supplier<Fluid> fluidType;
    public final int capacity;

    public FluidContainingItem(Properties properties, Supplier<Fluid> fluidType, int capacity) {
        super(properties);
        this.fluidType = fluidType;
        this.capacity = capacity;
    }

    public ItemWrapper fluidHandler(ItemStack itemStack) {
        int fluid = itemStack.getOrDefault(TFMGDataComponents.FLUID_AMOUNT, FluidAmount.DEFAULT).value();
        return new ItemWrapper(itemStack, fluidType, capacity, fluid);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        IFluidHandlerItem cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if(cap != null) {
            tooltipComponents.add(CreateLang.translateDirect("tooltip.fluid_item", cap.getFluidInTank(0).getAmount())
                    .withStyle(ChatFormatting.GREEN)
            );
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        IFluidHandlerItem cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
        Player player = context.getPlayer();

        if(cap == null || player == null || player.isCreative()) {
            return InteractionResult.PASS;
        }

        if (level.getBlockEntity(pos) != null)

            if (level.getBlockEntity(pos) instanceof FluidTankBlockEntity fluidTankBe) {
                FluidTankBlockEntity be = fluidTankBe.isController() ? fluidTankBe : fluidTankBe.getControllerBE();

                if (be.getTankInventory().getFluid().getFluid().equals(fluidType.get()) || be.getTankInventory().getFluid().isEmpty()) {
                    if(context.getPlayer().isCrouching()) {
                        int toFill = cap.drain(be.getTankInventory().getCapacity() - be.getTankInventory().getFluidAmount(), IFluidHandler.FluidAction.SIMULATE).getAmount();

                        if(toFill == 0 || context.getPlayer().getCooldowns().isOnCooldown(stack.getItem())) {
                            return InteractionResult.PASS;
                        }

                        level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
                        FluidStack drained = cap.drain(toFill, IFluidHandler.FluidAction.EXECUTE);
                        be.getTankInventory().fill(drained, IFluidHandler.FluidAction.EXECUTE);
                        context.getPlayer().getCooldowns().addCooldown(stack.getItem(), 20);

                        return InteractionResult.SUCCESS;
                    } else {
                        int toDrain = cap.fill(be.getTankInventory().getFluid(), IFluidHandler.FluidAction.SIMULATE);

                        if(toDrain == 0 || context.getPlayer().getCooldowns().isOnCooldown(stack.getItem())) {
                            return InteractionResult.PASS;
                        }

                        level.playSound(null, be.getBlockPos(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
                        FluidStack drained = be.getTankInventory().drain(toDrain, IFluidHandler.FluidAction.EXECUTE);
                        cap.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                        context.getPlayer().getCooldowns().addCooldown(stack.getItem(), 20);

                        return InteractionResult.SUCCESS;
                    }

                }
            }

        return InteractionResult.PASS;
    }

    public static class ItemWrapper implements IFluidHandlerItem {
        private final ItemStack itemStack;
        private final Supplier<Fluid> fluidType;
        private final int capacity;

        private int fluid;

        private ItemWrapper(ItemStack itemStack, Supplier<Fluid> fluidType, int capacity, int fluid) {
            this.itemStack = itemStack;
            this.fluidType = fluidType;
            this.capacity = capacity;
            this.fluid = fluid;
        }

        @Override
        public @NotNull ItemStack getContainer() {
            return itemStack;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return fluid == 0 ? FluidStack.EMPTY : new FluidStack(fluidType.get(), fluid);
        }

        @Override
        public int getTankCapacity(int tank) {
            return capacity;
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return stack.getFluid().equals(fluidType.get());
        }

        @Override
        public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
            if(resource.isEmpty()) {
                return 0;
            }
            int maxFill = Math.min(resource.getAmount(), capacity - fluid);

            if(action.execute()) {
                this.fluid += maxFill;
                itemStack.set(TFMGDataComponents.FLUID_AMOUNT, new FluidAmount(this.fluid));
            }

            return maxFill;
        }

        @Override
        public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
            if(!resource.getFluid().equals(fluidType.get())) {
                return FluidStack.EMPTY;
            }

            return drain(resource.getAmount(), action);
        }

        @Override
        public @NotNull FluidStack drain(int max, @NotNull FluidAction action) {
            if(max == 0) {
                return FluidStack.EMPTY;
            }
            int maxDrain = Math.min(max, fluid);

            if(action.execute()) {
                fluid -= maxDrain;
                itemStack.set(TFMGDataComponents.FLUID_AMOUNT, new FluidAmount(this.fluid));
            }

            return new FluidStack(fluidType.get(), maxDrain);
        }
    }
}
