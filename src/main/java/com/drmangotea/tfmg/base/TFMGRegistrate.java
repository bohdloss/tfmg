package com.drmangotea.tfmg.base;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.base.fluid.GasFluidType;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.VirtualFluidBuilder;
import com.tterrag.registrate.builders.FluidBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraftforge.registries.ForgeRegistries;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

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

    public FluidBuilder<VirtualFluid, CreateRegistrate> gasFluid(String name, ResourceLocation still,
                                                                 ResourceLocation flow) {
        return entry(name, c -> new VirtualFluidBuilder<>(self(),self(), name, c, still, flow,
                GasFluidType.create(),VirtualFluid::createSource,VirtualFluid::createFlowing));
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
