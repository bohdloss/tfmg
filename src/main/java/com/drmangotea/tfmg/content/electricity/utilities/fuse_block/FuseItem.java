package com.drmangotea.tfmg.content.electricity.utilities.fuse_block;

import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class FuseItem extends Item{


    public FuseItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {

        tooltip.add(Lang.translateDirect("tooltip.fuse", stack.getOrCreateTag().getInt("AmpRating")).append("A")
                .withStyle(ChatFormatting.GREEN)
        );


        super.appendHoverText(stack, world, tooltip, flag);
    }
}
