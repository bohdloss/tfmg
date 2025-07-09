package it.bohdloss.tfmg.content.items;

import it.bohdloss.tfmg.content.engines.FluidContainingItem;
import it.bohdloss.tfmg.registry.TFMGFluids;
import it.bohdloss.tfmg.registry.TFMGItems;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber
public class CoolingFluidItem extends FluidContainingItem {
    public CoolingFluidItem(Properties properties) {
        super(properties, TFMGFluids.COOLING_FLUID::getSource, 4000);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.FluidHandler.ITEM, (itemStack, ctx) -> {
            FluidContainingItem item = (FluidContainingItem) itemStack.getItem();
            return item.fluidHandler(itemStack);
        }, TFMGItems.COOLING_FLUID_BOTTLE.get());
    }
}
