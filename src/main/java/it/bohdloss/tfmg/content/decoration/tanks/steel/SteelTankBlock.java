package it.bohdloss.tfmg.content.decoration.tanks.steel;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SteelTankBlock extends FluidTankBlock {
    public static SteelTankBlock regular(Properties properties) {
        return new SteelTankBlock(properties, false);
    }

    protected SteelTankBlock(Properties properties, boolean creative) {
        super(properties, creative);
    }

    @Override
    public BlockEntityType<? extends FluidTankBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.STEEL_FLUID_TANK.get();
    }
}
