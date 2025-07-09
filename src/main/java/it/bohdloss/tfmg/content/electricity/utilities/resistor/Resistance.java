package it.bohdloss.tfmg.content.electricity.utilities.resistor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public record Resistance(int value) {
    public static final Resistance DEFAULT = new Resistance(0);

    public static final Codec<Resistance> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.INT.fieldOf("rating").forGetter(Resistance::value)
            ).apply(inst, Resistance::new)
    );

    public static final StreamCodec<ByteBuf, Resistance> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, Resistance::value,
            Resistance::new
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
