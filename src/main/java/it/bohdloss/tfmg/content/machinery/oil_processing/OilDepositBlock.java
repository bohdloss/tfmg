package it.bohdloss.tfmg.content.machinery.oil_processing;

import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class OilDepositBlock extends Block implements IBE<OilDepositBlockEntity> {
    public OilDepositBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        withBlockEntityDo(level, pos, OilDepositBlockEntity::generateOil);
    }

    @Override
    public Class<OilDepositBlockEntity> getBlockEntityClass() {
        return OilDepositBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends OilDepositBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.OIL_DEPOSIT.get();
    }
}
