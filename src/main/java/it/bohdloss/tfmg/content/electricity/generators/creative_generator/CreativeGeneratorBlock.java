package it.bohdloss.tfmg.content.electricity.generators.creative_generator;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.content.electricity.base.ElectricBlock;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeGeneratorBlock extends ElectricBlock implements IBE<CreativeGeneratorBlockEntity>, IWrenchable {
    public CreativeGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasConnectorTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return true;
    }

    @Override
    public Class<CreativeGeneratorBlockEntity> getBlockEntityClass() {
        return CreativeGeneratorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CreativeGeneratorBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.CREATIVE_GENERATOR.get();
    }
}
