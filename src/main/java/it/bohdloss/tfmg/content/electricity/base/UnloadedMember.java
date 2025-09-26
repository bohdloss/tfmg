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
                    Codec.list(BlockPos.CODEC).fieldOf("Outputs").forGetter(x -> x.outputs.stream().toList()),
                    Codec.FLOAT.fieldOf("GeneratedVoltage").forGetter(x -> x.generatedVoltage),
                    Codec.FLOAT.fieldOf("Resistance").forGetter(x -> x.resistance),
                    Codec.FLOAT.fieldOf("GeneratorResistance").forGetter(x -> x.generatorResistance),
                    Codec.FLOAT.fieldOf("InputOutputMultiplier").forGetter(x -> x.inputOutputMultiplier),
                    Codec.FLOAT.fieldOf("OutputInputMultiplier").forGetter(x -> x.outputInputMultiplier),
                    Codec.FLOAT.fieldOf("Frequency").forGetter(x -> x.frequency),
                    Codec.FLOAT.fieldOf("Voltage").forGetter(x -> x.voltage),
                    Codec.FLOAT.fieldOf("WattsConsumed").forGetter(x -> x.wattsConsumed),
                    Codec.FLOAT.fieldOf("WattsProvided").forGetter(x -> x.wattsProvided),
                    Codec.FLOAT.fieldOf("WattsReceived").forGetter(x -> x.wattsReceived)
            ).apply(inst, UnloadedMember::fromCodec)
    );

    public Long cluster;

    public final BlockPos pos;
    protected final Set<BlockPos> connections = new HashSet<>();
    protected final Set<BlockPos> outputs = new HashSet<>();

    // Data from the component
    public float generatedVoltage;
    public float resistance;
    public float generatorResistance;

    public float inputOutputMultiplier;
    public float outputInputMultiplier;

    // Compiled
    public float frequency;
    public float voltage;
    public float wattsConsumed;
    public float wattsProvided;

    public float wattsReceived; // Different sources may receive different amounts of energy (such as when diodes are involved)

    private static UnloadedMember fromCodec(BlockPos pos, List<BlockPos> connections, List<BlockPos> outputs, Float generatedVoltage, Float resistance, Float generatorResistance, Float inputOutputMultiplier, Float outputInputMultiplier, Float frequency, Float voltage, Float wattsConsumed, Float wattsProvided, Float wattsReceived) {
        UnloadedMember self = new UnloadedMember(pos);
        self.connections.addAll(connections);
        self.connections.remove(pos); // Prevent connections to itself
        self.outputs.addAll(outputs);
        self.outputs.removeIf(out -> !self.connections.contains(out)); // `outputs` is a subset of `connections`
        self.generatedVoltage = generatedVoltage;
        self.resistance = resistance;
        self.generatorResistance = generatorResistance;
        self.inputOutputMultiplier = inputOutputMultiplier;
        self.outputInputMultiplier = outputInputMultiplier;
        self.frequency = frequency;
        self.voltage = voltage;
        self.wattsConsumed = wattsConsumed;
        self.wattsProvided = wattsProvided;
        self.wattsReceived = wattsReceived;
        return self;
    }

    public boolean isSource() {
        return generatedVoltage != 0 && generatorResistance > 0;
    }

    public float calcGeneratedAmps() {
        float amps = generatedVoltage / generatorResistance;
        return Float.isFinite(amps) ? amps : 0;
    }

    public float calcConsumedAmps(float voltage) {
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

        float inputOutputMultiplier = Math.max(0, component.getInputOutputVoltageMultiplier());
        float outputInputMultiplier = Math.max(0, component.getOutputInputVoltageMultiplier());

        boolean dirty = generatedVoltage != this.generatedVoltage ||
                resistance != this.resistance ||
                generatorResistance != this.generatorResistance ||
                inputOutputMultiplier != this.inputOutputMultiplier ||
                outputInputMultiplier != this.outputInputMultiplier;

        this.generatedVoltage = generatedVoltage;
        this.resistance = resistance;
        this.generatorResistance = generatorResistance;

        this.inputOutputMultiplier = inputOutputMultiplier;
        this.outputInputMultiplier = outputInputMultiplier;

        // Provide component with updated data
        component.frequency = this.frequency;
        component.voltage = this.voltage;
        component.lastWattsConsumed = this.wattsConsumed;
        component.lastWattsProvided = this.wattsProvided;

        return dirty;
    }

    public void addConnection(BlockPos to, boolean isOutput) {
        connections.add(to);
        if(isOutput) {
            outputs.add(to);
        } else {
            outputs.remove(to);
        }
    }

    public void removeConnection(BlockPos to) {
        connections.remove(to);
        outputs.remove(to);
    }

    public Iterable<BlockPos> getConnections() {
        return connections::iterator;
    }

    public Iterable<BlockPos> getOutputConnections() {
        return outputs::iterator;
    }

    public boolean isConnected(BlockPos to) {
        return connections.contains(to);
    }

    /// If `isConnected` returns false for `to`, then this will also return false
    public boolean hasOutput(BlockPos to) {
        return outputs.contains(to);
    }

    public boolean isVoltageChanger() {
        return !outputs.isEmpty();
    }

    @Override
    public int hashCode() {
        return pos.hashCode();
    }
}
