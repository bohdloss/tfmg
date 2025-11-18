package it.bohdloss.tfmg.content.electricity;

import net.minecraft.core.BlockPos;

import java.util.Objects;

public class ElectricalCluster {
    public final long id;

    /// Just one of the sources in the cluster, as a starting point for propagation. No guarantees made.
    public BlockPos referenceSource;

    public float highestVoltage;
    public float totalWatts;
    public float frequency;

    public ElectricalCluster(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ElectricalCluster that = (ElectricalCluster) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
