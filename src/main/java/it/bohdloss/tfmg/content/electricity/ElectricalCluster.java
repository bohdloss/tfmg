package it.bohdloss.tfmg.content.electricity;

import net.minecraft.core.BlockPos;

public class ElectricalCluster {
    public final long id;

    /// Just one of the sources in the cluster, as a starting point for propagation. No guarantees made.
    public BlockPos referenceSource;

    public float highestVoltage;
    public float totalWatts;

    public ElectricalCluster(long id) {
        this.id = id;
    }
}
