package com.drmangotea.tfmg.content.electricity.base;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.base.TFMGUtils;
import com.drmangotea.tfmg.registry.TFMGPackets;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;


public interface IElectric {
    long getPos();

    LevelAccessor getLevelAccessor();

    boolean destroyed();

    ElectricalNetwork getOrCreateElectricNetwork();

    default boolean hasElectricitySlot(Direction direction) {
        return true;
    }

    default void onPlaced() {

        if (!getLevelAccessor().isClientSide())
            TFMGPackets.getChannel().send(PacketDistributor.ALL.noArg(), new ConnectNeightborsPacket(BlockPos.of(getPos())));
        ElectricalNetwork network = TFMG.NETWORK_MANAGER.getOrCreateNetworkFor(this);
        setNetwork(getPos());
        getData().electricalNetworkId = getPos();

        //BlockPos pos = BlockPos.of(getPos());
        ///// ////
//
//
        //for (Direction d : Direction.values()) {
        //    if (hasElectricitySlot(d))
        //        if (getLevelAccessor().getBlockEntity(pos.relative(d)) instanceof IElectric be) {
        //            if (be.hasElectricitySlot(d.getOpposite())) {
        //                if (!be.destroyed()) {
//
//
        //                    for(IElectric member : be.getOrCreateElectricNetwork().members){
        //                        network.add(member);
        //                        member.setNetwork(this.getData().electricalNetworkId);
//
        //                    }
//
        //                }
        //            }
        //        }
        //}
        //updateNextTick();

        onConnected();
        sendStuff();

    }

    default int getMaxVoltage() {
        return 0;
    }

    default int getMaxCurrent() {
        return 0;
    }

    default void onConnected() {


        BlockPos pos = BlockPos.of(getPos());
        for (Direction d : Direction.values()) {
            if (hasElectricitySlot(d))
                if (getLevelAccessor().getBlockEntity(pos.relative(d)) instanceof IElectric be) {
                    if (be.hasElectricitySlot(d.getOpposite())) {
                        if (!be.destroyed()) {
                            getOrCreateElectricNetwork().add(be);
                            if (be.getData().getId() != getData().getId()) {
                                be.setNetwork(getData().getId());
                                be.onConnected();
                                if (!getLevelAccessor().isClientSide())
                                    sendStuff();
                            }
                        }
                    } else if (be.getData().getId() != getData().getId()) {
                        be.updateNextTick();
                    }
                }
        }
        sendStuff();


    }


    default boolean makeElectricityTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        Lang.number(getOrCreateElectricNetwork().members.size()).forGoggles(tooltip, 1);
        if (getData().failTimer > 0)
            Lang.text("Skill issue: " + getData().failTimer).forGoggles(tooltip, 1);
        Lang.text("X " + BlockPos.of(getData().electricalNetworkId).getX() + " Y " + BlockPos.of(getData().electricalNetworkId).getY() + " Z " + BlockPos.of(getData().electricalNetworkId).getZ()).forGoggles(tooltip, 1);
        Lang.translate("multimeter.header")
                .style(ChatFormatting.WHITE)
                .forGoggles(tooltip, 1);
        Lang.text("   R = " + TFMGUtils.formatUnits(voltageGeneration() > 0 ? getGeneratorResistance() : resistance(), "Ω"))
                .style(ChatFormatting.GOLD)
                .forGoggles(tooltip, 1);
        Lang.text("   P = " + TFMGUtils.formatUnits(getPowerUsage(), "W"))
                .style(ChatFormatting.GOLD)
                .forGoggles(tooltip, 1);
        Lang.text("   U = " + TFMGUtils.formatUnits(getData().getVoltage(), "V"))
                .style(ChatFormatting.AQUA)
                .forGoggles(tooltip, 1);
        Lang.text("   I = " + TFMGUtils.formatUnits(getCurrent(), "A"))
                .style(ChatFormatting.GREEN)
                .forGoggles(tooltip, 1);

        ////////
        Lang.text("   Network Resistance: " + TFMGUtils.formatUnits(getNetworkResistance(), "Ω"))
                .style(ChatFormatting.YELLOW)
                .forGoggles(tooltip, 1);
        Lang.text("   Max Current: " + TFMGUtils.formatUnits(getData().highestCurrent, "A"))
                .style(ChatFormatting.AQUA)
                .forGoggles(tooltip, 1);
        Lang.text("   Network Power Usage: " + TFMGUtils.formatUnits(getNetworkPowerUsage(), "W"))
                .style(ChatFormatting.YELLOW)
                .forGoggles(tooltip, 1);
        Lang.text("   Group: " + getData().group.id)
                .style(ChatFormatting.DARK_PURPLE)
                .forGoggles(tooltip, 1);
        Lang.text("   Group Resistance: " + getData().group.resistance)
                .style(ChatFormatting.DARK_PURPLE)
                .forGoggles(tooltip, 1);
        Lang.text("   Voltage Supply: " + getData().voltageSupply)
                .style(ChatFormatting.DARK_PURPLE)
                .forGoggles(tooltip, 1);


        if (voltageGeneration() > 0) {
            Lang.translate("multimeter.power_generated")
                    .add(Component.literal(TFMGUtils.formatUnits(powerGeneration(), "W")))
                    .style(ChatFormatting.BLUE)
                    .forGoggles(tooltip, 1);
            Lang.translate("multimeter.voltage_generated")
                    .add(Component.literal(TFMGUtils.formatUnits(voltageGeneration(), "V")))
                    .style(ChatFormatting.BLUE)
                    .forGoggles(tooltip, 1);
        }


        return true;
    }

    default void updateNearbyNetworks(IElectric member) {
        if (member.getData().getsOutsidePower)
            for (Direction direction : Direction.values()) {
                if (member.getLevelAccessor().getBlockEntity(BlockPos.of(member.getPos()).relative(direction)) instanceof IElectric be && be.getData().getId() != be.getData().getId()) {
                    be.updateNextTick();
                }
            }

    }

    default boolean isCable() {
        return false;
    }

    ElectricBlockValues getData();

    default void blockFail() {
        getLevelAccessor().destroyBlock(BlockPos.of(getPos()), false);
    }

    default int getPowerUsage() {
        return (int) (getData().getVoltage() * getCurrent());
    }

    default int getNetworkPowerUsage(IElectric blocked) {
        int power = 0;
        for (IElectric member : getOrCreateElectricNetwork().members)
            if (member.getPos() != blocked.getPos()) {
                power += member.getPowerUsage();
            } else blocked.updateNextTick();
        return power;
    }

    default int getNetworkPowerUsage() {
        int power = 0;
        for (IElectric member : getOrCreateElectricNetwork().members)

            power += member.getPowerUsage();
        return power;
    }

    default void onNetworkChanged(int oldVoltage, int oldPower) {
    }

    default float getGeneratorResistance() {
        if (getData().voltageSupply == 0)
            return 0;
        return (float) powerGeneration() / (float) getData().networkPowerGeneration * (float) getNetworkResistance();
    }

    default float getGeneratorLoad() {
        if (getNetworkPowerUsage() == 0)
            return 0;
        return (float) powerGeneration() / (float) getData().networkPowerGeneration * getNetworkPowerUsage();
    }

    int getPowerPercentage();

    float resistance();

    int voltageGeneration();

    int powerGeneration();

    int frequencyGeneration();

    int getNetworkResistance();

    default int getMaxAmps() {
        return (int) getCurrent();
    }

    default float getCurrent() {
        return getData().getVoltage() == 0 || resistance() == 0 ? 0 : ((float) getData().getVoltage() / (float) resistance());
    }

    default float getCableCurrent() {
        float current = 0;
        List<Integer> groups = new ArrayList<>();
        for (IElectric member : getOrCreateElectricNetwork().members) {
            if (member.canBeInGroups())
                if (!groups.contains(member.getData().group.id)) {
                    current += member.getCurrent();
                    groups.add(member.getData().group.id);
                }
        }
        return current;
    }

    void updateNextTick();

    void updateNetwork();

    void sendStuff();

    void setVoltage(int newVoltage);

    void setFrequency(int newFrequency);

    void setNetworkResistance(int newUsage);

    void setWattage(int newWattage);

    void setPowerPercentage(int percentage);

    void setNetwork(long network);

    default boolean canBeInGroups() {
        return false;
    }


}