package it.bohdloss.tfmg.content.decoration.tanks.aluminum;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AluminumTankBlock extends FluidTankBlock {
    public static AluminumTankBlock regular(Properties properties) {
        return new AluminumTankBlock(properties, false);
    }

    protected AluminumTankBlock(Properties properties, boolean creative) {
        super(properties, creative);
    }

    @Override
    public BlockEntityType<? extends FluidTankBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.ALUMINUM_FLUID_TANK.get();
    }
}
