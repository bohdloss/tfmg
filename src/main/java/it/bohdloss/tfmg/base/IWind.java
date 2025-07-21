package it.bohdloss.tfmg.base;

import net.minecraft.world.item.ItemStack;

public interface IWind {
    int getWindings(ItemStack itemStack);
    void setWindings(ItemStack itemStack, int windings);
    int getMaxWindings(ItemStack itemStack);
    int getRenderedColor(ItemStack itemStack);
}
