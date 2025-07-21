package it.bohdloss.tfmg.registry;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllFluids;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.base.fluid.AcidFluidType;
import it.bohdloss.tfmg.base.fluid.AsphaltFluid;
import it.bohdloss.tfmg.base.fluid.ConcreteFluid;
import it.bohdloss.tfmg.base.fluid.HotFluidType;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static it.bohdloss.tfmg.TFMG.REGISTRATE;
import static it.bohdloss.tfmg.TFMGUtils.toHumanReadable;

public class TFMGFluids {
    public static final FluidEntry<VirtualFluid>
            LPG = gasFuel("lpg", 0xfff5e687, TFMGTags.TFMGFluidTags.LPG.tag, TFMGTags.TFMGFluidTags.FIREBOX_FUEL.tag),
            BUTANE = gasFuel("butane", 0xffad77d4, TFMGTags.TFMGFluidTags.FIREBOX_FUEL.tag),
            PROPANE = gasFuel("propane", 0xff88bf80, TFMGTags.TFMGFluidTags.FIREBOX_FUEL.tag),
            HYDROGEN = gasFuel("hydrogen", 0xffd0f2f5),
            FURNACE_GAS = gasFuel("furnace_gas", 0xff5c5555, TFMGTags.TFMGFluidTags.BLAST_STOVE_FUEL.tag, TFMGTags.TFMGFluidTags.FURNACE_GAS.tag),
            ETHYLENE = gas("ethylene", 0xffbcadcc),
            PROPYLENE = gas("propylene", 0xffc0d1b4),
            NEON = gas("neon", 0xff9dede9),
            CARBON_DIOXIDE = gas("carbon_dioxide", 0xff525252),
            AIR = gas("air", 0xffdfe6e5, TFMGTags.TFMGFluidTags.AIR.tag),
            HOT_AIR = gas("hot_air", 0xffe8e1d5);


    public static final FluidEntry<BaseFlowingFluid.Flowing>
            CRUDE_OIL = fluid("crude_oil", 0x010101, TFMGTags.TFMGFluidTags.CRUDE_OIL.tag, TFMGTags.TFMGFluidTags.FLAMMABLE.tag),
            HEAVY_OIL = fluid("heavy_oil", 0x010101, TFMGTags.TFMGFluidTags.HEAVY_OIL.tag, TFMGTags.TFMGFluidTags.FLAMMABLE.tag),
            GASOLINE = fuel("gasoline", 0xCCB17D, TFMGTags.TFMGFluidTags.GASOLINE.tag),
            DIESEL = fuel("diesel", 0xBE9C84, TFMGTags.TFMGFluidTags.DIESEL.tag, TFMGTags.TFMGFluidTags.FIREBOX_FUEL.tag),
            NAPHTHA = fuel("naphtha", 0x683525, TFMGTags.TFMGFluidTags.NAPHTHA.tag, TFMGTags.TFMGFluidTags.FIREBOX_FUEL.tag),
            KEROSENE = fuel("kerosene", 0x7C82D5, TFMGTags.TFMGFluidTags.KEROSENE.tag, TFMGTags.TFMGFluidTags.FIREBOX_FUEL.tag),
            CREOSOTE = fuel("creosote", 0x010101, TFMGTags.TFMGFluidTags.CREOSOTE.tag, TFMGTags.TFMGFluidTags.BLAST_STOVE_FUEL.tag),
            MOLTEN_STEEL = hotFluid("molten_steel", 0xFFF760, TFMGTags.TFMGFluidTags.MOLTEN_STEEL.tag),
            MOLTEN_SLAG = hotFluid("molten_slag", 0xFFF760),
            MOLTEN_PLASTIC = hotFluid("molten_plastic", 0xDEE4FF),
            LIQUID_SILICON = hotFluid("liquid_silicon", 0xFFF760),
            LUBRICATION_OIL = fluid("lubrication_oil", 0x9D945F, TFMGTags.TFMGFluidTags.LUBRICATION_OIL.tag, TFMGTags.TFMGFluidTags.FLAMMABLE.tag),
            COOLING_FLUID = fluid("cooling_fluid", 0x7BC1C1, TFMGTags.TFMGFluidTags.COOLING_FLUID.tag),
            NAPALM = fluid("napalm", 0xC0CA97),
            SULFURIC_ACID = acidFluid("sulfuric_acid", 0xE9E7CC),
            LIQUID_CONCRETE = concreteFluid("liquid_concrete", 0x5B5B59, ConcreteFluid.Source::new),
            LIQUID_ASPHALT = concreteFluid("liquid_asphalt", 0x010101, AsphaltFluid.Source::new);


    @SafeVarargs
    private static FluidEntry<BaseFlowingFluid.Flowing> fluid(String name, int fogColor, TagKey<Fluid>... tags) {
        return REGISTRATE.fluid(name, getLocation(name), getLocationFlow(name),
                        SolidRenderedPlaceableFluidType.create(fogColor, () -> 1f / 32f))
                .lang(toHumanReadable(name))
                .properties(b -> b.viscosity(1000)
                        .density(1000))
                .fluidProperties(p -> p.levelDecreasePerBlock(1)
                        .tickRate(10)
                        .slopeFindDistance(5)
                        .explosionResistance(100f))
                .tag(tags)
                .source(BaseFlowingFluid.Source::new)
                .bucket()
                .tag(AllTags.commonItemTag("buckets/" + name))
                .build()
                .register();
    }
    @SafeVarargs
    private static FluidEntry<BaseFlowingFluid.Flowing> acidFluid(String name, int fogColor, TagKey<Fluid>... tags) {
        return REGISTRATE.fluid(name, getLocation(name), getLocationFlow(name),
                        AcidFluidType.create(fogColor, () -> 1f / 32f))
                .lang(toHumanReadable(name))
                .properties(b -> b.viscosity(1000)
                        .density(1000))
                .fluidProperties(p -> p.levelDecreasePerBlock(1)
                        .tickRate(10)
                        .slopeFindDistance(5)
                        .explosionResistance(100f))
                .tag(tags)
                .source(BaseFlowingFluid.Source::new)
                .bucket()
                .tag(AllTags.commonItemTag("buckets/" + name))
                .build()
                .register();
    }

    @SafeVarargs
    private static FluidEntry<BaseFlowingFluid.Flowing> fuel(String name, int fogColor, TagKey<Fluid>... tags) {
        return REGISTRATE.fluid(name, getLocation(name), getLocationFlow(name),
                        SolidRenderedPlaceableFluidType.create(fogColor, () -> 1f / 32f))
                .lang(toHumanReadable(name))
                .properties(b -> b.viscosity(1000)
                        .density(1000))
                .fluidProperties(p -> p.levelDecreasePerBlock(1)
                        .tickRate(10)
                        .slopeFindDistance(5)
                        .explosionResistance(100f))
                .tag(tags)
                .tag(TFMGTags.TFMGFluidTags.FUEL.tag, TFMGTags.TFMGFluidTags.FLAMMABLE.tag)
                .source(BaseFlowingFluid.Source::new)
                .bucket()
                .tag(AllTags.commonItemTag("buckets/" + name))
                .build()
                .register();
    }

    @SafeVarargs
    private static FluidEntry<BaseFlowingFluid.Flowing> hotFluid(String name, int fogColor, TagKey<Fluid>... tags) {
        return REGISTRATE.fluid(name, getLocation(name), getLocationFlow(name),
                        HotFluidType.create(fogColor, () -> 1f / 32f))
                .lang(toHumanReadable(name))
                .properties(b -> b.viscosity(1000)
                        .density(1000))
                .fluidProperties(p -> p.levelDecreasePerBlock(1)
                        .tickRate(3)
                        .slopeFindDistance(3)
                        .explosionResistance(100f))
                .tag(tags)
                .source(BaseFlowingFluid.Source::new)
                .bucket()
                .tag(AllTags.commonItemTag("buckets/" + name))
                .build()
                .register();
    }

    @SafeVarargs
    private static FluidEntry<BaseFlowingFluid.Flowing> concreteFluid(String name, int fogColor, NonNullFunction<BaseFlowingFluid.Properties, ? extends BaseFlowingFluid> factory, TagKey<Fluid>... tags) {
        return REGISTRATE.fluid(name, getLocation(name), getLocationFlow(name),
                        SolidRenderedPlaceableFluidType.create(fogColor, () -> 1f / 32f))
                .lang(toHumanReadable(name))
                .properties(b -> b.viscosity(5000)
                        .density(2500))
                .fluidProperties(p -> p.levelDecreasePerBlock(1)
                        .tickRate(99999)
                        .slopeFindDistance(1)
                        .explosionResistance(1000f))
                .tag(tags)
                .source(factory)
                .bucket()
                .tag(AllTags.commonItemTag("buckets/" + name))
                .build()
                .register();
    }


    @SafeVarargs
    public static FluidEntry<VirtualFluid> gas(String name, int color, TagKey<Fluid>... tags) {
        return REGISTRATE.gasFluid(name, color)
                .lang(TFMGUtils.fromId(name))
                .tag(tags)
                .tag(TFMGTags.TFMGFluidTags.GAS.tag)
                .bucket()
                .lang(TFMGUtils.fromId(name) + " Tank")
                .tag(AllTags.commonItemTag("buckets/" + name))
                .build()
                .register();
    }

    @SafeVarargs
    public static FluidEntry<VirtualFluid> gasFuel(String name, int color, TagKey<Fluid>... tags) {
        return REGISTRATE.gasFluid(name, color)
                .lang(TFMGUtils.fromId(name))
                .tag(tags)
                .tag(TFMGTags.TFMGFluidTags.GAS.tag)
                .tag(TFMGTags.TFMGFluidTags.FUEL.tag)
                .tag(TFMGTags.TFMGFluidTags.FLAMMABLE.tag)
                .bucket()
                .lang(TFMGUtils.fromId(name) + " Tank")
                .tag(AllTags.commonItemTag("buckets/" + name))
                .build()
                .register();
    }

    public static ResourceLocation getGasLocation(String name) {
        return TFMG.asResource("fluid/" + name);
    }

    public static ResourceLocation getGasTexture() {
        return TFMG.asResource("fluid/gas_texture");
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

    public static class GasFluidType extends TFMGFluids.SolidRenderedPlaceableFluidType {
        final int color;

        public GasFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, int color) {
            super(properties, stillTexture, flowingTexture);
            this.color = color;
        }

        public static FluidBuilder.FluidTypeFactory  create(int color) {
            return (p, s, f) -> {
                GasFluidType fluidType = new GasFluidType(p,s,f,color);
                return fluidType;
            };
        }
        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {

                @Override
                public @NotNull ResourceLocation getStillTexture() {
                    return getGasTexture();
                }

                @Override
                public @NotNull ResourceLocation getFlowingTexture() {
                    return getGasTexture();
                }

                @Override
                public int getTintColor(@NotNull FluidStack stack) {
                    return color;
                }

                @Override
                public int getTintColor(@NotNull FluidState state, @NotNull BlockAndTintGetter getter, @NotNull BlockPos pos) {
                    return 0xff99f22f;
                }

                @Override
                public @NotNull Vector3f modifyFogColor(@NotNull Camera camera, float partialTick, @NotNull ClientLevel level,
                                                        int renderDistance, float darkenWorldAmount, @NotNull Vector3f fluidFogColor) {
                    Vector3f customFogColor = GasFluidType.this.getCustomFogColor();
                    return customFogColor == null ? fluidFogColor : customFogColor;
                }

                @Override
                public void modifyFogRender(@NotNull Camera camera, FogRenderer.@NotNull FogMode mode, float renderDistance, float partialTick,
                                            float nearDistance, float farDistance, @NotNull FogShape shape) {
                    float modifier = GasFluidType.this.getFogDistanceModifier();
                    float baseWaterFog = 96.0f;
                    if (modifier != 1f) {
                        RenderSystem.setShaderFogShape(FogShape.CYLINDER);
                        RenderSystem.setShaderFogStart(-8);
                        RenderSystem.setShaderFogEnd(baseWaterFog * modifier);
                    }
                }

            });
        }


        @Override
        public int getDensity() {
            return -1;
        }
    }

    public static void registerFluidInteractions() {
        FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                TFMGFluids.CRUDE_OIL.get().getFluidType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return TFMGBlocks.FOSSILSTONE.get().defaultBlockState();
                    } else {
                        return Blocks.SMOOTH_BASALT.defaultBlockState();
                    }
                }
        ));
        FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                TFMGFluids.HEAVY_OIL.get().getFluidType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return TFMGBlocks.FOSSILSTONE.get().defaultBlockState();
                    } else {
                        return Blocks.SMOOTH_BASALT.defaultBlockState();
                    }
                }
        ));
        //
        FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                TFMGFluids.GASOLINE.get().getFluidType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return TFMGBlocks.FOSSILSTONE.get().defaultBlockState();
                    } else {
                        return Blocks.SMOOTH_BASALT.defaultBlockState();
                    }
                }
        ));
        FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                TFMGFluids.DIESEL.get().getFluidType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return TFMGBlocks.FOSSILSTONE.get().defaultBlockState();
                    } else {
                        return Blocks.SMOOTH_BASALT.defaultBlockState();
                    }
                }
        ));
        FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                TFMGFluids.NAPHTHA.get().getFluidType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return TFMGBlocks.FOSSILSTONE.get().defaultBlockState();
                    } else {
                        return Blocks.SMOOTH_BASALT.defaultBlockState();
                    }
                }
        ));
        FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                TFMGFluids.KEROSENE.get().getFluidType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return TFMGBlocks.FOSSILSTONE.get().defaultBlockState();
                    } else {
                        return Blocks.SMOOTH_BASALT.defaultBlockState();
                    }
                }
        ));
        FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                TFMGFluids.LUBRICATION_OIL.get().getFluidType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return TFMGBlocks.FOSSILSTONE.get().defaultBlockState();
                    } else {
                        return Blocks.SMOOTH_BASALT.defaultBlockState();
                    }
                }
        ));
        FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                TFMGFluids.COOLING_FLUID.get().getFluidType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return Blocks.BASALT.defaultBlockState();
                    } else {
                        return Blocks.SMOOTH_BASALT.defaultBlockState();
                    }
                }
        ));
    }

    public static void init() {
    }
}
