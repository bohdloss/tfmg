package it.bohdloss.tfmg.content.decoration.cogs;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TFMGCogwheelBlock extends CogWheelBlock {
    protected TFMGCogwheelBlock(boolean large, Properties properties) {
        super(large, properties);
    }

    public static TFMGCogwheelBlock small(Properties properties) {
        return new TFMGCogwheelBlock(false, properties);
    }

    public static TFMGCogwheelBlock large(Properties properties) {
        return new TFMGCogwheelBlock(true, properties);
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.TFMG_COGWHEEL.get();
    }
}
