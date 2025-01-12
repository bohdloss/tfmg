package com.drmangotea.tfmg.content.items;

import com.drmangotea.tfmg.content.decoration.pipes.TFMGPipeBlockEntity;
import com.drmangotea.tfmg.content.decoration.pipes.TFMGPipes;
import com.drmangotea.tfmg.content.engines.AbstractEngineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;


public class ScrewdriverItem extends Item {
    public ScrewdriverItem(Properties p_40566_) {
        super( p_40566_);

    }
    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Player player = pContext.getPlayer();

        BlockPos positionClicked = pContext.getClickedPos();

        Level level = pContext.getLevel();

        if(level.getBlockEntity(positionClicked) instanceof AbstractEngineBlockEntity){



            return super.useOn(pContext);
        }
        if(level.getBlockEntity(positionClicked)!=null) {
            ((TFMGPipeBlockEntity) level.getBlockEntity(positionClicked)).toggleLock(player);
            pContext.getItemInHand().hurtAndBreak(1, pContext.getPlayer(),
                    (playerr) -> playerr.broadcastBreakEvent(playerr.getUsedItemHand()));
        }
        return InteractionResult.SUCCESS;
    }
}