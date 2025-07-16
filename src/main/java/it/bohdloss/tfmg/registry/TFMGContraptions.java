package it.bohdloss.tfmg.registry;

import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateRegistries;
import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.hammer.PumpjackContraption;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TFMGContraptions {
    public static final DeferredRegister<ContraptionType> CONTRAPTIONS = DeferredRegister.create(CreateRegistries.CONTRAPTION_TYPE, TFMG.MOD_ID);
    public static final DeferredHolder<ContraptionType, ContraptionType>
            PUMPJACK_CONTRAPTION = CONTRAPTIONS.register("pumpjack", () -> new ContraptionType(PumpjackContraption::new));

    public static void register(IEventBus eventBus) {
        CONTRAPTIONS.register(eventBus);
    }
}
