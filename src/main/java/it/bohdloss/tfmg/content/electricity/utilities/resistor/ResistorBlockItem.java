package it.bohdloss.tfmg.content.electricity.utilities.resistor;

import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.base.IWindable;
import it.bohdloss.tfmg.content.electricity.utilities.transformer.CoilTurns;
import it.bohdloss.tfmg.registry.TFMGDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ResistorBlockItem extends BlockItem implements IWindable {
    public ResistorBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(CreateLang.translateDirect("tooltip.resistor", stack.getOrDefault(TFMGDataComponents.RESISTANCE, Resistance.DEFAULT).value()).append("Ω")
                .withStyle(ChatFormatting.GREEN)
        );
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public int getWindings(ItemStack itemStack) {
        return itemStack.getOrDefault(TFMGDataComponents.RESISTANCE, Resistance.DEFAULT).value();
    }

    @Override
    public void setWindings(ItemStack itemStack, int windings) {
        itemStack.set(TFMGDataComponents.RESISTANCE, new Resistance(windings));
    }

    @Override
    public int getMaxWindings(ItemStack itemStack) {
        return Resistance.MAX.amount();
    }

    @Override
    public int getRenderedColor(ItemStack itemStack) {
        return getWindings(itemStack) == 0 ? 0x61472F : 0xCFC2A8;
    }
}
