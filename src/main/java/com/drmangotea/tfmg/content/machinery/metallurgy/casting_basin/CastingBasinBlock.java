package com.drmangotea.tfmg.content.machinery.metallurgy.casting_basin;

import com.drmangotea.tfmg.base.TFMGHorizontalDirectionalBlock;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CastingBasinBlock extends TFMGHorizontalDirectionalBlock implements IBE<CastingBasinBlockEntity> {
    public CastingBasinBlock(Properties p_54120_) {
        super(p_54120_);
    }

    @Override
    public Class<CastingBasinBlockEntity> getBlockEntityClass() {
        return CastingBasinBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CastingBasinBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.CASTING_BASIN.get();
    }
}
