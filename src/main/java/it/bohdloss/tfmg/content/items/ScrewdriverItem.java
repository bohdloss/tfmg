package it.bohdloss.tfmg.content.items;

import it.bohdloss.tfmg.content.decoration.pipes.ILockablePipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ScrewdriverItem extends Item {
    public ScrewdriverItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext pContext) {
        Player player = pContext.getPlayer();
        BlockPos positionClicked = pContext.getClickedPos();
        Level level = pContext.getLevel();
        if(player == null) {
            return InteractionResult.PASS;
        }

//        if(level.getBlockEntity(positionClicked) instanceof AbstractEngineBlockEntity){ // TODO when engines are added
//            return super.useOn(pContext);
//        }
        if(level.getBlockEntity(positionClicked) instanceof ILockablePipe pipe) {
            if(pipe.tfmg$canPipeBeLocked()) {
                boolean wasLocked = pipe.tfmg$isPipeLocked();
                pipe.tfmg$setPipeLocked(player, !wasLocked);
                if (wasLocked) {
                    player.displayClientMessage(Component.translatable("item.tfmg.screwdriver.pipeUnlocked"), true);
                } else {
                    player.displayClientMessage(Component.translatable("item.tfmg.screwdriver.pipeLocked"), true);
                }

                pContext.getItemInHand().hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
