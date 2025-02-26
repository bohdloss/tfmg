package com.drmangotea.tfmg.base;

import com.drmangotea.tfmg.content.machinery.misc.winding_machine.SpoolItem;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

import static com.drmangotea.tfmg.TFMG.MOD_ID;
import static com.drmangotea.tfmg.TFMG.REGISTRATE;

public class TFMGCreativeTabs {


    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final RegistryObject<CreativeModeTab> TFMG_MAIN = CREATIVE_MODE_TABS.register("tfmg_main", () -> CreativeModeTab.builder()
            .withTabsBefore(AllCreativeModeTabs.BASE_CREATIVE_TAB.getId())
            .title(Component.translatable("creative_tab.tfmg_main"))
            .icon(()-> TFMGItems.STEEL_INGOT.get().asItem().getDefaultInstance())
            .build());

    public static final RegistryObject<CreativeModeTab> TFMG_DECORATION = CREATIVE_MODE_TABS.register("tfmg_decoration", () -> CreativeModeTab.builder()
            .withTabsBefore(TFMG_MAIN.getId())
            .title(Component.translatable("creative_tab.tfmg_decoration"))
            .icon(()-> TFMGBlocks.CONCRETE.block.get().asItem().getDefaultInstance())
            .build());
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == TFMG_MAIN.get()){
            event.acceptAll(customAdditions());
            for(RegistryEntry<Item> item : REGISTRATE.getAll(Registries.ITEM)){

                if(!CreateRegistrate.isInCreativeTab(item,TFMG_MAIN))
                    continue;
                if(blacklist().contains(item))
                    continue;
                if(item.get() instanceof SequencedAssemblyItem)
                    continue;
                if(item.get() instanceof SpoolItem&&!item.is(TFMGItems.EMPTY_SPOOL.get())){
                    ItemStack spool = item.get().getDefaultInstance();
                    spool.getOrCreateTag().putInt("Amount", 1000);
                    event.accept(spool);
                    continue;
                }
                event.accept(item);
            }
        }

        if (event.getTab() == TFMG_DECORATION.get()){
            for(RegistryEntry<Item> item : REGISTRATE.getAll(Registries.ITEM)){
                if(!CreateRegistrate.isInCreativeTab(item, TFMG_DECORATION))
                    continue;
                if(blacklist().contains(item))
                    continue;
                if(item.get() instanceof SequencedAssemblyItem)
                    continue;

                event.accept(item);

            }

        }
    }
    public static void register(IEventBus modEventBus){
        CREATIVE_MODE_TABS.register(modEventBus);
    }

    public static List<RegistryEntry<Item>> blacklist(){
        List<RegistryEntry<Item>> list = new ArrayList<>();
        return list;
    }
    public static List<ItemStack> customAdditions(){
        List<ItemStack> list = new ArrayList<>();

        CompoundTag gasolineTag = new CompoundTag();
        gasolineTag.putString("gasoline", "forge:gasoline");
        CompoundTag gasolineTagName = new CompoundTag();
        gasolineTagName.putString("gasoline", "Gasoline");
        //
        CompoundTag dieselTag = new CompoundTag();
        dieselTag.putString("diesel", "forge:diesel");
        CompoundTag dieselTagName = new CompoundTag();
        dieselTagName.putString("diesel", "Diesel");
        //
        CompoundTag lpgTag = new CompoundTag();
        lpgTag.putString("lpg", "forge:lpg");
        CompoundTag lpgTagName = new CompoundTag();
        lpgTagName.putString("lpg", "LPG");
        //


        ItemStack gasoline = TFMGItems.ENGINE_CYLINDER.asStack();
        gasoline.getOrCreateTag().put("Fuels", gasolineTag);
        gasoline.getOrCreateTag().put("FuelNames", gasolineTagName);
        list.add(gasoline);
        ItemStack diesel = TFMGItems.ENGINE_CYLINDER.asStack();
        diesel.getOrCreateTag().put("Fuels", dieselTag);
        diesel.getOrCreateTag().put("FuelNames", dieselTagName);
        list.add(diesel);
        ItemStack lpg = TFMGItems.ENGINE_CYLINDER.asStack();
        lpg.getOrCreateTag().put("Fuels", lpgTag);
        lpg.getOrCreateTag().put("FuelNames", lpgTagName);
        list.add(lpg);

        return list;
    }


}
