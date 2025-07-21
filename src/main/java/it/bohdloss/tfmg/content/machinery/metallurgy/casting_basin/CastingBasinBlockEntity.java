package it.bohdloss.tfmg.content.machinery.metallurgy.casting_basin;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.base.TFMGFluidBehavior;
import it.bohdloss.tfmg.base.TFMGItemBehavior;
import it.bohdloss.tfmg.base.TFMGRecipeBehavior;
import it.bohdloss.tfmg.base.TFMGRecipeWrapper;
import it.bohdloss.tfmg.recipes.CastingRecipe;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

@EventBusSubscriber
public class CastingBasinBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    protected TFMGFluidBehavior fluid;
    protected TFMGItemBehavior item;
    protected TFMGRecipeBehavior<TFMGRecipeWrapper, CastingRecipe> recipeExecutor;
    protected TFMGRecipeWrapper input;
    protected int dropTimer;

    // Rendering stuff
    protected int lastFluidAmount = -1;
    protected int flowTimer;
    protected LerpedFloat fluidLevel = LerpedFloat.linear();

    public CastingBasinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        fluid = new TFMGFluidBehavior(TFMGFluidBehavior.TYPE, "Fluid", this, 200)
                .allowExtraction(true)
                .allowInsertion(true)
                .syncCapacity(false)
                .withCallback(this::onIOChange);
        item = new TFMGItemBehavior(TFMGItemBehavior.TYPE, "Output", this, 1)
                .withStackSize(1)
                .allowExtraction(true)
                .allowInsertion(false)
                .withStackSize(64)
                .withCallback(this::onIOChange);
        input = new TFMGRecipeWrapper(null, fluid.getHandler());
        recipeExecutor = new TFMGRecipeBehavior<TFMGRecipeWrapper, CastingRecipe>(this, TFMGRecipeTypes.CASTING.getType())
                .withInput(() -> input)
                .withCheckFreeSpace(this::checkFreeSpace)
                .withResultsDo(this::acceptResults)
                .withCallback(this::notifyUpdate);

        behaviours.add(fluid);
        behaviours.add(item);
        behaviours.add(recipeExecutor);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        TFMGUtils.createFluidTooltip(this, tooltip, null);
        TFMGUtils.createItemTooltip(this, tooltip, null);
        return true;
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                TFMGBlockEntities.CASTING_BASIN.get(),
                (be, ctx) -> {
                    if(ctx == null || ctx == be.getBlockState().getValue(FACING).getOpposite()) {
                        return be.fluid.getCapability();
                    }
                    return null;
                }
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TFMGBlockEntities.CASTING_BASIN.get(),
                (be, ctx) -> be.item.getCapability()
        );
    }

    protected boolean checkFreeSpace(List<ItemStack> items, List<FluidStack> fluids) {
        ItemStack primary = items.getFirst();
        ItemStack extra = item.getHandler().insertItem(0, primary, true);
        return extra.isEmpty();
    }

    protected void acceptResults(List<ItemStack> items, List<FluidStack> fluids) {
        dropTimer = 20;

        ItemStack primary = items.getFirst();
        item.getHandler().insertItem(0, primary, false);
    }

    protected void onIOChange() {
        recipeExecutor.updateRecipe();
        notifyUpdate();
    }

    @Override
    public void tick() {
        super.tick();

        if(level.isClientSide){
            int fluidAmount = fluid.getHandler().getFluidAmount();
            if(lastFluidAmount != fluidAmount) {
                lastFluidAmount = fluidAmount;
                flowTimer = 10;
            }

            if(flowTimer > 0) {
                flowTimer--;
            }

            fluidLevel.chase(Math.min(fluidAmount, 144), 0.3f, LerpedFloat.Chaser.EXP);
            fluidLevel.tickChaser();
        }

        if(!level.isClientSide) {
            dropTimer = Math.max(0, dropTimer - 1);
            if(dropTimer == 0 && !item.getHandler().getStackInSlot(0).isEmpty()) {
                Vec3 dropVec = VecHelper.getCenterOf(worldPosition).add(0, 4f / 16f, 0);
                ItemEntity dropped = new ItemEntity(level, dropVec.x, dropVec.y, dropVec.z, item.getHandler().extractItem(0, 1, false).copy());
                dropped.setDefaultPickUpDelay();
                dropped.setDeltaMovement(0, .2f, 0);
                level.addFreshEntity(dropped);
            }
            recipeExecutor.update();
        }
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        tag.putInt("DropTimer", dropTimer);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        dropTimer = tag.getInt("DropTimer");
    }
}
