package it.bohdloss.tfmg.content.electricity.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnloadedMember {
    public static final Codec<UnloadedMember> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    BlockPos.CODEC.fieldOf("Pos").forGetter(x -> x.pos),
                    Codec.list(BlockPos.CODEC).fieldOf("Connections").forGetter(x -> x.connections.stream().toList()),
                    Codec.FLOAT.fieldOf("GeneratedVoltage").forGetter(x -> x.generatedVoltage),
                    Codec.FLOAT.fieldOf("Resistance").forGetter(x -> x.resistance),
                    Codec.FLOAT.fieldOf("GeneratorResistance").forGetter(x -> x.generatorResistance),
                    Codec.FLOAT.fieldOf("Frequency").forGetter(x -> x.frequency),
                    Codec.FLOAT.fieldOf("Voltage").forGetter(x -> x.voltage),
                    Codec.FLOAT.fieldOf("AmpsConsumed").forGetter(x -> x.ampsConsumed),
                    Codec.FLOAT.fieldOf("AmpsProvided").forGetter(x -> x.ampsProvided)
            ).apply(inst, UnloadedMember::fromCodec)
    );

    public Long cluster;

    public final BlockPos pos;
    public Set<BlockPos> connections = new HashSet<>();

    public boolean marked;
    public boolean moved;

    // Data from the component
    public float generatedVoltage;
    public float resistance;
    public float generatorResistance;

    // Compiled
    public float frequency;
    public float voltage;
    public float ampsConsumed;
    public float ampsProvided;

    private static UnloadedMember fromCodec(BlockPos pos, List<BlockPos> connections, Float generatedVoltage, Float resistance, Float generatorResistance, Float frequency, Float voltage, Float ampsConsumed, Float ampsProvided) {
        UnloadedMember self = new UnloadedMember(pos);
        self.connections.addAll(connections);
        self.generatedVoltage = generatedVoltage;
        self.resistance = resistance;
        self.generatorResistance = generatorResistance;
        self.frequency = frequency;
        self.voltage = voltage;
        self.ampsConsumed = ampsConsumed;
        self.ampsProvided = ampsProvided;
        return self;
    }

    public boolean isSource() {
        return generatedVoltage != 0 && generatorResistance > 0;
    }

    public float calcGeneratedAmps() {
        float amps = generatedVoltage / generatorResistance;
        return Float.isFinite(amps) ? amps : 0;
    }

    public float calcConsumedAmps() {
        float amps = voltage / resistance;
        return Float.isFinite(amps) ? amps : 0;
    }

    public UnloadedMember(BlockPos pos) {
        this.pos = pos;
    }

    public boolean sync(ElectricData component) {
        // Sync data from the component itself
        float generatedVoltage = Math.max(0, component.getGeneratedVoltage());
        float resistance = Math.max(0, component.getResistance());
        float generatorResistance = Math.max(0, component.getGeneratorResistance());

        boolean dirty = generatedVoltage != this.generatedVoltage ||
                resistance != this.resistance ||
                generatorResistance != this.generatorResistance;

        this.generatedVoltage = generatedVoltage;
        this.resistance = resistance;
        this.generatorResistance = generatorResistance;

        // Provide component with updated data
        component.frequency = this.frequency;
        component.voltage = this.voltage;
//        component.totalNetworkUsage = totalUsage;
//        component.totalNetworkProduction = totalProduction; // FIXME
        component.lastAmpsConsumed = this.ampsConsumed;
        component.lastAmpsProvided = this.ampsProvided;

        return dirty;
    }

    public boolean isVoltageChanger() {
        return false;
    }

    @Override
    public int hashCode() {
        return pos.hashCode();
    }
}
