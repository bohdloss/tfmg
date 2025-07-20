package it.bohdloss.tfmg.content.electricity.debug;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import it.bohdloss.tfmg.DebugStuff;
import it.bohdloss.tfmg.base.AbstractKineticMultiblock;
import it.bohdloss.tfmg.base.AbstractMultiblock;
import it.bohdloss.tfmg.content.machinery.metallurgy.blast_furnace.BlastFurnaceOutputBlockEntity;
import it.bohdloss.tfmg.content.machinery.metallurgy.coke_oven.CokeOvenBlockEntity;
import it.bohdloss.tfmg.content.machinery.misc.air_intake.AirIntakeBlockEntity;
import it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.hammer.PumpjackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class DebugCinderBlockItem extends Item {
    public DebugCinderBlockItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
//        if (level.getBlockEntity(pos) instanceof IElectric be) {
//            if(level.isClientSide()) {
//                DebugStuff.show("VOLTAGE {}", be.getData().voltage);
//            } else {
//                TFMG.LOGGER.debug("VOLTAGE {}", be.getData().voltage);
//            }
//        }


//        if(!level.isClientSide && level.getBlockEntity(pos) instanceof BlastFurnaceOutputBlockEntity be) {
//            IItemHandlerModifiable handler = be.flux.getHandler();
//            DebugStuff.show("HANDLER:");
//            int slots = handler.getSlots();
//            DebugStuff.show("Slots: " + slots);
//            for(int i = 0; i < slots; i++) {
//                DebugStuff.show("Item in slot " + i + ": " + handler.getStackInSlot(i));
//            }
//
//            IItemHandlerModifiable managed = be.flux.getManagedHandler();
//            DebugStuff.show("MANAGED:");
//            int slotsmanaged = managed.getSlots();
//            DebugStuff.show("Slots: " + slotsmanaged);
//            for(int i = 0; i < slotsmanaged; i++) {
//                DebugStuff.show("Item in slot " + i + ": " + managed.getStackInSlot(i));
//            }
//
//            IItemHandlerModifiable cap = be.flux.getCapability();
//            DebugStuff.show("CAPABILITY:");
//            int capslots = cap.getSlots();
//            DebugStuff.show("Slots: " + capslots);
//            for(int i = 0; i < capslots; i++) {
//                DebugStuff.show("Item in slot " + i + ": " + cap.getStackInSlot(i));
//            }
//        }
        if(level.getBlockEntity(pos) instanceof BlastFurnaceOutputBlockEntity be) {
            DebugStuff.show((level.isClientSide ? "Client" : "Server") + " -> " + be.fuelConsumeTimer);
        }else if(!level.isClientSide && level.getBlockEntity(pos) instanceof PumpjackBlockEntity be) {
            BlockPos anchorPos = be.getBlockPos().above();
            BlockPos connector = be.findConnector(anchorPos);
            DebugStuff.show("Connector found: " + connector);
            if(connector != null) {
                BlockPos crank = be.findCrank(connector);
                DebugStuff.show("From connector found crank: " + crank);
            }
            BlockPos head = be.findHead(anchorPos);
            DebugStuff.show("Head found: " + head);
            if(head != null) {
                BlockPos base = be.findBase(head);
                DebugStuff.show("From head found base: " + base);
            }
            DebugStuff.show("CURRENT DATA: connector: " + be.connectorPosition + " head: " + be.headPosition + " base: " + be.basePosition + " crank: " + be.crankPosition);
        } else if(!level.isClientSide && level.getBlockEntity(pos) instanceof AbstractKineticMultiblock be) {
            DebugStuff.show("is controller: " + be.isController() + "; width: " + be.getWidth() + " height: " + be.getHeight());
        } else if(!level.isClientSide && level.getBlockEntity(pos) instanceof AbstractMultiblock be) {
            DebugStuff.show("is controller: " + be.isController() + "; width: " + be.getWidth() + " height: " + be.getHeight());
        } else if(!level.isClientSide && level.getBlockEntity(pos) instanceof IMultiBlockEntityContainer be) {
            DebugStuff.show("is controller: " + be.isController() + "; width: " + be.getWidth() + " height: " + be.getHeight());
        }

        return InteractionResult.SUCCESS;
    }
}
