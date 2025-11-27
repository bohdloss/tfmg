package it.bohdloss.tfmg.content.electricity.storage;

import it.bohdloss.tfmg.config.TFMGConfigs;
import it.bohdloss.tfmg.content.items.AccumulatorStorage;
import it.bohdloss.tfmg.registry.TFMGDataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class AccumulatorItem extends BlockItem {
    public AccumulatorItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        return 0x51DBD4;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float value = stack.getOrDefault(TFMGDataComponents.ACCUMULATOR_STORAGE, AccumulatorStorage.DEFAULT).value();
        float max = TFMGConfigs.common().machines.accumulatorStorage.get();

        return (int) (13f * (value / max));
    }
}
