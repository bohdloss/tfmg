package it.bohdloss.tfmg.content.electricity.connection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public record WireSelection(BlockPos pos, float renderX, float renderY, float renderZ) {
    public static final Codec<WireSelection> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.INT.fieldOf("blockX").forGetter(it -> it.pos().getX()),
                    Codec.INT.fieldOf("blockY").forGetter(it -> it.pos().getY()),
                    Codec.INT.fieldOf("blockZ").forGetter(it -> it.pos().getZ()),
                    Codec.FLOAT.fieldOf("renderX").forGetter(WireSelection::renderX),
                    Codec.FLOAT.fieldOf("renderY").forGetter(WireSelection::renderY),
                    Codec.FLOAT.fieldOf("renderZ").forGetter(WireSelection::renderZ)
            ).apply(inst, (blockX, blockY, blockZ, renderX, renderY, renderZ) -> new WireSelection(new BlockPos(blockX, blockY, blockZ), renderX, renderY, renderZ))
    );

    public static final StreamCodec<ByteBuf, WireSelection> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, it -> it.pos().getX(),
            ByteBufCodecs.INT, it -> it.pos().getY(),
            ByteBufCodecs.INT, it -> it.pos().getZ(),
            ByteBufCodecs.FLOAT, WireSelection::renderX,
            ByteBufCodecs.FLOAT, WireSelection::renderY,
            ByteBufCodecs.FLOAT, WireSelection::renderZ,
            (blockX, blockY, blockZ, renderX, renderY, renderZ) -> new WireSelection(new BlockPos(blockX, blockY, blockZ), renderX, renderY, renderZ)
    );

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WireSelection that = (WireSelection) o;
        return Float.compare(renderX, that.renderX) == 0 && Float.compare(renderY, that.renderY) == 0 && Float.compare(renderZ, that.renderZ) == 0 && Objects.equals(pos, that.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, renderX, renderY, renderZ);
    }
}