package it.bohdloss.tfmg.content.electricity.utilities.fuse_block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public record AmpRating(int rating) {
    public static final AmpRating DEFAULT = new AmpRating(0);

    public static final Codec<AmpRating> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.INT.fieldOf("rating").forGetter(AmpRating::rating)
            ).apply(inst, AmpRating::new)
    );

    public static final StreamCodec<ByteBuf, AmpRating> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AmpRating::rating,
            AmpRating::new
    );

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AmpRating windings = (AmpRating) o;
        return rating == windings.rating;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(rating);
    }
}