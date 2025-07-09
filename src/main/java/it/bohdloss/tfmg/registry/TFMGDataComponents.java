package it.bohdloss.tfmg.registry;

import it.bohdloss.tfmg.content.electricity.utilities.fuse_block.AmpRating;
import it.bohdloss.tfmg.content.electricity.utilities.resistor.Resistance;
import it.bohdloss.tfmg.content.electricity.connection.Windings;
import it.bohdloss.tfmg.content.electricity.connection.WireSelection;
import it.bohdloss.tfmg.content.items.FluidAmount;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static it.bohdloss.tfmg.TFMG.MOD_ID;

public class TFMGDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Resistance>> RESISTANCE = DATA_COMPONENTS.registerComponentType(
            "resistance",
            builder -> builder.persistent(Resistance.CODEC).networkSynchronized(Resistance.STREAM_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Windings>> WINDINGS = DATA_COMPONENTS.registerComponentType(
            "windings",
            builder -> builder.persistent(Windings.CODEC).networkSynchronized(Windings.STREAM_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WireSelection>> WIRE_SELECTION = DATA_COMPONENTS.registerComponentType(
            "wire_selection",
            builder -> builder.persistent(WireSelection.CODEC).networkSynchronized(WireSelection.STREAM_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AmpRating>> AMP_RATING = DATA_COMPONENTS.registerComponentType(
            "amp_rating",
            builder -> builder.persistent(AmpRating.CODEC).networkSynchronized(AmpRating.STREAM_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FluidAmount>> FLUID_AMOUNT = DATA_COMPONENTS.registerComponentType(
            "fluid_amount",
            builder -> builder.persistent(FluidAmount.CODEC).networkSynchronized(FluidAmount.STREAM_CODEC)
    );

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}
