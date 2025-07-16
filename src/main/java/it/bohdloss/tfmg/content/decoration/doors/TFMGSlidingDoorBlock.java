package it.bohdloss.tfmg.content.decoration.doors;

import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlockEntity;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class TFMGSlidingDoorBlock extends SlidingDoorBlock {
    public TFMGSlidingDoorBlock(Properties properties, BlockSetType type, boolean folds) {
        super(properties, type, folds);
    }

    @Override
    public BlockEntityType<? extends SlidingDoorBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.TFMG_SLIDING_DOOR.get();
    }
}
