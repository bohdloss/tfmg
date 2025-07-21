package it.bohdloss.tfmg.content.electricity.utilities.transformer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.bohdloss.tfmg.content.electricity.utilities.resistor.Resistance;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.Objects;

public record CoilTurns(int amount) {
    public static final CoilTurns DEFAULT = new CoilTurns(0);
    public static final CoilTurns MAX = new CoilTurns(1000);

    public static final Codec<CoilTurns> CODEC = ExtraCodecs.NON_NEGATIVE_INT.comapFlatMap(
            amount -> DataResult.success(new CoilTurns(amount)),
            coilTurns -> coilTurns.amount
    );

    public static final StreamCodec<ByteBuf, CoilTurns> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(
            CoilTurns::new,
            CoilTurns::amount
    );

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CoilTurns windings = (CoilTurns) o;
        return amount == windings.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(amount);
    }
}