package it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.hammer;

import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class PumpjackBlock extends BearingBlock implements IBE<PumpjackBlockEntity>, IWrenchable {
    public static final BooleanProperty WIDE = BooleanProperty.create("wide");

    public PumpjackBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(WIDE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public Class<PumpjackBlockEntity> getBlockEntityClass() {
        return PumpjackBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PumpjackBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.PUMPJACK_HAMMER.get();
    }
}
