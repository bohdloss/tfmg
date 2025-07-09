package it.bohdloss.tfmg.registry;

import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.VirtualFluidBuilder;
import com.tterrag.registrate.builders.FluidBuilder;
import it.bohdloss.tfmg.TFMG;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;

import static it.bohdloss.tfmg.registry.TFMGFluids.getGasTexture;

// TODO
public class TFMGRegistrate extends CreateRegistrate {
    public static String autoLang(String id) {
        StringBuilder builder = new StringBuilder();
        boolean b = true;
        for (char c: id.toCharArray()) {
            if(c == '_') {
                builder.append(' ');
                b = true;
            } else {
                builder.append(b ? String.valueOf(c).toUpperCase() : c);
                b = false;
            }
        }
        return builder.toString();
    }

    public FluidBuilder<VirtualFluid, CreateRegistrate> gasFluid(String name, int color) {
        return entry(name, c -> new VirtualFluidBuilder<>(self(),self(), name, c, getGasTexture(), getGasTexture(),
                TFMGFluids.GasFluidType.create(color),VirtualFluid::createSource,VirtualFluid::createFlowing));
    }

    protected TFMGRegistrate() {
        super(TFMG.MOD_ID);
    }

    public static TFMGRegistrate create() {
        return (TFMGRegistrate) new TFMGRegistrate().setTooltipModifierFactory(item ->
                new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                        .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
        );
    }

//    public static Block getBlock(String name) {
//        return TFMG.REGISTRATE.get(name, BuiltInRegistries.BLOCK.getRegistryKey()).get();
//    }
//    public static Item getItem(String name) {
//        return TFMG.REGISTRATE.get(name, BuiltInRegistries.ITEM.getRegistryKey()).get();
//    }
//    public static Item getBucket(String name) {
//        return TFMG.REGISTRATE.get(name+"_bucket", BuiltInRegistries.ITEM.getRegistryKey()).get();
//    }

//    public <T extends CableType> CableTypeBuilder<T, TFMGRegistrate> cableType(NonNullFunction<CableType.Properties, T> factory) {
//        return cableType((TFMGRegistrate) self(), factory);
//    }
//
//    public <T extends CableType> CableTypeBuilder<T, TFMGRegistrate> cableType(String name, NonNullFunction<CableType.Properties, T> factory) {
//        return cableType((TFMGRegistrate) self(), name, factory);
//    }
//
//    public <T extends CableType, P> CableTypeBuilder<T, P> cableType(P parent, NonNullFunction<CableType.Properties, T> factory) {
//        return cableType(parent, currentName(), factory);
//    }
//
//    public <T extends CableType, P> CableTypeBuilder<T, P> cableType(P parent, String name, NonNullFunction<CableType.Properties, T> factory) {
//        return entry(name, callback -> CableTypeBuilder.create(this, parent, name, callback, factory));
//    }
//
//    public <T extends Electrode> ElectrodeBuilder<T, TFMGRegistrate> electrode(NonNullFunction<Electrode.Properties, T> factory) {
//        return electrode((TFMGRegistrate) self(), factory);
//    }
//
//    public <T extends Electrode> ElectrodeBuilder<T, TFMGRegistrate> electrode(String name, NonNullFunction<Electrode.Properties, T> factory) {
//        return electrode((TFMGRegistrate) self(), name, factory);
//    }
//
//    public <T extends Electrode, P> ElectrodeBuilder<T, P> electrode(P parent, NonNullFunction<Electrode.Properties, T> factory) {
//        return electrode(parent, currentName(), factory);
//    }
//
//    public <T extends Electrode, P> ElectrodeBuilder<T, P> electrode(P parent, String name, NonNullFunction<Electrode.Properties, T> factory) {
//        return entry(name, callback -> ElectrodeBuilder.create(this, parent, name, callback, factory));
//    }
}
