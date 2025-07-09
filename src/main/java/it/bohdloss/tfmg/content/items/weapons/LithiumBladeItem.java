package it.bohdloss.tfmg.content.items.weapons;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class LithiumBladeItem extends SwordItem {
    public static final int MAX_TIME = 2000;

    public LithiumBladeItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

//    @Override
//    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player player, @NotNull InteractionHand hand) {
////        ItemStack stack = player.getItemInHand(hand);
////        ItemStack stack1 = TFMGItems.LIT_LITHIUM_BLADE.asStack(1);
//
////        stack.
////
////        Map<Enchantment, Integer> enchantments = stack.getAllEnchantments(HolderLookup.RegistryLookup.);
////        enchantments.forEach(stack1::enchant);
////
////        int slot = -1;
////        for(int i =0 ; i < player.getInventory().getContainerSize(); i++){
////            if(player.getInventory().getItem(i).is(TFMGItems.LITHIUM_CHARGE.get())){
////                slot = i;
////                break;
////
////            }
////        }
////
////        if(slot==-1) {
////            return super.use(pLevel, player, hand);
////        }
////
////        stack1.getOrCreateTag().putInt("time",MAX_TIME);
////
////        pLevel.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL, 0.5F, 0.4F);
////        player.getInventory().getItem(slot).shrink(1);
////        player.setItemInHand(hand, stack1);
////
////        return super.use(pLevel, player, hand);
//    }
}
