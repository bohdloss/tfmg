package it.bohdloss.tfmg.content.electricity.utilities.fuse_block;

import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.registry.TFMGDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FuseItem extends Item {
    public FuseItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(CreateLang.translateDirect("tooltip.fuse", stack.getOrDefault(TFMGDataComponents.AMP_RATING, AmpRating.DEFAULT).rating()).append("A")
                .withStyle(ChatFormatting.GREEN)
        );
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
