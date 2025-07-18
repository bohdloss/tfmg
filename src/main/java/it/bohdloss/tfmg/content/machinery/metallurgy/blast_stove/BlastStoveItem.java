package it.bohdloss.tfmg.content.machinery.metallurgy.blast_stove;

import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class BlastStoveItem extends BlockItem {
    public BlastStoveItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull InteractionResult place(@NotNull BlockPlaceContext ctx) {
        InteractionResult initialResult = super.place(ctx);
        if (!initialResult.consumesAction())
            return initialResult;
        TFMGUtils.tryMultiPlace(ctx, TFMGBlockEntities.BLAST_STOVE.get(), super::place, BlastStoveBlock::isBlastStove);
        return initialResult;
    }
}
