package com.drmangotea.tfmg.content.engines;

import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class CylinderItem extends Item {


    public CylinderItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        CompoundTag fuels = stack.getOrCreateTag().getCompound("FuelNames");

        if(fuels.isEmpty())
            return;
        tooltip.add(Lang.translateDirect("tooltip.cylinder")
                .withStyle(ChatFormatting.GRAY));

        for(String key : fuels.getAllKeys()) {

            tooltip.add(Lang.text("- "+fuels.getString(key)).component()
                    .withStyle(ChatFormatting.AQUA));
        }

    }


}
