package it.bohdloss.tfmg.content.electricity.utilities.resistor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.bohdloss.tfmg.content.electricity.connection.SpoolAmount;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.Objects;

public record Resistance(int value) {
    public static final Resistance DEFAULT = new Resistance(0);

    public static final Codec<Resistance> CODEC = ExtraCodecs.NON_NEGATIVE_INT.comapFlatMap(
            value -> DataResult.success(new Resistance(value)),
            resistance -> resistance.value
    );

    public static final StreamCodec<ByteBuf, Resistance> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(
            Resistance::new,
            Resistance::value
    );

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Resistance that = (Resistance) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
