package com.drmangotea.tfmg.content.electricity.base;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.registry.TFMGPackets;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class KineticElectricBlockEntity extends GeneratingKineticBlockEntity implements IElectric, IHaveGoggleInformation, IHaveHoveringInformation {

    public ElectricBlockValues data = new ElectricBlockValues(getPos());
    int powerPercentage = 100;

    int timer = 0;


    public KineticElectricBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        data.connectNextTick = true;
    }



    //@Override
    //public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
    //    CreateLang.text("MAX POWER: "+getNetworkPowerGeneration()).forGoggles(tooltip);
    //    return makeElectricityTooltip(tooltip, isPlayerSneaking);
    //}

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    public LevelAccessor getLevelAccessor(){
        return level;
    }

    @Override
    public boolean destroyed() {
        return data.destroyed;
    }

    @Override
    public ElectricalNetwork getOrCreateElectricNetwork() {
        if(level.getBlockEntity(BlockPos.of(data.electricalNetworkId)) instanceof IElectric) {
            return TFMG.NETWORK_MANAGER.getOrCreateNetworkFor((IElectric) level.getBlockEntity(BlockPos.of(data.electricalNetworkId)));
        } else {
            ElectricNetworkManager.networks.get(getLevel())
                    .remove(data.electricalNetworkId);
            return TFMG.NETWORK_MANAGER.getOrCreateNetworkFor(this);
        }
    }

    @Override
    public ElectricBlockValues getData() {
        return data;
    }




    @Override
    public float resistance() {
        return 0;
    }

    @Override
    public int voltageGeneration() {

        int voltageGeneration = 0;

        for(Direction direction : Direction.values()){
            if(hasElectricitySlot(direction)){

                if(level.getBlockEntity(getBlockPos().relative(direction)) instanceof VoltageAlteringBlockEntity be)
                    if(be.getData().getId() !=getData().getId())
                        if(be.getData().getVoltage()!=0)
                            if(be.hasElectricitySlot(direction)){
                                voltageGeneration = Math.max(voltageGeneration,be.getOutputVoltage());
                                data.getsOutsidePower = true;
                            }
            }
        }

        if(voltageGeneration == 0)
            data.getsOutsidePower = false;

        return voltageGeneration;
    }

    @Override
    public int powerGeneration() {

        int powerGeneration = 0;

        for(Direction direction : Direction.values()){
            if(hasElectricitySlot(direction)){

                if(level.getBlockEntity(getBlockPos().relative(direction)) instanceof VoltageAlteringBlockEntity be)
                    if(be.getData().getId() !=getData().getId())
                        if(be.getData().getVoltage()!=0)
                            if(be.hasElectricitySlot(direction)){
                                powerGeneration = Math.max(powerGeneration,be.getOutputPower())+10;
                            }
            }
        }

        return powerGeneration;
    }

    @Override
    public int frequencyGeneration() {
        return 0;
    }


    @Override
    public void updateNextTick() {
        data.updateNextTick = true;
    }

    @Override
    public void updateNetwork() {
        getOrCreateElectricNetwork().updateNetwork();
        if(!level.isClientSide)
            TFMGPackets.getChannel().send(PacketDistributor.ALL.noArg(), new NetworkUpdatePacket(BlockPos.of(getPos())));
        sendData();
    }

    @Override
    public void sendStuff() {
        sendData();
    }

    @Override
    public void setVoltage(int newVoltage) {

        if(canBeInGroups()){
            data.voltage = (int) (((float)resistance()/data.group.resistance)*(float)data.voltageSupply);
            return;
        }
        data.voltage = newVoltage;
    }

    @Override
    public void setFrequency(int newFrequency) {
        data.frequency = newFrequency;
    }

    @Override
    public void setNetworkResistance(int newUsage) {
        data.networkResistance = newUsage;
    }

    @Override
    public int getNetworkResistance() {
        return data.networkResistance;
    }



    @Override
    public void setNetwork(long network) {
        this.data.electricalNetworkId = network;
        if(network!=getPos())
            ElectricNetworkManager.networks.get(getLevel())
                    .remove(getPos());
    }

    @Override
    public long getPos() {
        return getBlockPos().asLong();
    }

    @Override
    public void remove() {
        super.remove();
        this.data.destroyed = true;
        for (Direction d : Direction.values()) {
            if (hasElectricitySlot(d))
                if (getLevelAccessor().getBlockEntity(BlockPos.of(getPos()).relative(d)) instanceof IElectric be && be.hasElectricitySlot(d.getOpposite())) {
                    ElectricNetworkManager.networks.get(getLevel())
                            .remove(be.getPos());
                    be.setNetwork(be.getPos());
                    be.getData().connectNextTick = true;
                    be.updateNextTick();
                }
        }
        if (data.electricalNetworkId != getPos())
            getOrCreateElectricNetwork().getMembers().remove(this);

        if (data.electricalNetworkId == getPos())
            ElectricNetworkManager.networks.get(getLevel())
                    .remove(getData().getId());
    }

    @Override
    public void tick() {
        super.tick();

        if(data.connectNextTick) {
            onPlaced();
            data.connectNextTick = false;
        }
        if(data.updateNextTick) {
            updateNetwork();
            data.updateNextTick = false;
        }

    }

    @Override
    public void lazyTick() {
        super.lazyTick();

        if(data.failTimer >=4){
            this.blockFail();
            data.failTimer = 0;
            sendStuff();
        } else if((data.voltage>getMaxVoltage()&&getMaxVoltage()>0)||(getCurrent()>getMaxCurrent()&&getMaxCurrent()>0)){
            blockFail();
            data.failTimer++;
        }
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("GroupId", data.group.id);
        compound.putFloat("GroupResistance", data.group.resistance);

        compound.putFloat("MotorSpeed", getSpeed());
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        data.group = new ElectricalGroup(compound.getInt("GroupId"));
        data.group.resistance = compound.getFloat("GroupResistance");
        setSpeed(compound.getFloat("MotorSpeed"));
        if(!clientPacket)
            data.connectNextTick = true;

    }



    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        TFMG.LOGGER.debug("SPEEED CHANGED "+getBlockPos().getX()+" "+getBlockPos().getY()+" "+getBlockPos().getZ());
        notifyNetworkAboutSpeedChange();
        timer = 0;

    }
    public void notifyNetworkAboutSpeedChange(){
        updateNextTick();
    }
}
