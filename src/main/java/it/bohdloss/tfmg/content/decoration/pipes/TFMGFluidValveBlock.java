package it.bohdloss.tfmg.content.decoration.pipes;

import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlock;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlockEntity;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TFMGFluidValveBlock extends FluidValveBlock {
    public TFMGFluidValveBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends FluidValveBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.TFMG_FLUID_VALVE.get();
    }
}
