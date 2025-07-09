package it.bohdloss.tfmg.content.machinery.oil_processing;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class OilHammerItem extends Item {
    public OilHammerItem(Properties properties) {
        super(properties);
    }

//    @Override
//    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
//        Level level = context.getLevel();
//        BlockPos pos = context.getClickedPos();
//        Player player = context.getPlayer();
//
//        for(int i = 0;i<300;i++){
//            BlockPos posToCheck = pos.below(i);
//            if(level.getBlockState(posToCheck).is(TFMGBlocks.OIL_DEPOSIT.get())){
//                if(TFMG.DEPOSITS.getReservoirFor(posToCheck.asLong())==null)
//                    return InteractionResult.SUCCESS;
//                int oilReserves = TFMG.DEPOSITS.getReservoirFor(posToCheck.asLong()).oilReserves;
//
//                if (level.isClientSide&&player!=null)
//                    player.displayClientMessage(CreateLang.translateDirect("oil_hammer.reserves", oilReserves)
//                            .withStyle(ChatFormatting.YELLOW), true);
//
//                return InteractionResult.SUCCESS;
//            }
//        }
//
//
//
//        return InteractionResult.SUCCESS;
//    }
}
