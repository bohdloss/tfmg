package com.drmangotea.tfmg.content.engines.engine_gearbox;

import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity;

public class EngineGearboxInstance extends ShaftInstance<GearboxBlockEntity> {
    public EngineGearboxInstance(MaterialManager materialManager, GearboxBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }
}
