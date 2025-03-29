package com.drmangotea.tfmg.worldgen;




import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.worldgen.deposits.OilDepositFeature;
import com.drmangotea.tfmg.worldgen.deposits.OilWellFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class TFMGFeatures {


        public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, TFMG.MOD_ID);

        public static final RegistryObject<OilDepositFeature> OIL_DEPOSIT =
                FEATURES.register("oil_deposit", () -> new OilDepositFeature(NoneFeatureConfiguration.CODEC));

        public static final RegistryObject<OilWellFeature> OIL_WELL =
                FEATURES.register("oil_well", () -> new OilWellFeature(NoneFeatureConfiguration.CODEC));

        public static void register(IEventBus modEventBus) {
                FEATURES.register(modEventBus);
        }
}
