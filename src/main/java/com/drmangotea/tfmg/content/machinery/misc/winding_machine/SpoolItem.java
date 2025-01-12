package com.drmangotea.tfmg.content.machinery.misc.winding_machine;

import com.drmangotea.tfmg.registry.TFMGItems;
import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class SpoolItem extends Item {

    final PartialModel model;
    final int barColor;

    public SpoolItem(Properties properties, PartialModel model, int barColor) {
        super(properties);
        this.model = model;
        this.barColor = barColor;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if(level.getBlockEntity(pos) instanceof WindingMachineBlockEntity be){

            ItemStack oldSpool = ItemStack.EMPTY;

            if(!be.spool.isEmpty()){
                oldSpool = be.spool;
            }
            be.spool = context.getItemInHand();

            context.getPlayer().setItemInHand(context.getHand(), oldSpool);



            be.sendData();
            be.setChanged();

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean p_41408_) {
        super.inventoryTick(stack, level, entity, slot, p_41408_);

        if(stack.getOrCreateTag().getInt("Amount")==0&& entity instanceof Player player&&!stack.is(TFMGItems.EMPTY_SPOOL.get())){
            player.getInventory().setItem(slot, TFMGItems.EMPTY_SPOOL.asStack());
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return model != null;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return barColor;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return (int) (13f*((float)stack.getOrCreateTag().getInt("Amount")/1000));
    }
}
