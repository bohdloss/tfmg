package it.bohdloss.tfmg.content.electricity.base;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class GeneratingElectricBlockEntity extends ElectricBlockEntity implements IGeneratingElectric {
    public boolean reactiveSource;

    public GeneratingElectricBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void removeElectricalSource() {
        if (hasElectricalSource() && isElectricalSource()) {
            reactiveSource = true;
        }
        super.removeElectricalSource();
    }

    @Override
    public void setElectricalSource(BlockPos source) {
        super.setElectricalSource(source);
        BlockEntity blockEntity = level.getBlockEntity(source);
        if (!(blockEntity instanceof IElectric sourceBE)) {
            return;
        }
        if (reactiveSource && Math.abs(sourceBE.getVoltage()) >= Math.abs(getGeneratedVoltage())) {
            reactiveSource = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (reactiveSource) {
            updateGeneratedVoltage();
            reactiveSource = false;
        }
    }

    @Override
    public void setReactivateElectricalSource(boolean reactivateSource) {
        this.reactiveSource = reactivateSource;
    }

    @Override
    public boolean getReactivateElectricalSource() {
        return reactiveSource;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        if (!IRotate.StressImpact.isEnabled())
            return added;

        float powerAtBase = calculateAmpsGenerated1Volt();
        if (Mth.equal(powerAtBase, 0)) {
            return added;
        }

        CreateLang.translate("gui.goggles.electric_generator_stats")
                .forGoggles(tooltip);
        CreateLang.translate("tooltip.powerProvided")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);

        float voltage = getTheoreticalVoltage();
        if (voltage != getGeneratedVoltage() && voltage != 0) {
            powerAtBase *= getGeneratedVoltage() / voltage;
        }

        float powerTotal = Math.abs(powerAtBase * voltage);

        CreateLang.number(powerTotal)
                .translate("generic.unit.current")
                .style(ChatFormatting.AQUA)
                .space()
                .add(CreateLang.translate("gui.goggles.at_current_voltage")
                        .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);

        return true;
    }
}
