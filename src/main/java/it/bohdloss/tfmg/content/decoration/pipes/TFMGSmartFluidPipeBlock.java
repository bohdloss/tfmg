package it.bohdloss.tfmg.content.decoration.pipes;

import com.simibubi.create.content.fluids.pipes.SmartFluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.SmartFluidPipeBlockEntity;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TFMGSmartFluidPipeBlock extends SmartFluidPipeBlock {
    public TFMGSmartFluidPipeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends SmartFluidPipeBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.TFMG_SMART_FLUID_PIPE.get();
    }
}
