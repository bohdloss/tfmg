package it.bohdloss.tfmg.content.machinery.oil_processing.distillation_tower.output;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class DistillationOutputBlock extends Block implements IBE<DistillationOutputBlockEntity>, IWrenchable {
    public DistillationOutputBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<DistillationOutputBlockEntity> getBlockEntityClass() {
        return DistillationOutputBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DistillationOutputBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.STEEL_DISTILLATION_OUTPUT.get();
    }
}
