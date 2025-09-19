package it.bohdloss.tfmg.content.electricity;

public class ElectricalCluster {
    public final long id;

    public float highestVoltage;
    public float totalWatts;

    public ElectricalCluster(long id) {
        this.id = id;
    }
}
