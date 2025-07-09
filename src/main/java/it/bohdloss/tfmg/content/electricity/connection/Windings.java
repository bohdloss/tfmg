package it.bohdloss.tfmg.content.electricity.connection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public record Windings(int amount) {
    public static final Windings DEFAULT = new Windings(0);
    public static final Windings MAX = new Windings(1000);

    public static final Codec<Windings> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.INT.fieldOf("rating").forGetter(Windings::amount)
            ).apply(inst, Windings::new)
    );

    public static final StreamCodec<ByteBuf, Windings> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, Windings::amount,
            Windings::new
    );

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Windings windings = (Windings) o;
        return amount == windings.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(amount);
    }
}