package it.bohdloss.tfmg.config;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.Map;

public class TFMGResistivity extends ConfigBase {
    // bump this version to reset configured values.
    private static final int VERSION = 2;

    // IDs need to be used since configs load before registration

    private static final Object2DoubleMap<ResourceLocation> DEFAULT_RESISTIVITIES = new Object2DoubleOpenHashMap<>();

    protected final Map<ResourceLocation, ModConfigSpec.ConfigValue<Double>> resistivities = new HashMap<>();

    @Override
    public void registerAll(ModConfigSpec.Builder builder) {
        builder.comment(".", Comments.resistivity).push("resistivity");
        DEFAULT_RESISTIVITIES.forEach((id, value) -> this.resistivities.put(id, builder.define(id.getPath(), value)));
        builder.pop();
    }

    @Override
    public String getName() {
        return "resistivityValues.v" + VERSION;
    }

//    @Nullable
//    public DoubleSupplier getResistivity(CableType cableType) {
//        ResourceLocation id = cableType.getKey();
//        ModConfigSpec.ConfigValue<Double> rating = this.resistivities.get(id);
//        return rating == null ? null : rating::get;
//    }
//
//    public static <B extends CableType, P> NonNullUnaryOperator<CableTypeBuilder<B, P>> setNoResistivity() {
//        return setResistivity(0);
//    }
//
//    public static <B extends CableType, P> NonNullUnaryOperator<CableTypeBuilder<B, P>> setResistivity(double rating) {
//        return builder -> {
//            //assertFromCreate(builder);
//            ResourceLocation id = TFMG.asResource(builder.getName());
//            DEFAULT_RESISTIVITIES.put(id, rating);
//            return builder;
//        };
//    }

    private static class Comments {
        static String resistivity = "Configure the individual resistivity of cable types.";
    }
}
