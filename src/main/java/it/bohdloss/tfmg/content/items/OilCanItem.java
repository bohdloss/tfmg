package it.bohdloss.tfmg.content.items;

import it.bohdloss.tfmg.content.engines.FluidContainingItem;
import it.bohdloss.tfmg.registry.TFMGFluids;
import it.bohdloss.tfmg.registry.TFMGItems;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber
public class OilCanItem extends FluidContainingItem {
    public OilCanItem(Properties properties) {
        super(properties, TFMGFluids.LUBRICATION_OIL::getSource, 4000);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.FluidHandler.ITEM, (itemStack, ctx) -> {
            FluidContainingItem item = (FluidContainingItem) itemStack.getItem();
            return item.fluidHandler(itemStack);
        }, TFMGItems.OIL_CAN.get());
    }
}
