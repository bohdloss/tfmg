package it.bohdloss.tfmg.content.decoration.pipes;

import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TFMGPumpBlock extends PumpBlock {
    public TFMGPumpBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends PumpBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.TFMG_MECHANICAL_PUMP.get();
    }
}
