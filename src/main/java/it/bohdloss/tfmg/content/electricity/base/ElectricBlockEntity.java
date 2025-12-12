package it.bohdloss.tfmg.content.electricity.base;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import it.bohdloss.tfmg.TFMG;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ElectricBlockEntity extends SmartBlockEntity implements IElectric, IHaveGoggleInformation, IHaveHoveringInformation {
    private ElectricData electricData;

    public ElectricBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected ElectricData instantiateElectric() {
        return new ElectricData(this);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    public void tick() {
        super.tick();
        getElectricData().tick();
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        getElectricData().lazyTick();
    }

    @Override
    public void remove() {
        super.remove();
        getElectricData().remove();
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        tag.put("ElectricalData", getElectricData().write(registries, clientPacket));

        super.write(tag, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        getElectricData().read(tag.getCompound("ElectricalData"), registries, clientPacket);

        super.read(tag, registries, clientPacket);
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return getElectricData().addToTooltip(tooltip, isPlayerSneaking);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return getElectricData().addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    @Override
    public ElectricData getElectricData() {
        if(electricData == null) {
            electricData = instantiateElectric();
        }
        return electricData;
    }
}
