package it.bohdloss.tfmg.content.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.Objects;

public record AccumulatorStorage(int value) {
    public static final AccumulatorStorage DEFAULT = new AccumulatorStorage(0);

    public static final Codec<AccumulatorStorage> CODEC = ExtraCodecs.NON_NEGATIVE_INT.comapFlatMap(
            amount -> DataResult.success(new AccumulatorStorage(amount)),
            storage -> storage.value
    );

    public static final StreamCodec<ByteBuf, AccumulatorStorage> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(
            AccumulatorStorage::new,
            AccumulatorStorage::value
    );

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AccumulatorStorage that = (AccumulatorStorage) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
