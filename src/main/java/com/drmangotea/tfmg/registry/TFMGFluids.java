package com.drmangotea.tfmg.registry;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.base.TFMGUtils;
import com.drmangotea.tfmg.base.fluid.AsphaltFluid;
import com.drmangotea.tfmg.base.fluid.ConcreteFluid;
import com.drmangotea.tfmg.base.fluid.HotFluidType;
import com.simibubi.create.AllFluids;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.joml.Vector3f;

import java.util.function.Supplier;

import static com.drmangotea.tfmg.TFMG.REGISTRATE;
import static com.drmangotea.tfmg.base.TFMGUtils.toHumanReadable;

public class TFMGFluids {


    public static final FluidEntry<VirtualFluid>
            LPG = gasFuel("lpg", TFMGTags.TFMGFluidTags.LPG.tag, TFMGTags.TFMGFluidTags.FIREBOX_FUEL.tag),
            BUTANE = gasFuel("butane", TFMGTags.TFMGFluidTags.FIREBOX_FUEL.tag),
            PROPANE = gasFuel("propane", TFMGTags.TFMGFluidTags.FIREBOX_FUEL.tag),
            HYDROGEN = gasFuel("hydrogen"),

    FURNACE_GAS = gasFuel("furnace_gas", TFMGTags.TFMGFluidTags.BLAST_STOVE_FUEL.tag, TFMGTags.TFMGFluidTags.FURNACE_GAS.tag),
    //  METHANE = gasFuel("methane"),
    ETHYLENE = gas("ethylene"),
            PROPYLENE = gas("propylene"),
            NEON = gas("neon"),
            CARBON_DIOXIDE = gas("carbon_dioxide"),
            AIR = gas("air",TFMGTags.TFMGFluidTags.AIR.tag),
            HOT_AIR = gas("hot_air");


    public static final FluidEntry<ForgeFlowingFluid.Flowing>
            CRUDE_OIL = fluid("crude_oil", 0x010101, TFMGTags.TFMGFluidTags.CRUDE_OIL.tag, TFMGTags.TFMGFluidTags.FLAMMABLE.tag),
            HEAVY_OIL = fluid("heavy_oil", 0x010101, TFMGTags.TFMGFluidTags.HEAVY_OIL.tag, TFMGTags.TFMGFluidTags.FLAMMABLE.tag),

    GASOLINE = fuel("gasoline", 0x010101, TFMGTags.TFMGFluidTags.GASOLINE.tag),
            DIESEL = fuel("diesel", 0x010101, TFMGTags.TFMGFluidTags.DIESEL.tag, TFMGTags.TFMGFluidTags.FIREBOX_FUEL.tag),
            NAPHTHA = fuel("naphtha", 0x010101, TFMGTags.TFMGFluidTags.NAPHTHA.tag, TFMGTags.TFMGFluidTags.FIREBOX_FUEL.tag),
            KEROSENE = fuel("kerosene", 0x010101, TFMGTags.TFMGFluidTags.KEROSENE.tag, TFMGTags.TFMGFluidTags.FIREBOX_FUEL.tag),
            CREOSOTE = fuel("creosote", 0x010101, TFMGTags.TFMGFluidTags.CREOSOTE.tag, TFMGTags.TFMGFluidTags.BLAST_STOVE_FUEL.tag),
            MOLTEN_STEEL = hotFluid("molten_steel", 0x010101, TFMGTags.TFMGFluidTags.MOLTEN_STEEL.tag),
            MOLTEN_SLAG = hotFluid("molten_slag", 0x010101),
            MOLTEN_PLASTIC = hotFluid("molten_plastic", 0x010101),
            LIQUID_SILICON = hotFluid("liquid_silicon", 0x010101),
            LUBRICATION_OIL = fluid("lubrication_oil", 0x010101, TFMGTags.TFMGFluidTags.LUBRICATION_OIL.tag, TFMGTags.TFMGFluidTags.FLAMMABLE.tag),
            COOLING_FLUID = fluid("cooling_fluid", 0x010101, TFMGTags.TFMGFluidTags.COOLING_FLUID.tag),
            NAPALM = fluid("napalm", 0x010101),
            SULFURIC_ACID = fluid("sulfuric_acid", 0x010101),

    LIQUID_CONCRETE = concreteFluid("liquid_concrete", 0x010101, ConcreteFluid.Source::new),

    LIQUID_ASPHALT = concreteFluid("liquid_asphalt", 0x010101, AsphaltFluid.Source::new);


    @SafeVarargs
    private static FluidEntry<ForgeFlowingFluid.Flowing> fluid(String name, int fogColor, TagKey<Fluid>... tags) {
        return REGISTRATE.fluid(name, getLocation(name), getLocationFlow(name),
                        SolidRenderedPlaceableFluidType.create(fogColor, () -> 1f))
                .lang(toHumanReadable(name))
                .properties(b -> b.viscosity(1000)
                        .density(1000))
                .fluidProperties(p -> p.levelDecreasePerBlock(1)
                        .tickRate(10)
                        .slopeFindDistance(5)
                        .explosionResistance(100f))
                .tag(tags)
                .source(ForgeFlowingFluid.Source::new)
                .bucket()
                .tag(AllTags.forgeItemTag("buckets/" + name))
                .build()
                .register();
    }

    @SafeVarargs
    private static FluidEntry<ForgeFlowingFluid.Flowing> fuel(String name, int fogColor, TagKey<Fluid>... tags) {
        return REGISTRATE.fluid(name, getLocation(name), getLocationFlow(name),
                        SolidRenderedPlaceableFluidType.create(fogColor, () -> 1f))
                .lang(toHumanReadable(name))
                .properties(b -> b.viscosity(1000)
                        .density(1000))
                .fluidProperties(p -> p.levelDecreasePerBlock(1)
                        .tickRate(10)
                        .slopeFindDistance(5)
                        .explosionResistance(100f))
                .tag(tags)
                .tag(TFMGTags.TFMGFluidTags.FUEL.tag, TFMGTags.TFMGFluidTags.FLAMMABLE.tag)
                .source(ForgeFlowingFluid.Source::new)
                .bucket()
                .tag(AllTags.forgeItemTag("buckets/" + name))
                .build()
                .register();
    }

    @SafeVarargs
    private static FluidEntry<ForgeFlowingFluid.Flowing> hotFluid(String name, int fogColor, TagKey<Fluid>... tags) {
        return REGISTRATE.fluid(name, getLocation(name), getLocationFlow(name),
                        HotFluidType.create(fogColor, () -> 1f))
                .lang(toHumanReadable(name))
                .properties(b -> b.viscosity(1000)
                        .density(1000))
                .fluidProperties(p -> p.levelDecreasePerBlock(1)
                        .tickRate(3)
                        .slopeFindDistance(3)
                        .explosionResistance(100f))
                .tag(tags)
                .source(ForgeFlowingFluid.Source::new)
                .bucket()
                .tag(AllTags.forgeItemTag("buckets/" + name))
                .build()
                .register();
    }

    @SafeVarargs
    private static FluidEntry<ForgeFlowingFluid.Flowing> concreteFluid(String name, int fogColor, NonNullFunction<ForgeFlowingFluid.Properties, ? extends ForgeFlowingFluid> factory, TagKey<Fluid>... tags) {
        return REGISTRATE.fluid(name, getLocation(name), getLocationFlow(name),
                        SolidRenderedPlaceableFluidType.create(fogColor, () -> 1f))
                .lang(toHumanReadable(name))
                .properties(b -> b.viscosity(5000)
                        .density(2500))
                .fluidProperties(p -> p.levelDecreasePerBlock(1)
                        .tickRate(10)
                        .slopeFindDistance(2)
                        .explosionResistance(1000f))
                .tag(tags)
                .source(factory)
                .bucket()
                .tag(AllTags.forgeItemTag("buckets/" + name))
                .build()
                .register();
    }


    @SafeVarargs
    public static FluidEntry<VirtualFluid> gas(String name, TagKey<Fluid>... tags) {
        return REGISTRATE.gasFluid(name, getLocation(name), getLocationFlow(name))
                .lang(TFMGUtils.fromId(name))
                .tag(tags)
                .tag(TFMGTags.TFMGFluidTags.GAS.tag)
                .bucket()
                .lang(TFMGUtils.fromId(name) + " Tank")
                .tag(AllTags.forgeItemTag("buckets/" + name))
                .build()
                .register();
    }

    @SafeVarargs
    public static FluidEntry<VirtualFluid> gasFuel(String name, TagKey<Fluid>... tags) {
        return REGISTRATE.gasFluid(name, getGasLocation(name), getGasLocation(name))
                .lang(TFMGUtils.fromId(name))
                .tag(tags)
                .tag(TFMGTags.TFMGFluidTags.GAS.tag)
                .tag(TFMGTags.TFMGFluidTags.FUEL.tag)
                .tag(TFMGTags.TFMGFluidTags.FLAMMABLE.tag)
                .bucket()
                .lang(TFMGUtils.fromId(name) + " Tank")
                .tag(AllTags.forgeItemTag("buckets/" + name))
                .build()
                .register();
    }

    public static ResourceLocation getGasLocation(String name) {
        return TFMG.asResource("fluid/" + name);
    }

    public static ResourceLocation getLocation(String name) {
        return TFMG.asResource("fluid/" + name + "_still");
    }

    public static ResourceLocation getLocationFlow(String name) {
        return TFMG.asResource("fluid/" + name + "_flow");
    }

    public static class SolidRenderedPlaceableFluidType extends AllFluids.TintedFluidType {

        private Vector3f fogColor;
        private Supplier<Float> fogDistance;


        public static FluidBuilder.FluidTypeFactory create(int fogColor, Supplier<Float> fogDistance) {
            return (p, s, f) -> {
                SolidRenderedPlaceableFluidType fluidType = new SolidRenderedPlaceableFluidType(p, s, f);
                fluidType.fogColor = new Color(fogColor, false).asVectorF();
                fluidType.fogDistance = fogDistance;
                return fluidType;
            };
        }

        public SolidRenderedPlaceableFluidType(Properties properties, ResourceLocation stillTexture,
                                               ResourceLocation flowingTexture) {
            super(properties, stillTexture, flowingTexture);
        }

        @Override
        protected int getTintColor(FluidStack stack) {
            return NO_TINT;
        }

        @Override
        public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
            return 0x00ffffff;
        }

        @Override
        protected Vector3f getCustomFogColor() {
            return fogColor;
        }

        @Override
        protected float getFogDistanceModifier() {
            return fogDistance.get();
        }

    }


    public static void init() {
    }
}
