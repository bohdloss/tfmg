package it.bohdloss.tfmg.content.electricity.base;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ElectricKineticBlockEntity extends GeneratingKineticBlockEntity implements IElectric {
    public final ElectricData electricData = instantiateElectric();

    public ElectricKineticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected ElectricData instantiateElectric() {
        return new ElectricData(this);
    }

    @Override
    public void tick() {
        super.tick();
        electricData.tick();
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        electricData.lazyTick();
    }

    @Override
    public void remove() {
        super.remove();
        electricData.remove();
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        tag.put("ElectricalData", electricData.write(registries, clientPacket));

        super.write(tag, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        electricData.read(tag.getCompound("ElectricalData"), registries, clientPacket);

        super.read(tag, registries, clientPacket);
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return super.addToTooltip(tooltip, isPlayerSneaking) | electricData.addToTooltip(tooltip, isPlayerSneaking);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking) | electricData.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    @Override
    public ElectricData getElectricData() {
        return electricData;
    }
}
