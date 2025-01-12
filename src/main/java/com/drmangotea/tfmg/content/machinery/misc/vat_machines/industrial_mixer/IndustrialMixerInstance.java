package com.drmangotea.tfmg.content.machinery.misc.vat_machines.industrial_mixer;

import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.content.kinetics.base.HalfShaftInstance;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class IndustrialMixerInstance<T extends KineticBlockEntity> extends HalfShaftInstance<T> {

    public IndustrialMixerInstance(MaterialManager materialManager, T blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    protected Direction getShaftDirection() {
        return Direction.UP;
    }
}