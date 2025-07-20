package it.bohdloss.tfmg.content.machinery.metallurgy.blast_furnace;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.base.TFMGFluidBehavior;
import it.bohdloss.tfmg.base.TFMGItemBehavior;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

import java.util.List;

@EventBusSubscriber
public class BlastFurnaceHatchBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    protected TFMGFluidBehavior tank;
    protected TFMGItemBehavior inventory;

    public BlastFurnaceHatchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = new TFMGFluidBehavior(TFMGFluidBehavior.TYPE, "Fluid", this, 4000)
                .allowInsertion(true)
                .allowExtraction(true)
                .syncCapacity(false)
                .withCallback(this::notifyUpdate);
        inventory = new TFMGItemBehavior(TFMGItemBehavior.TYPE, "Inventory", this, 1)
                .withStackSize(64)
                .allowInsertion(true)
                .allowExtraction(true)
                .withCallback(this::notifyUpdate);

        behaviours.add(tank);
        behaviours.add(inventory);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                TFMGBlockEntities.BLAST_FURNACE_HATCH.get(),
                (be, ctx) -> be.tank.getCapability()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TFMGBlockEntities.BLAST_FURNACE_HATCH.get(),
                (be, ctx) -> be.inventory.getCapability()
        );
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        TFMGUtils.createItemTooltip(this, tooltip, Direction.UP);
        return TFMGUtils.createFluidTooltip(this, tooltip, Direction.UP);
    }

    @Override
    public void remove() {
        super.remove();
        ItemHelper.dropContents(level, worldPosition, inventory.getHandler());
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if(level.isClientSide) {
            return;
        }

        dropItemsBelow();
    }

    public void dropItemsBelow(){
        if(level.getBlockState(getBlockPos().below()).isAir()) {
            Vec3 dropVec = VecHelper.getCenterOf(worldPosition).add(0, -12 / 16f, 0);
            ItemStack extract = inventory.getHandler().extractItem(0, inventory.stackSize, false).copy();
            ItemEntity dropped = new ItemEntity(level, dropVec.x, dropVec.y, dropVec.z, extract);
            dropped.setDefaultPickUpDelay();
            dropped.setDeltaMovement(0, -.25f, 0);
            level.addFreshEntity(dropped);
        }
    }
}
