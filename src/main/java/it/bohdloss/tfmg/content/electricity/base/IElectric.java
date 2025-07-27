package it.bohdloss.tfmg.content.electricity.base;

import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.content.electricity.BlockResistanceValues;
import it.bohdloss.tfmg.content.electricity.ElectricalNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IElectric extends IHaveContext {
    boolean needsVoltageUpdate();

    void setElectricalSource(BlockPos source);

    void attachElectric();

    void detachElectric();

    void clearElectricInformation();

    boolean isShortCircuited();

    @Nullable Long getElectricalNetwork();

    default ElectricalNetwork getOrCreateElectricalNetwork() {
        return TFMG.ELECTRICITY_MANAGER.getOrCreateNetworkFor(this);
    }

    boolean hasElectricalNetwork();

    void validateElectric();

    void removeElectricalSource();

    BlockPos getElectricalSource();

    void nullElectricalSource();

    void setElectricalNetwork(@Nullable Long networkIn);

    void updateFromElectricalNetwork(float currentPower, float currentUsage, int size);

    default float getConsumedPower() {
        return getConsumedCurrent() * getVoltage();
    }

    default float getGeneratedPower() {
        return getGeneratorResistance() * getGeneratedVoltage();
    }

    default float getResistance() {
        return (float) BlockResistanceValues.getResistance(getPowerConfigKey());
    }

    default float getGeneratorResistance() {
        return (float) BlockResistanceValues.getGeneratorResistance(getPowerConfigKey());
    }

    /// The amps consumed at 1 volt of input
    float calculateAmpsConsumed1Volt();

    /// The amps produced if the produced voltage was 1 volt
    float calculateAmpsGenerated1Volt();

    void onVoltageChanged(float previousVoltage);

    boolean isElectricalSource();

    float getVoltage();

    float getTheoreticalVoltage();

    void setNeedsVoltageUpdate(boolean updateVoltage);

    float getGeneratedVoltage();

    default float getGeneratedCurrent() {
        return getGeneratedVoltage() * calculateAmpsGenerated1Volt();
    }

    default float getConsumedCurrent() {
        return getVoltage() * calculateAmpsConsumed1Volt();
    }

    void setVoltage(float voltage);

    boolean hasElectricalSource();

    void warnOfMovementElectrical();

    int getVoltageFlickerScore();

    Block getPowerConfigKey();

    boolean isElectricalNetworkDirty();

    void setElectricalNetworkDirty(boolean dirty);

    int getPreventVoltageUpdate();
    void setPreventVoltageUpdate(int preventVoltageUpdate);

    @Nullable ElectricEffectHandler getElectricEffects();

    default float propagateVoltageTo(IElectric target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedByAxis) {
        return 0;
    }

    default List<BlockPos> addElectricPropagationLocations(IElectricBlock block, BlockState state, List<BlockPos> neighbours) {
        return neighbours;
    }

    default boolean isCustomElectricConnection(IElectric other, BlockState state, BlockState otherState) {
        return false;
    }
}
