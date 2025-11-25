package it.bohdloss.tfmg.content.electricity.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnloadedMember {
    private record UnloadedMemberChunk1(
            BlockPos pos,
            List<BlockPos> connections,
            List<BlockPos> outputs,
            float generatedVoltage,
            float resistance,
            float generatorResistance,
            float generatorFrequency,
            float inputOutputVoltageMultiplier,
            float outputInputVoltageMultiplier,
            float inputOutputFrequencyMultiplier,
            float outputInputFrequencyMultiplier,
            float current,
            float frequency,
            float voltage,
            float wattsConsumed,
            float wattsProvided
    ) {
        final static Codec<UnloadedMemberChunk1> CODEC = RecordCodecBuilder.create(
                inst2 -> inst2.group(
                        BlockPos.CODEC.fieldOf("Pos").forGetter(UnloadedMemberChunk1::pos),
                        Codec.list(BlockPos.CODEC).fieldOf("Connections").forGetter(UnloadedMemberChunk1::connections),
                        Codec.list(BlockPos.CODEC).fieldOf("Outputs").forGetter(UnloadedMemberChunk1::outputs),
                        Codec.FLOAT.fieldOf("GeneratedVoltage").forGetter(UnloadedMemberChunk1::generatedVoltage),
                        Codec.FLOAT.fieldOf("Resistance").forGetter(UnloadedMemberChunk1::resistance),
                        Codec.FLOAT.fieldOf("GeneratorResistance").forGetter(UnloadedMemberChunk1::generatorResistance),
                        Codec.FLOAT.fieldOf("GeneratorFrequency").forGetter(UnloadedMemberChunk1::generatorFrequency),
                        Codec.FLOAT.fieldOf("InputOutputVoltageMultiplier").forGetter(UnloadedMemberChunk1::inputOutputVoltageMultiplier),
                        Codec.FLOAT.fieldOf("OutputInputVoltageMultiplier").forGetter(UnloadedMemberChunk1::outputInputVoltageMultiplier),
                        Codec.FLOAT.fieldOf("InputOutputFrequencyMultiplier").forGetter(UnloadedMemberChunk1::inputOutputFrequencyMultiplier),
                        Codec.FLOAT.fieldOf("OutputInputFrequencyMultiplier").forGetter(UnloadedMemberChunk1::outputInputFrequencyMultiplier),
                        Codec.FLOAT.fieldOf("Current").forGetter(UnloadedMemberChunk1::current),
                        Codec.FLOAT.fieldOf("Frequency").forGetter(UnloadedMemberChunk1::frequency),
                        Codec.FLOAT.fieldOf("Voltage").forGetter(UnloadedMemberChunk1::voltage),
                        Codec.FLOAT.fieldOf("WattsConsumed").forGetter(UnloadedMemberChunk1::wattsConsumed),
                        Codec.FLOAT.fieldOf("WattsProvided").forGetter(UnloadedMemberChunk1::wattsProvided)
                ).apply(inst2, UnloadedMemberChunk1::new)
        );
    }
    private record UnloadedMemberChunk2(
            float wattsReceived
    ) {
        final static Codec<UnloadedMemberChunk2> CODEC = RecordCodecBuilder.create(
                inst2 -> inst2.group(
                        Codec.FLOAT.fieldOf("WattsReceived").forGetter(UnloadedMemberChunk2::wattsReceived)
                ).apply(inst2, UnloadedMemberChunk2::new)
        );
    }

    public static final Codec<UnloadedMember> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    UnloadedMemberChunk1.CODEC.fieldOf("Chunk1").forGetter(UnloadedMember::serializeChunk1),
                    UnloadedMemberChunk2.CODEC.fieldOf("Chunk2").forGetter(UnloadedMember::serializeChunk2)
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
    public float generatorFrequency;

    public float inputOutputVoltageMultiplier;
    public float outputInputVoltageMultiplier;

    public float inputOutputFrequencyMultiplier;
    public float outputInputFrequencyMultiplier;

    // Compiled
    public float current;
    public float frequency;
    public float voltage;
    public float wattsConsumed;
    public float wattsProvided;
    public float wattsReceived; // Different sources may receive different amounts of energy (such as when diodes are involved)

    // Temporary
    public float satisfaction;

    private static UnloadedMember fromCodec(
            UnloadedMemberChunk1 chunk1,
            UnloadedMemberChunk2 chunk2
    ) {
        UnloadedMember self = new UnloadedMember(chunk1.pos);
        self.connections.addAll(chunk1.connections);
        self.connections.remove(chunk1.pos); // Prevent connections to itself
        self.outputs.addAll(chunk1.outputs);
        self.outputs.removeIf(out -> !self.connections.contains(out)); // `outputs` is a subset of `connections`
        self.generatedVoltage = chunk1.generatedVoltage;
        self.resistance = chunk1.resistance;
        self.generatorResistance = chunk1.generatorResistance;
        self.generatorFrequency = chunk1.generatorFrequency;
        self.inputOutputVoltageMultiplier = chunk1.inputOutputVoltageMultiplier;
        self.outputInputVoltageMultiplier = chunk1.outputInputVoltageMultiplier;
        self.inputOutputFrequencyMultiplier = chunk1.inputOutputFrequencyMultiplier;
        self.outputInputFrequencyMultiplier = chunk1.outputInputFrequencyMultiplier;
        self.current = chunk1.current;
        self.frequency = chunk1.frequency;
        self.voltage = chunk1.voltage;
        self.wattsConsumed = chunk1.wattsConsumed;
        self.wattsProvided = chunk1.wattsProvided;
        self.wattsReceived = chunk2.wattsReceived;
        return self;
    }

    private UnloadedMemberChunk1 serializeChunk1() {
        return new UnloadedMemberChunk1(
                pos,
                connections.stream().toList(),
                outputs.stream().toList(),
                generatedVoltage,
                resistance,
                generatorResistance,
                generatorFrequency,
                inputOutputVoltageMultiplier,
                outputInputVoltageMultiplier,
                inputOutputFrequencyMultiplier,
                outputInputFrequencyMultiplier,
                current,
                frequency,
                voltage,
                wattsConsumed,
                wattsProvided
        );
    }

    private UnloadedMemberChunk2 serializeChunk2() {
        return new UnloadedMemberChunk2(
                wattsReceived
        );
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

    public static final int COMPONENT_DIRTY = 1;
    public static final int CLUSTER_DIRTY = 1 << 1;

    public int sync(ElectricData component) {
        // Sync data from the component itself
        boolean wasSource = isSource();
        float generatedVoltage = Math.max(0, component.getGeneratedVoltage());
        float resistance = Math.max(0, component.getResistance());
        float generatorResistance = Math.max(0, component.getGeneratorResistance());
        float generatorFrequency = Math.max(0, component.getGeneratorFrequency());

        float inputOutputVoltageMultiplier = Math.max(0, component.getInputOutputVoltageMultiplier());
        float outputInputVoltageMultiplier = Math.max(0, component.getOutputInputVoltageMultiplier());
        float inputOutputFrequencyMultiplier = Math.max(0, component.getInputOutputFrequencyMultiplier());
        float outputInputFrequencyMultiplier = Math.max(0, component.getOutputInputFrequencyMultiplier());

        boolean componentDirty = generatedVoltage != this.generatedVoltage ||
                resistance != this.resistance ||
                generatorResistance != this.generatorResistance ||
                generatorFrequency != this.generatorFrequency ||
                inputOutputVoltageMultiplier != this.inputOutputVoltageMultiplier ||
                outputInputVoltageMultiplier != this.outputInputVoltageMultiplier ||
                inputOutputFrequencyMultiplier != this.inputOutputFrequencyMultiplier ||
                outputInputFrequencyMultiplier != this.outputInputFrequencyMultiplier;

        boolean clusterDirty = generatedVoltage != this.generatedVoltage ||
                generatorResistance != this.generatorResistance ||
                inputOutputVoltageMultiplier != this.inputOutputVoltageMultiplier ||
                outputInputVoltageMultiplier != this.outputInputVoltageMultiplier ||
                inputOutputFrequencyMultiplier != this.inputOutputFrequencyMultiplier ||
                outputInputFrequencyMultiplier != this.outputInputFrequencyMultiplier;

        this.generatedVoltage = generatedVoltage;
        this.resistance = resistance;
        this.generatorResistance = generatorResistance;
        this.generatorFrequency = generatorFrequency;

        this.inputOutputVoltageMultiplier = inputOutputVoltageMultiplier;
        this.outputInputVoltageMultiplier = outputInputVoltageMultiplier;
        this.inputOutputFrequencyMultiplier = inputOutputFrequencyMultiplier;
        this.outputInputFrequencyMultiplier = outputInputFrequencyMultiplier;

        clusterDirty |= wasSource != isSource();

        // Provide component with updated data
        component.current = this.current;
        component.frequency = Math.max(0, this.frequency); // In case it is uninitialized and has value -1
        component.voltage = this.voltage;
        component.lastWattsConsumed = this.wattsConsumed;
        component.lastWattsProvided = this.wattsProvided;

        return (componentDirty ? COMPONENT_DIRTY : 0) | (clusterDirty ? CLUSTER_DIRTY : 0);
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
