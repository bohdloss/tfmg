package it.bohdloss.tfmg.content.decoration.encased;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class TFMGEncasedShaftBlock extends EncasedShaftBlock {
    public TFMGEncasedShaftBlock(Properties properties, Supplier<Block> casing) {
        super(properties, casing);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return TFMGBlockEntities.TFMG_ENCASED_SHAFT.create(blockPos, blockState);
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.TFMG_ENCASED_SHAFT.get();
    }
}
