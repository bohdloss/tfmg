package it.bohdloss.tfmg.content.electricity.utilities.transformer;

import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.base.IWindable;
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

public class ElectromagneticCoilItem extends Item implements IWindable {
    public ElectromagneticCoilItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack def = super.getDefaultInstance();
        setWindings(def, CoilTurns.MAX.amount());
        return def;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(CreateLang.translateDirect("tooltip.coils", getWindings(stack))
                .withStyle(ChatFormatting.GREEN)
        );
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public int getWindings(ItemStack itemStack) {
        return itemStack.getOrDefault(TFMGDataComponents.COIL_TURNS, CoilTurns.DEFAULT).amount();
    }

    @Override
    public void setWindings(ItemStack itemStack, int windings) {
        itemStack.set(TFMGDataComponents.COIL_TURNS, new CoilTurns(windings));
    }

    @Override
    public int getMaxWindings(ItemStack itemStack) {
        return CoilTurns.MAX.amount();
    }

    @Override
    public int getRenderedColor(ItemStack itemStack) {
        return getWindings(itemStack) == 0 ? 0x61472F : 0xD8735A;
    }
}
