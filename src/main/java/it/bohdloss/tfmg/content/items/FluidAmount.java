package it.bohdloss.tfmg.content.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public record FluidAmount(int value) {
    public static final FluidAmount DEFAULT = new FluidAmount(0);

    public static final Codec<FluidAmount> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.INT.fieldOf("amount").forGetter(FluidAmount::value)
            ).apply(inst, FluidAmount::new)
    );

    public static final StreamCodec<ByteBuf, FluidAmount> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, FluidAmount::value,
            FluidAmount::new
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
