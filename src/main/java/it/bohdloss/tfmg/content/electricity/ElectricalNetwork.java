package it.bohdloss.tfmg.content.electricity;

import it.bohdloss.tfmg.content.electricity.base.IElectric;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ElectricalNetwork {
    public Long id;
    public boolean initialized;

    // Float represents power in Amps and 1 volt
    public Map<IElectric, Float> sources = new HashMap<>();
    public Map<IElectric, Float> members = new HashMap<>();

    private float currentPower;
    private float currentConsumption;
    private float unloadedPower;
    private float unloadedConsumption;
    private int unloadedMembers;

    public void initFromTE(float maxUsage, float currentUsage, int members) {
        unloadedPower = maxUsage;
        unloadedConsumption = currentUsage;
        unloadedMembers = members;
        initialized = true;
        updateConsumption();
        updatePower();
    }

    public void addSilently(IElectric be, float lastProducedAmps, float lastConsumedAmps) {
        if (members.containsKey(be))
            return;
        if (be.isElectricalSource()) {
            unloadedPower -= lastProducedAmps * positive(be.getGeneratedVoltage());
            float addedPower = be.calculateAmpsGenerated1Volt();
            sources.put(be, addedPower);
        }

        unloadedConsumption -= lastConsumedAmps * positive(be.getTheoreticalVoltage());
        float consumptionApplied = be.calculateAmpsConsumed1Volt();
        members.put(be, consumptionApplied);

        unloadedMembers--;
        if (unloadedMembers < 0) {
            unloadedMembers = 0;
        }
        if (unloadedPower < 0) {
            unloadedPower = 0;
        }
        if (unloadedConsumption < 0) {
            unloadedConsumption = 0;
        }
    }

    public void add(IElectric be) {
        if (members.containsKey(be)) {
            return;
        }
        if (be.isElectricalSource()) {
            sources.put(be, be.calculateAmpsGenerated1Volt());
        }
        members.put(be, be.calculateAmpsConsumed1Volt());
        updateFromNetwork(be);
        be.setElectricalNetworkDirty(true);
    }

    public void updatePowerFor(IElectric be, float power) {
        sources.put(be, power);
        updatePower();
    }

    public void updateConsumptionFor(IElectric be, float consumption) {
        members.put(be, consumption);
        updateConsumption();
    }

    public void remove(IElectric be) {
        if (!members.containsKey(be)) {
            return;
        }
        if (be.isElectricalSource()) {
            sources.remove(be);
        }
        members.remove(be);
        be.updateFromElectricalNetwork(0, 0, 0);

        if (members.isEmpty()) {
            ElectricalNetworkManager.networks.get(be.getLevel()).remove(this.id);
            return;
        }

        members.keySet()
                .stream()
                .findFirst()
                .map(member -> { member.setElectricalNetworkDirty(true); return true; });
    }

    public void sync() {
        for (IElectric be : members.keySet()) {
            updateFromNetwork(be);
        }
    }

    private void updateFromNetwork(IElectric be) {
        be.updateFromElectricalNetwork(currentPower, currentConsumption, getSize());
    }

    public void updatePower() {
        float newMaxConsumption = calculatePower();
        if (currentPower != newMaxConsumption) {
            currentPower = newMaxConsumption;
            sync();
        }
    }

    public void updateConsumption() {
        float newConsumption = calculateConsumption();
        if (currentConsumption != newConsumption) {
            currentConsumption = newConsumption;
            sync();
        }
    }

    public void updateNetwork() {
        float newConsumption = calculateConsumption();
        float newMaxConsumption = calculatePower();
        if (currentConsumption != newConsumption || currentPower != newMaxConsumption) {
            currentConsumption = newConsumption;
            currentPower = newMaxConsumption;
            sync();
        }
    }

    public float calculatePower() {
        float presentPower = 0;
        for (Iterator<IElectric> iterator = sources.keySet().iterator(); iterator.hasNext();) {
            IElectric be = iterator.next();
            if (be.getLevel().getBlockEntity(be.getBlockPos()) != be) {
                iterator.remove();
                continue;
            }
            presentPower += getActualPowerOf(be);
        }
        float newMaxPower = presentPower + unloadedPower;
        return newMaxPower;
    }

    public float calculateConsumption() {
        float presentConsumption = 0;
        for (Iterator<IElectric> iterator = members.keySet().iterator(); iterator.hasNext();) {
            IElectric be = iterator.next();
            if (be.getLevel().getBlockEntity(be.getBlockPos()) != be) {
                iterator.remove();
                continue;
            }
            presentConsumption += getActualConsumptionOf(be);
        }
        float newConsumption = presentConsumption + unloadedConsumption;
        return newConsumption;
    }

    public float getActualPowerOf(IElectric be) {
        return sources.get(be) * positive(be.getGeneratedVoltage());
    }

    public float getActualConsumptionOf(IElectric be) {
        return members.get(be) * positive(be.getTheoreticalVoltage());
    }

    private static float positive(float voltage) {
        return Math.max(voltage, 0f);
    }

    public int getSize() {
        return unloadedMembers + members.size();
    }
}
