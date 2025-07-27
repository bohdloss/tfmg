package it.bohdloss.tfmg.content.electricity.base;

import it.bohdloss.tfmg.content.electricity.ElectricalNetwork;

public interface IGeneratingElectric extends IElectric {
    default void notifyPowerChange(float power) {
        getOrCreateElectricalNetwork().updatePowerFor(this, power);
    }

    default void updateGeneratedVoltage() {
        float voltage = getGeneratedVoltage();
        float prevVoltage = this.getTheoreticalVoltage();

        if (getLevel() == null || getLevel().isClientSide) {
            return;
        }

        if (prevVoltage != voltage) {
            if (!hasElectricalSource() && getElectricEffects() != null) {
                getElectricEffects().queuePowerIndicators();
            }

            applyNewVoltage(prevVoltage, voltage);
        }

        if (hasElectricalNetwork() && voltage != 0) {
            ElectricalNetwork network = getOrCreateElectricalNetwork();
            notifyPowerChange(calculateAmpsGenerated1Volt());
            getOrCreateElectricalNetwork().updateConsumptionFor(this, calculateAmpsConsumed1Volt());
            network.updateConsumption();
        }

        onVoltageChanged(prevVoltage);
        sendData();
    }

    default void applyNewVoltage(float prevVoltage, float voltage) {
        // Voltage changed to 0
        if (voltage == 0) {
            if (hasElectricalSource()) {
                notifyPowerChange(0);
                getOrCreateElectricalNetwork().updateConsumptionFor(this, calculateAmpsConsumed1Volt());
                return;
            }
            detachElectric();
            setVoltage(0);
            setElectricalNetwork(null);
            return;
        }

        // voltage was zero - create a new Network
        if (prevVoltage == 0) {
            setVoltage(voltage);
            setElectricalNetwork(createElectricalNetworkId());
            attachElectric();
            return;
        }

        // Change voltage when overpowered by other generator
        if (hasElectricalSource()) {

            // Staying below Overpowered voltage
            if (Math.abs(prevVoltage) >= Math.abs(voltage)) {
                if (Math.signum(prevVoltage) != Math.signum(voltage)) {
                    getLevel().destroyBlock(getBlockPos(), true);
                }
                return;
            }

            // Voltage higher than attached network -> become the new source
            detachElectric();
            setVoltage(voltage);
            nullElectricalSource();
            setElectricalNetwork(createElectricalNetworkId());
            attachElectric();
            return;
        }

        // Reapply source
        detachElectric();
        setVoltage(voltage);
        attachElectric();
    }

    default Long createElectricalNetworkId() {
        return getBlockPos().asLong();
    }

    void setReactivateElectricalSource(boolean reactivateSource);

    boolean getReactivateElectricalSource();
}
