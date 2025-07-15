package it.bohdloss.tfmg.content.decoration.tanks.cast_iron.aluminum;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CastIronTankBlock extends FluidTankBlock {
    public static CastIronTankBlock regular(Properties properties) {
        return new CastIronTankBlock(properties, false);
    }

    protected CastIronTankBlock(Properties properties, boolean creative) {
        super(properties, creative);
    }

    @Override
    public BlockEntityType<? extends FluidTankBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.CAST_IRON_FLUID_TANK.get();
    }
}
