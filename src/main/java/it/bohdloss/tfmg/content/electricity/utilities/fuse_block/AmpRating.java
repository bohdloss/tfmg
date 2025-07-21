package it.bohdloss.tfmg.content.electricity.utilities.fuse_block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.bohdloss.tfmg.content.electricity.utilities.transformer.CoilTurns;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.Objects;

public record AmpRating(int rating) {
    public static final AmpRating DEFAULT = new AmpRating(0);

    public static final Codec<AmpRating> CODEC = ExtraCodecs.NON_NEGATIVE_INT.comapFlatMap(
            rating -> DataResult.success(new AmpRating(rating)),
            ampRating -> ampRating.rating
    );

    public static final StreamCodec<ByteBuf, AmpRating> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(
            AmpRating::new,
            AmpRating::rating
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