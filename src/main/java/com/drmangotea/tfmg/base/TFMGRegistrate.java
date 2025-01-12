package com.drmangotea.tfmg.base;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.ForgeRegistries;

import static com.simibubi.create.foundation.data.BlockStateGen.simpleCubeAll;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class TFMGRegistrate extends CreateRegistrate {
    public static String autoLang(String id) {
        StringBuilder builder = new StringBuilder();
        boolean b = true;
        for (char c: id.toCharArray()) {
            if(c == '_') {
                builder.append(' ');
                b = true;
            } else {
                builder.append(b ? String.valueOf(c).toUpperCase() : c);
                b = false;
            }
        }
        return builder.toString();
    }
    protected TFMGRegistrate() {
        super(TFMG.MOD_ID);
    }

    public static TFMGRegistrate create() {
        return new TFMGRegistrate();
    }

    public static Block getBlock(String name) {
        return TFMG.REGISTRATE.get(name, ForgeRegistries.BLOCKS.getRegistryKey()).get();
    }
    public static Item getItem(String name) {
        return TFMG.REGISTRATE.get(name, ForgeRegistries.ITEMS.getRegistryKey()).get();
    }
    public static Item getBucket(String name) {
        return TFMG.REGISTRATE.get(name+"_bucket", ForgeRegistries.ITEMS.getRegistryKey()).get();
    }

}
