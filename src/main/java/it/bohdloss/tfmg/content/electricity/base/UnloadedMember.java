package it.bohdloss.tfmg.content.electricity.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// TODO serialize to binary then bas64 so we dont store 60 billion billion yottabytes per component
// Also avoids f***ing around with chunks just because mojang couldn't be f***ed to overload their methods for more than 16 parameters
public class UnloadedMember {
    private record UnloadedMemberChunk1(
            BlockPos pos,
            List<BlockPos> connections,
            List<BlockPos> outputs,
            float generatedVoltage,
            CurrentCalculation power,
            CurrentCalculation generatorPower,
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
                        CurrentCalculation.CODEC.fieldOf("Power").forGetter(UnloadedMemberChunk1::power),
                        CurrentCalculation.CODEC.fieldOf("GeneratorPower").forGetter(UnloadedMemberChunk1::generatorPower),
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
            float wattsReceived,
            Optional<AccumulatorBehavior> accumulator
    ) {
        final static Codec<UnloadedMemberChunk2> CODEC = RecordCodecBuilder.create(
                inst2 -> inst2.group(
                        Codec.FLOAT.fieldOf("WattsReceived").forGetter(UnloadedMemberChunk2::wattsReceived),
                        Codec.optionalField("Accumulator", AccumulatorBehavior.CODEC, false).forGetter(UnloadedMemberChunk2::accumulator)
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
    public CurrentCalculation power;
    public CurrentCalculation generatorPower;
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

    public AccumulatorBehavior accumulator;

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
        self.power = chunk1.power;
        self.generatorPower = chunk1.generatorPower;
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
                power,
                generatorPower,
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
                wattsReceived,
                Optional.ofNullable(accumulator)
        );
    }

    public boolean isSource() {
        return generatedVoltage != 0 && generatorPower.calcCurrent(voltage) > 0 && (accumulator == null || accumulator.charge > 0f);
    }

    public float calcGeneratedAmps() {
        return generatorPower.calcCurrent(generatedVoltage);
    }

    public float calcGeneratorResistance() {
        return generatorPower.calcResistance(generatedVoltage);
    }

    public float calcConsumedAmps(float voltage) {
        return power.calcCurrent(voltage);
    }

    public float calcResistance(float voltage) {
        return power.calcResistance(voltage);
    }

    public UnloadedMember(BlockPos pos) {
        this.pos = pos;
    }

    public static final int COMPONENT_DIRTY = 1;
    public static final int CLUSTER_DIRTY = 1 << 1;

    public int sync(long time, ElectricData component) {
        if(accumulator != null) {
            accumulator.updateCharge(this, time); // Update charge before we change the voltages. Avoids funny business.
        }

        // Sync data from the component itself
        boolean wasAccumulator = accumulator != null;
        boolean wasSource = isSource();
        float generatedVoltage = Math.max(0f, component.getGeneratedVoltage());
        CurrentCalculation power = component.getResistance();
        CurrentCalculation generatorPower = component.getGeneratorResistance();
        float generatorFrequency = Math.max(0f, component.getGeneratorFrequency());

        float inputOutputVoltageMultiplier = Math.max(0f, component.getInputOutputVoltageMultiplier());
        float outputInputVoltageMultiplier = Math.max(0f, component.getOutputInputVoltageMultiplier());
        float inputOutputFrequencyMultiplier = Math.max(0f, component.getInputOutputFrequencyMultiplier());
        float outputInputFrequencyMultiplier = Math.max(0f, component.getOutputInputFrequencyMultiplier());

        boolean componentDirty = generatedVoltage != this.generatedVoltage ||
                !power.equals(this.power) ||
                !generatorPower.equals(this.generatorPower) ||
                generatorFrequency != this.generatorFrequency ||
                inputOutputVoltageMultiplier != this.inputOutputVoltageMultiplier ||
                outputInputVoltageMultiplier != this.outputInputVoltageMultiplier ||
                inputOutputFrequencyMultiplier != this.inputOutputFrequencyMultiplier ||
                outputInputFrequencyMultiplier != this.outputInputFrequencyMultiplier;

        boolean clusterDirty = generatedVoltage != this.generatedVoltage ||
                !generatorPower.equals(this.generatorPower);

        this.generatedVoltage = generatedVoltage;
        this.power = power;
        this.generatorPower = generatorPower;
        this.generatorFrequency = generatorFrequency;

        this.inputOutputVoltageMultiplier = inputOutputVoltageMultiplier;
        this.outputInputVoltageMultiplier = outputInputVoltageMultiplier;
        this.inputOutputFrequencyMultiplier = inputOutputFrequencyMultiplier;
        this.outputInputFrequencyMultiplier = outputInputFrequencyMultiplier;

        if(!wasAccumulator && component.getMaxCharge() > 0f) {
            accumulator = new AccumulatorBehavior();
        } else if(wasAccumulator && component.getMaxCharge() <= 0f) {
            accumulator = null;
        }
        if(accumulator != null) {
            accumulator.maxCharge = Math.max(0f, component.getMaxCharge());
            float charge = component.getCharge();
            if(charge >= 0f) {
                accumulator.charge = charge;
            }
            accumulator.normalizeCharge();
        }

        clusterDirty |= wasSource != isSource();

        // Provide component with updated data
        float previousVoltage = component.voltage;
        component.current = this.current;
        component.frequency = Math.max(0, this.frequency); // In case it is uninitialized and has value -1
        component.voltage = this.voltage;
        component.lastWattsConsumed = this.wattsConsumed;
        component.lastWattsProvided = this.wattsProvided;

        component.notifyVoltageChange(previousVoltage);

        if(accumulator != null) {
            component.onChargeChange(accumulator.charge);
        }

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
        return !outputs.isEmpty() && ((inputOutputVoltageMultiplier != 1f) ||
                (outputInputVoltageMultiplier != 1f) ||
                (inputOutputFrequencyMultiplier != 1f) ||
                (outputInputFrequencyMultiplier != 1f));
    }

    @Override
    public int hashCode() {
        return pos.hashCode();
    }

    public static class AccumulatorBehavior {
        public static final Codec<AccumulatorBehavior> CODEC = RecordCodecBuilder.create(
                inst -> inst.group(
                        Codec.FLOAT.fieldOf("MaxCharge").forGetter(x -> x.maxCharge),
                        Codec.FLOAT.fieldOf("Charge").forGetter(x -> x.charge),
                        Codec.LONG.fieldOf("LastUpdateTime").forGetter(x -> x.lastUpdateTime)
                ).apply(inst, AccumulatorBehavior::fromCodec)
        );

        private static AccumulatorBehavior fromCodec(Float charge, Float maxCharge, Long lastUpdateTime) {
            AccumulatorBehavior self = new AccumulatorBehavior();
            self.maxCharge = maxCharge;
            self.charge = charge;
            self.lastUpdateTime = lastUpdateTime;
            return self;
        }

        public float maxCharge;
        public float charge;
        public long lastUpdateTime;

        public float changeRate(UnloadedMember parent) {
            if(parent.satisfaction < parent.wattsConsumed) {
                return 0f;
            }

            float chargingRate = parent.calcConsumedAmps(parent.voltage);
            float dischargingRate = parent.calcGeneratedAmps();

            return chargingRate - dischargingRate;
        }

        public void updateCharge(UnloadedMember parent, long currentTime) {
            float changeRate = changeRate(parent);
            float secondsPassed =  ((float) (currentTime - lastUpdateTime)) / 20f;
            float offset = changeRate * secondsPassed;

            charge += offset;
            normalizeCharge();
            lastUpdateTime = currentTime;
        }

        public void normalizeCharge() {
            charge = Math.max(0f, Math.min(maxCharge, charge));
        }

        /// If charging, the return is positive and is the number of ticks until fully charged
        ///
        /// If discharging, the return is negative and is the number of ticks until fully discharged
        ///
        /// If the return value is `0`, it's either fully discharged (if `changeRate() < 0`),
        /// fully charged (if `changeRate() > 0`),
        /// or charging and discharging at exactly the same rate (if `changeRate() == 0`)
        public long ticksUntilCharged(UnloadedMember parent) {
            float changeRate = changeRate(parent);

            float remaining = (changeRate >= 0f) ? (maxCharge - charge) : charge; // Coulombs
            // t = C / A
            float secondsUntilCharged = remaining / changeRate;
            float ticksUntilCharged = secondsUntilCharged * 20f;

            if(Float.isNaN(ticksUntilCharged)) {
                return 0L;
            } else if(Float.isInfinite(ticksUntilCharged)) {
                return (long) (ticksUntilCharged > 0f ? Float.MAX_VALUE : Float.MIN_VALUE);
            } else {
                return (long) ticksUntilCharged;
            }
        }
    }
}
