package it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.base;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class PumpjackBaseBlock extends Block implements IBE<PumpjackBaseBlockEntity>, IWrenchable {
    public PumpjackBaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return TFMGShapes.PUMPJACK_BASE;
    }

    @Override
    public Class<PumpjackBaseBlockEntity> getBlockEntityClass() {
        return PumpjackBaseBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PumpjackBaseBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.PUMPJACK_BASE.get();
    }
}
