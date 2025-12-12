package it.bohdloss.tfmg.content.electricity.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

import static it.bohdloss.tfmg.TFMGUtils.rationalize;

/// Determines how the amount of current produced / consumed by a component is calculated
public final class CurrentCalculation {
    public static final Codec<CurrentCalculation> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.INT.fieldOf("t").forGetter(x -> x.type),
                    Codec.FLOAT.fieldOf("v").forGetter(x -> x.value)
            ).apply(inst, CurrentCalculation::new)
    );

    private static final int RESISTANCE = 0;
    private static final int CONSTANT = 1;

    private final int type;
    private final float value;

    private CurrentCalculation(int type, float value) {
        this.type = type;
        this.value = value;
    }

    /// The current is calculated based on a resistance value and varies with the voltage
    public static CurrentCalculation resistance(float resistance) {
        return new CurrentCalculation(RESISTANCE, rationalize(resistance));
    }

    /// The current is a constant fixed value, independent of the voltage
    public static CurrentCalculation constant(float current) {
        return new CurrentCalculation(CONSTANT, rationalize(current));
    }

    public float calcCurrent(float voltage) {
        return switch (type) {
            case RESISTANCE -> {
                float amps = voltage / value;
                yield Float.isFinite(amps) ? amps : 0f;
            }
            case CONSTANT -> (voltage <= 0f) ? 0f : value;
            default -> 0;
        };
    }

    public float calcResistance(float voltage) {
        return switch (type) {
            case RESISTANCE -> value;
            case CONSTANT -> {
                float ohms = voltage / value;
                yield Float.isFinite(ohms) ? ohms : 0f;
            }
            default -> 0;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CurrentCalculation that = (CurrentCalculation) o;
        return type == that.type && Float.compare(value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}
