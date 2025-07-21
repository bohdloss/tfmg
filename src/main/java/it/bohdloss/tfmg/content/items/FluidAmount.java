package it.bohdloss.tfmg.content.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.bohdloss.tfmg.content.electricity.utilities.transformer.CoilTurns;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.Objects;

public record FluidAmount(int value) {
    public static final FluidAmount DEFAULT = new FluidAmount(0);

    public static final Codec<FluidAmount> CODEC = ExtraCodecs.NON_NEGATIVE_INT.comapFlatMap(
            amount -> DataResult.success(new FluidAmount(amount)),
            fluidAmount -> fluidAmount.value
    );

    public static final StreamCodec<ByteBuf, FluidAmount> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(
            FluidAmount::new,
            FluidAmount::value
    );

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FluidAmount that = (FluidAmount) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
