package it.bohdloss.tfmg.base;

import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SecondaryBehavior<T extends BlockEntityBehaviour> extends BlockEntityBehaviour {
    public static final BehaviourType<SecondaryBehavior<?>> TYPE = new BehaviourType<>();

    private final T behaviour;

    public SecondaryBehavior(T behaviour) {
        super(behaviour.blockEntity);
        this.behaviour = behaviour;
    }

    public T get() {
        return behaviour;
    }

    @Override
    public void initialize() {
        behaviour.initialize();
    }

    @Override
    public void unload() {
        behaviour.unload();
    }

    @Override
    public void destroy() {
        behaviour.destroy();
    }

    @Override
    public void tick() {
        behaviour.tick();
    }

    @Override
    public void lazyTick() {
        behaviour.lazyTick();
    }

    @Override
    public void setLazyTickRate(int slowTickRate) {
        behaviour.setLazyTickRate(slowTickRate);
    }

    @Override
    public void onBlockChanged(BlockState oldState) {
        behaviour.onBlockChanged(oldState);
    }

    @Override
    public void onNeighborChanged(BlockPos neighborPos) {
        behaviour.onNeighborChanged(neighborPos);
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        behaviour.write(nbt, registries, clientPacket);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        behaviour.read(nbt, registries, clientPacket);
    }

    @Override
    public boolean isSafeNBT() {
        return behaviour.isSafeNBT();
    }

    @Override
    public void writeSafe(CompoundTag nbt, HolderLookup.Provider registries) {
        behaviour.writeSafe(nbt, registries);
    }

    @Override
    public ItemRequirement getRequiredItems() {
        return behaviour.getRequiredItems();
    }

    @Override
    public BlockPos getPos() {
        return behaviour.getPos();
    }

    @Override
    public Level getWorld() {
        return behaviour.getWorld();
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
