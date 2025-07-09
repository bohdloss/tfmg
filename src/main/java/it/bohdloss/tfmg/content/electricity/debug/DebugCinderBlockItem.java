package it.bohdloss.tfmg.content.electricity.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DebugCinderBlockItem extends Item {
    public DebugCinderBlockItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
//        if (level.getBlockEntity(pos) instanceof IElectric be) {
//            if(level.isClientSide()) {
//                DebugStuff.show("VOLTAGE {}", be.getData().voltage);
//            } else {
//                TFMG.LOGGER.debug("VOLTAGE {}", be.getData().voltage);
//            }
//        }
        return InteractionResult.SUCCESS;
    }
}
