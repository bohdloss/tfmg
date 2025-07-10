package it.bohdloss.tfmg;

import it.bohdloss.tfmg.config.TFMGConfigs;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import it.bohdloss.tfmg.registry.*;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

import static net.createmod.catnip.lang.FontHelper.styleFromColor;

@Mod(TFMG.MOD_ID)
@EventBusSubscriber
public class TFMG {
    public static final String MOD_ID = "tfmg";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final TFMGRegistrate REGISTRATE = TFMGRegistrate.create();
    static {
        REGISTRATE.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    public static final FontHelper.Palette TFMG_PALETTE = new FontHelper.Palette(styleFromColor(0x4c5155), styleFromColor(0x838c8a));

    static {
        REGISTRATE.setTooltipModifierFactory((item) -> (new ItemDescription.Modifier(item, TFMG_PALETTE)).andThen(TooltipModifier.mapNull(KineticStats.create(item))));
        //.andThen(TooltipModifier.mapNull(CableTypeStats.create(item))) (save this for whenever the fuck I figure out what resistivity is meant to do)
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void register(RegisterEvent event) {
        if(event.getRegistryKey() == Registries.BLOCK.registryKey()) {
            System.out.println(event.getRegistry().key().location());
        }

    }

    public TFMG(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);

        TFMGCreativeTabs.register(modEventBus);
        TFMGItems.init();
        TFMGTags.init();
        TFMGBlocks.init();
        TFMGEntityTypes.init();
        TFMGMobEffects.register(modEventBus);
        TFMGBlockEntities.init();
        TFMGEncasedBlocks.init();
        TFMGPartialModels.init();
        TFMGDataComponents.register(modEventBus);
        TFMGRegistries.init();
        TFMGArmorMaterials.register(modEventBus);
        TFMGFluids.init();
        TFMGColoredFires.register(modEventBus);
        TFMGPipes.init();
        TFMGParticleTypes.register(modEventBus);

        TFMGConfigs.register(modContainer);
    }
}
