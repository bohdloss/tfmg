package it.bohdloss.tfmg.content.decoration.encased;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class TFMGEncasedCogwheelBlock extends EncasedCogwheelBlock {
    private final Supplier<Block> cogwheelBlock;
    private final Supplier<Block> largeCogwheelBlock;

    public TFMGEncasedCogwheelBlock(Properties properties, boolean large, Supplier<Block> casing, Supplier<Block> cogwheelBlock, Supplier<Block> largeCogwheelBlock) {
        super(properties, large, casing);
        this.cogwheelBlock = cogwheelBlock;
        this.largeCogwheelBlock = largeCogwheelBlock;
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (context.getLevel().isClientSide)
            return InteractionResult.SUCCESS;
        context.getLevel()
                .levelEvent(2001, context.getClickedPos(), Block.getId(state));
        KineticBlockEntity.switchToBlockState(context.getLevel(), context.getClickedPos(),
                (isLarge ? largeCogwheelBlock.get() : cogwheelBlock.get()).defaultBlockState()
                        .setValue(AXIS, state.getValue(AXIS)));
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntityType<? extends SimpleKineticBlockEntity> getBlockEntityType() {
        return isLarge ? TFMGBlockEntities.ENCASED_LARGE_COGWHEEL.get() : TFMGBlockEntities.ENCASED_COGWHEEL.get();
    }
}
