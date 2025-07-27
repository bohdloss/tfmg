package it.bohdloss.tfmg.content.electricity.connection.cable_hub;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.content.electricity.base.ElectricBlock;
import it.bohdloss.tfmg.content.electricity.base.ElectricBlockEntity;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CableHubBlock extends ElectricBlock implements IBE<ElectricBlockEntity>, IWrenchable {
    public CableHubBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasConnectorTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return true;
    }

    @Override
    public Class<ElectricBlockEntity> getBlockEntityClass() {
        return ElectricBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ElectricBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.CABLE_HUB.get();
    }
}
