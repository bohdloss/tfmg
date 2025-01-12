package com.drmangotea.tfmg.content.machinery.metallurgy.blast_furnace;

import com.drmangotea.tfmg.base.TFMGHorizontalDirectionalBlock;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlastFurnaceOutputBlock extends TFMGHorizontalDirectionalBlock implements IBE<BlastFurnaceOutputBlockEntity> {
    public BlastFurnaceOutputBlock(Properties properties) {
        super(properties);
    }



    @Override
    public Class<BlastFurnaceOutputBlockEntity> getBlockEntityClass() {
        return BlastFurnaceOutputBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BlastFurnaceOutputBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.BLAST_FURNACE_OUTPUT.get();
    }
}
