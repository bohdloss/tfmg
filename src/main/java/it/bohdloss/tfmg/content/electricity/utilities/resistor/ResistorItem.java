package it.bohdloss.tfmg.content.electricity.utilities.resistor;

import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.registry.TFMGDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ResistorItem extends Item {
    public ResistorItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(CreateLang.translateDirect("tooltip.resistor", stack.getOrDefault(TFMGDataComponents.RESISTANCE, Resistance.DEFAULT).value()).append("Ω")
                .withStyle(ChatFormatting.GREEN)
        );
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
