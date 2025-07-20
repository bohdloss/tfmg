package it.bohdloss.tfmg.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public class IndustrialBlastingRecipeParams extends ProcessingRecipeParams {
    public static MapCodec<IndustrialBlastingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec(IndustrialBlastingRecipeParams::new).forGetter(Function.identity()),
            Codec.INT.optionalFieldOf("hot_air_usage", 0).forGetter(IndustrialBlastingRecipeParams::hotAirUsage),
            Codec.BOOL.optionalFieldOf("needs_flux", false).forGetter(IndustrialBlastingRecipeParams::needsFlex)
    ).apply(instance, (params, hotAirUsage, needsFlux) -> {
        params.hotAirUsage = hotAirUsage;
        params.needsFlux = needsFlux;
        return params;
    }));
    public static StreamCodec<RegistryFriendlyByteBuf, IndustrialBlastingRecipeParams> STREAM_CODEC = streamCodec(IndustrialBlastingRecipeParams::new);

    protected int hotAirUsage;
    protected boolean needsFlux;

    protected final int hotAirUsage() {
        return hotAirUsage;
    }

    protected final boolean needsFlex() {
        return needsFlux;
    }

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        ByteBufCodecs.INT.encode(buffer, hotAirUsage);
        ByteBufCodecs.BOOL.encode(buffer, needsFlux);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        hotAirUsage = ByteBufCodecs.INT.decode(buffer);
        needsFlux = ByteBufCodecs.BOOL.decode(buffer);
    }
}
