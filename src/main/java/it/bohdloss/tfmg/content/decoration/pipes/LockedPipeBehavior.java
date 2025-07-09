package it.bohdloss.tfmg.content.decoration.pipes;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class LockedPipeBehavior extends BlockEntityBehaviour {
    public static final BehaviourType<LockedPipeBehavior> TYPE = new BehaviourType<>();

    public boolean locked;

    public LockedPipeBehavior(SmartBlockEntity be) {
        super(be);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        nbt.putBoolean("locked", locked);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        locked = nbt.getBoolean("locked");
    }
}
