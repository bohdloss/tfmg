package it.bohdloss.tfmg.content.electricity.utilities.transformer;

import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.content.electricity.connection.Windings;
import it.bohdloss.tfmg.registry.TFMGDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElectromagneticCoilItem extends Item {
    public ElectromagneticCoilItem(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(CreateLang.translateDirect("tooltip.coils", stack.getOrDefault(TFMGDataComponents.WINDINGS, Windings.DEFAULT).amount())
                .withStyle(ChatFormatting.GREEN)
        );
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
