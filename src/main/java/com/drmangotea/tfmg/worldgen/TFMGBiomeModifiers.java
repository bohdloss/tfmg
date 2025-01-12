package com.drmangotea.tfmg.worldgen;

import com.drmangotea.tfmg.TFMG;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

public class TFMGBiomeModifiers {
    public static final ResourceKey<BiomeModifier>
            LEAD_ORE = key("lead_ore"),
            NICKEL_ORE = key("nickel_ore"),
            LITHIUM_ORE = key("lithium_ore"),
            TFMG_STRIATED_ORES_OVERWORLD = key("tfmg_striated_ores_overworld"),
            TFMG_STRIATED_ORES_NETHER = key("tfmg_striated_ores_nether");

    private static ResourceKey<BiomeModifier> key(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, TFMG.asResource(name));
    }

    public static void bootstrap(BootstapContext<BiomeModifier> ctx) {
        HolderGetter<Biome> biomeLookup = ctx.lookup(Registries.BIOME);
        HolderSet<Biome> isOverworld = biomeLookup.getOrThrow(BiomeTags.IS_OVERWORLD);
        HolderSet<Biome> isNether = biomeLookup.getOrThrow(BiomeTags.IS_NETHER);

        HolderGetter<PlacedFeature> featureLookup = ctx.lookup(Registries.PLACED_FEATURE);
        Holder<PlacedFeature> leadOre = featureLookup.getOrThrow(TFMGPlacedFeatures.LEAD_ORE);
        Holder<PlacedFeature> nickelOre = featureLookup.getOrThrow(TFMGPlacedFeatures.NICKEL_ORE);
        Holder<PlacedFeature> lithiumOre = featureLookup.getOrThrow(TFMGPlacedFeatures.LITHIUM_ORE);
        Holder<PlacedFeature> striatedOresOverworld = featureLookup.getOrThrow(TFMGPlacedFeatures.TFMG_STRIATED_ORES_OVERWORLD);
        Holder<PlacedFeature> striatedOresNether = featureLookup.getOrThrow(TFMGPlacedFeatures.TFMG_STRIATED_ORES_NETHER);

        ctx.register(LEAD_ORE, addOre(isOverworld, leadOre));
        ctx.register(NICKEL_ORE, addOre(isOverworld, nickelOre));
        ctx.register(LITHIUM_ORE, addOre(isOverworld, lithiumOre));
        ctx.register(TFMG_STRIATED_ORES_OVERWORLD, addOre(isOverworld, striatedOresOverworld));
        ctx.register(TFMG_STRIATED_ORES_NETHER, addOre(isNether, striatedOresNether));
    }

    private static ForgeBiomeModifiers.AddFeaturesBiomeModifier addOre(HolderSet<Biome> biomes, Holder<PlacedFeature> feature) {
        return new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomes, HolderSet.direct(feature), GenerationStep.Decoration.UNDERGROUND_ORES);
    }
}
