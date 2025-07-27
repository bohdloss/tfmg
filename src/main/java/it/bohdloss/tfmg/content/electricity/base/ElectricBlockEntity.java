package it.bohdloss.tfmg.content.electricity.base;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import it.bohdloss.tfmg.content.electricity.ElectricalNetwork;
import it.bohdloss.tfmg.content.electricity.ElectricityPropagator;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.GRAY;

public class ElectricBlockEntity extends SmartBlockEntity implements IElectric, IHaveGoggleInformation, IHaveHoveringInformation {
    @Nullable public Long electricalNetwork;
    @Nullable public BlockPos electricalSource;
    public boolean electricalNetworkDirty;
    public boolean updateVoltage;
    public int preventVoltageUpdate;

    protected ElectricEffectHandler electricEffects;
    protected float voltage;
    protected float power;
    protected float consumption;
    protected boolean shortCircuited;
    protected boolean wasMoved;

    private int voltageFlickerTally;
    private int electricalNetworkSize;
    private int electricValidationCountdown;
    protected float lastAmpsConsumed;
    protected float lastAmpsProvided;

    public ElectricBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        electricEffects = new ElectricEffectHandler(this);
        updateVoltage = true;
    }

    @Override
    public void initialize() {
        if (hasElectricalNetwork() && !level.isClientSide) {
            ElectricalNetwork network = getOrCreateElectricalNetwork();
            if (!network.initialized) {
                network.initFromTE(power, consumption, electricalNetworkSize);
            }
            network.addSilently(this, lastAmpsProvided, lastAmpsConsumed);
        }

        super.initialize();
    }

    @Override
    public void tick() {
        if (!level.isClientSide && needsVoltageUpdate()) {
            attachElectric();
        }

        super.tick();
        electricEffects.tick();

        preventVoltageUpdate = 0;

        if(level.isClientSide) {
            return;
        }

        if (electricValidationCountdown-- <= 0) {
            electricValidationCountdown = 60;
            validateElectric();
        }

        if (getVoltageFlickerScore() > 0) {
            voltageFlickerTally = getVoltageFlickerScore() - 1;
        }

        if (electricalNetworkDirty) {
            if (hasElectricalNetwork()) {
                getOrCreateElectricalNetwork().updateNetwork();
            }
            electricalNetworkDirty = false;
        }
    }

    @Override
    public void validateElectric() {
        if (hasElectricalSource()) {
            if (!hasElectricalNetwork()) {
                removeElectricalSource();
                return;
            }

            if (!level.isLoaded(electricalSource)) {
                return;
            }

            BlockEntity blockEntity = level.getBlockEntity(electricalSource);
            IElectric sourceBE = blockEntity instanceof IElectric ? (IElectric) blockEntity : null;

            if (sourceBE == null || sourceBE.getTheoreticalVoltage() == 0) {
                removeElectricalSource();
                detachElectric();
                return;
            }

            return;
        }

        if (voltage != 0) {
            if (getGeneratedVoltage() == 0) {
                voltage = 0;
            }
        }
    }

    @Override
    public void updateFromElectricalNetwork(float maxConsumption, float currentConsumption, int networkSize) {
        electricalNetworkDirty = false;
        this.power = maxConsumption;
        this.consumption = currentConsumption;
        this.electricalNetworkSize = networkSize;
        boolean shortCircuited = maxConsumption < currentConsumption;
        setChanged();

        if (shortCircuited != this.shortCircuited) {
            float prevVoltage = getVoltage();
            this.shortCircuited = shortCircuited;
//            if(level != null) {
//                DebugStuff.show((level.isClientSide ? "Client" : "Server") + " -> update -> " + shortCircuited);
//            }
            onVoltageChanged(prevVoltage);
            sendData();
        }
    }

    @Override
    public Block getPowerConfigKey() {
        return getBlockState().getBlock();
    }

    @Override
    public float calculateAmpsConsumed1Volt() {
        float resistance = getResistance();
        float amps = resistance == 0 ? 0 : 1f / resistance;
        this.lastAmpsConsumed = amps;
        return amps;
    }

    @Override
    public float calculateAmpsGenerated1Volt() {
        float resistance = getGeneratorResistance();
        float amps = resistance == 0 ? 0 : 1f / resistance;
        this.lastAmpsProvided = amps;
        return amps;
    }

    @Override
    public void onVoltageChanged(float previousVoltage) {
        boolean fromOrToZero = (previousVoltage == 0) != (getVoltage() == 0);
        boolean directionSwap = !fromOrToZero && Math.signum(previousVoltage) != Math.signum(getVoltage());
        if (fromOrToZero || directionSwap) {
            voltageFlickerTally = getVoltageFlickerScore() + 5;
        }
        setChanged();
    }

    @Override
    public void remove() {
        if (!level.isClientSide) {
            if (hasElectricalNetwork()) {
                getOrCreateElectricalNetwork().remove(this);
            }
            detachElectric();
        }
        super.remove();
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putFloat("Voltage", voltage);

        if (needsVoltageUpdate()) {
            compound.putBoolean("NeedsVoltageUpdate", true);
        }

        if (hasElectricalSource()) {
            compound.put("ElectricalSource", NbtUtils.writeBlockPos(electricalSource));
        }

        if (hasElectricalNetwork()) {
            CompoundTag networkTag = new CompoundTag();
            networkTag.putLong("Id", this.electricalNetwork);
            networkTag.putFloat("Consumption", consumption);
            networkTag.putFloat("Power", power);
            networkTag.putInt("Size", electricalNetworkSize);

            if (lastAmpsConsumed != 0)
                networkTag.putFloat("AddedConsumption", lastAmpsConsumed);
            if (lastAmpsProvided != 0)
                networkTag.putFloat("AddedPower", lastAmpsProvided);

            compound.put("ElectricalNetwork", networkTag);
        }

        super.write(compound, registries, clientPacket);
    }

    @Override
    public boolean needsVoltageUpdate() {
        return updateVoltage;
    }

    @Override
    public void setNeedsVoltageUpdate(boolean updateVoltage) {
        this.updateVoltage = updateVoltage;
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        boolean shortCircuitedBefore = shortCircuited;
        clearElectricInformation();

        // DO NOT READ kinetic information when placed after movement
        if (wasMoved) {
            super.read(compound, registries, clientPacket);
            return;
        }

        voltage = compound.getFloat("Voltage");

        electricalSource = null;
        if (compound.contains("ElectricalSource")) {
            electricalSource = NBTHelper.readBlockPos(compound, "ElectricalSource");
        }

        if (compound.contains("ElectricalNetwork")) {
            CompoundTag networkTag = compound.getCompound("ElectricalNetwork");
            electricalNetwork = networkTag.getLong("Id");
            consumption = networkTag.getFloat("Consumption");
            power = networkTag.getFloat("Power");
            electricalNetworkSize = networkTag.getInt("Size");
            lastAmpsConsumed = networkTag.getFloat("AddedConsumption");
            lastAmpsProvided = networkTag.getFloat("AddedPower");
            shortCircuited = power < consumption;// && IRotate.StressImpact.isEnabled();
        }

        super.read(compound, registries, clientPacket);

        if (clientPacket && shortCircuitedBefore != shortCircuited && voltage != 0) {
            electricEffects.triggerShortCircuitEffect();
        }

//        if (clientPacket)
//            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> VisualizationHelper.queueUpdate(this));
    }

    @Override
    public @Nullable ElectricEffectHandler getElectricEffects() {
        return electricEffects;
    }

    @Override
    public float getGeneratedVoltage() {
        return 0;
    }

    @Override
    public boolean isElectricalSource() {
        return getGeneratedVoltage() != 0;
    }

    @Override
    public float getVoltage() {
        if (shortCircuited || (level != null && level.tickRateManager().isFrozen())) {
            return 0;
        }
        return getTheoreticalVoltage();
    }

    @Override
    public float getTheoreticalVoltage() {
        return voltage;
    }

    @Override
    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    @Override
    public boolean hasElectricalSource() {
        return electricalSource != null;
    }

    @Override
    public void setElectricalSource(BlockPos source) {
        this.electricalSource = source;
        if (level == null || level.isClientSide) {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(source);
        if (!(blockEntity instanceof IElectric sourceBE)) {
            removeElectricalSource();
            return;
        }

        setElectricalNetwork(sourceBE.getElectricalNetwork());
    }

    @Override
    public void removeElectricalSource() {
        float prevVoltage = getVoltage();

        voltage = 0;
        electricalSource = null;
        setElectricalNetwork(null);

        onVoltageChanged(prevVoltage);
    }

    @Override
    public BlockPos getElectricalSource() {
        return electricalSource;
    }

    @Override
    public void setElectricalNetwork(@Nullable Long networkIn) {
        if (Objects.equals(electricalNetwork, networkIn)) {
            return;
        }
        if (electricalNetwork != null) {
            getOrCreateElectricalNetwork().remove(this);
        }

        electricalNetwork = networkIn;
        setChanged();

        if (networkIn == null) {
            return;
        }

        electricalNetwork = networkIn;
        ElectricalNetwork network = getOrCreateElectricalNetwork();
        network.initialized = true;
        network.add(this);
    }

    @Override
    public boolean hasElectricalNetwork() {
        return electricalNetwork != null;
    }

    @Override
    public void attachElectric() {
        updateVoltage = false;
        ElectricityPropagator.handleAdded(level, worldPosition, this);
    }

    @Override
    public void detachElectric() {
        ElectricityPropagator.handleRemoved(level, worldPosition, this);
    }

    public static void switchToBlockState(Level world, BlockPos pos, BlockState state) {
        if (world.isClientSide) {
            return;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockState currentState = world.getBlockState(pos);
        boolean isElectric = blockEntity instanceof IElectric;

        if (currentState == state) {
            return;
        }
        if (blockEntity == null || !isElectric) {
            world.setBlock(pos, state, 3);
            return;
        }

        IElectric electricBlockEntity = (IElectric) blockEntity;
        if (state.getBlock() instanceof IElectricBlock && !((IElectricBlock) state.getBlock()).areStatesElectricallyEquivalent(currentState, state)) {
            if (electricBlockEntity.hasElectricalNetwork()) {
                electricBlockEntity.getOrCreateElectricalNetwork().remove(electricBlockEntity);
            }
            electricBlockEntity.detachElectric();
            electricBlockEntity.removeElectricalSource();
        }

        if (blockEntity instanceof IGeneratingElectric generatingBlockEntity) {
            generatingBlockEntity.setReactivateElectricalSource(true);
        }

        world.setBlock(pos, state, 3);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    public static boolean extraDebug = false;

    @Override
    public void clearElectricInformation() {
        voltage = 0;
        electricalSource = null;
        electricalNetwork = null;
        shortCircuited = false;
        consumption = 0;
        power = 0;
        lastAmpsConsumed = 0;
        lastAmpsProvided = 0;
    }

    @Override
    public void warnOfMovementElectrical() {
        wasMoved = true;
    }

    @Override
    public int getVoltageFlickerScore() {
        return voltageFlickerTally;
    }

    @Override
    public boolean isShortCircuited() {
        return shortCircuited;
    }

    @Override
    public @Nullable Long getElectricalNetwork() {
        return electricalNetwork;
    }

    @Override
    public boolean isElectricalNetworkDirty() {
        return electricalNetworkDirty;
    }

    @Override
    public void setElectricalNetworkDirty(boolean dirty) {
        this.electricalNetworkDirty = dirty;
    }

    @Override
    public int getPreventVoltageUpdate() {
        return preventVoltageUpdate;
    }

    @Override
    public void setPreventVoltageUpdate(int preventVoltageUpdate) {
        this.preventVoltageUpdate = preventVoltageUpdate;
    }

    @Override
    public void nullElectricalSource() {
        this.electricalSource = null;
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (shortCircuited && AllConfigs.client().enableOverstressedTooltip.get()) {
            CreateLang.translate("gui.stressometer.shorted")
                    .style(GOLD)
                    .forGoggles(tooltip);
            Component hint = CreateLang.translateDirect("gui.contraptions.network_shorted");
            List<Component> cutString = TooltipHelper.cutTextComponent(hint, FontHelper.Palette.GRAY_AND_WHITE);
            for (Component component : cutString)
                CreateLang.builder()
                        .add(component
                                .copy())
                        .forGoggles(tooltip);
            return true;
        }

        return false;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean added = false;

        float powerAtBase = calculateAmpsConsumed1Volt();
        if (Mth.equal(powerAtBase, 0))
            return added;

        CreateLang.translate("gui.goggles.electric_stats")
                .forGoggles(tooltip);

        addPowerConsumptionStats(tooltip, powerAtBase);

        return true;
    }

    protected void addPowerConsumptionStats(List<Component> tooltip, float powerAtBase) {
        CreateLang.translate("tooltip.powerConsumed")
                .style(GRAY)
                .forGoggles(tooltip);

        float powerTotal = powerAtBase * Math.abs(getTheoreticalVoltage());

        CreateLang.number(powerTotal)
                .translate("generic.unit.current")
                .style(ChatFormatting.AQUA)
                .space()
                .add(CreateLang.translate("gui.goggles.at_current_voltage")
                        .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);
    }
}
