package it.bohdloss.tfmg.content.electricity.connection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.Objects;

public record SpoolAmount(int amount) {
    public static final SpoolAmount DEFAULT = new SpoolAmount(0);
    public static final SpoolAmount MAX = new SpoolAmount(1000);

    public static final Codec<SpoolAmount> CODEC = ExtraCodecs.NON_NEGATIVE_INT.comapFlatMap(
            amount -> DataResult.success(new SpoolAmount(amount)),
            spoolAmount -> spoolAmount.amount
    );

    public static final StreamCodec<ByteBuf, SpoolAmount> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(
            SpoolAmount::new,
            SpoolAmount::amount
    );

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SpoolAmount windings = (SpoolAmount) o;
        return amount == windings.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(amount);
    }
}