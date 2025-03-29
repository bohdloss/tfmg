package com.drmangotea.tfmg.datagen.recipes.values;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.recipe.CompatMetals;
import com.simibubi.create.foundation.data.recipe.Mods;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import static com.drmangotea.tfmg.datagen.recipes.TFMGRecipeProvider.I.*;


public class TFMGStandardRecipeGen extends TFMGRecipeProvider {
    private Marker MATERIALS = enterFolder("kinetics");
    GeneratedRecipe
            STEEL_COMPACTING =
            metalCompacting(ImmutableList.of(TFMGItems.STEEL_NUGGET, TFMGItems.STEEL_INGOT, TFMGBlocks.STEEL_BLOCK),
                    ImmutableList.of(I::steelNugget, I::steelIngot, I::steelBlock)),
            ALUMINUM_COMPACTING =
                    metalCompacting(ImmutableList.of(TFMGItems.ALUMINUM_NUGGET, TFMGItems.ALUMINUM_INGOT, TFMGBlocks.ALUMINUM_BLOCK),
                            ImmutableList.of(I::aluminumNugget, I::aluminumIngot, I::aluminumBlock)),
            CAST_IRON_COMPACTING =
                    metalCompacting(ImmutableList.of(TFMGItems.CAST_IRON_NUGGET, TFMGItems.CAST_IRON_INGOT, TFMGBlocks.CAST_IRON_BLOCK),
                            ImmutableList.of(I::castIronNugget, I::castIronIngot, I::castIronBlock)),
            LEAD_COMPACTING =
                    metalCompacting(ImmutableList.of(TFMGItems.LEAD_NUGGET, TFMGItems.LEAD_INGOT, TFMGBlocks.LEAD_BLOCK),
                            ImmutableList.of(I::leadNugget, I::leadIngot, I::leadBlock)),
            NICKEL_COMPACTING =
                    metalCompacting(ImmutableList.of(TFMGItems.NICKEL_NUGGET, TFMGItems.NICKEL_INGOT, TFMGBlocks.NICKEL_BLOCK),
                            ImmutableList.of(I::nickelNugget, I::nickelIngot, I::nickelBlock)),
            LITHIUM_COMPACTING =
                    metalCompacting(ImmutableList.of(TFMGItems.LITHIUM_NUGGET, TFMGItems.LITHIUM_INGOT, TFMGBlocks.LITHIUM_BLOCK),
                            ImmutableList.of(I::lithiumNugget, I::lithiumIngot, I::lithiumBlock)),
            CONSTANTAN_COMPACTING =
                    metalCompacting(ImmutableList.of(TFMGItems.CONSTANTAN_NUGGET, TFMGItems.CONSTANTAN_INGOT, TFMGBlocks.CONSTANTAN_BLOCK),
                            ImmutableList.of(I::constantanNugget, I::constantanIngot, I::constantanBlock)),

    //
    LEAD_FLYWHEEL = create(TFMGBlocks.LEAD_FLYWHEEL)
            .unlockedBy(TFMGItems.LEAD_INGOT::get)
            .viaShaped(b -> b
                    .define('C', leadIngot())
                    .define('S', shaft())
                    .pattern("CCC")
                    .pattern("CSC")
                    .pattern("CCC")),
            STEEL_FLYWHEEL = create(TFMGBlocks.STEEL_FLYWHEEL)
                    .unlockedBy(TFMGItems.STEEL_INGOT::get)
                    .viaShaped(b -> b
                            .define('C', tfmgSteelIngot())
                            .define('S', shaft())
                            .pattern("CCC")
                            .pattern("CSC")
                            .pattern("CCC")),
            CAST_IRON_FLYWHEEL = create(TFMGBlocks.CAST_IRON_FLYWHEEL)
                    .unlockedBy(TFMGItems.CAST_IRON_INGOT::get)
                    .viaShaped(b -> b
                            .define('C', leadIngot())
                            .define('S', shaft())
                            .pattern("CCC")
                            .pattern("CSC")
                            .pattern("CCC")),
            NICKEL_FLYWHEEL = create(TFMGBlocks.NICKEL_FLYWHEEL)
                    .unlockedBy(TFMGItems.NICKEL_INGOT::get)
                    .viaShaped(b -> b
                            .define('C', nickelIngot())
                            .define('S', shaft())
                            .pattern("CCC")
                            .pattern("CSC")
                            .pattern("CCC")),
            ALUMINUM_FLYWHEEL = create(TFMGBlocks.ALUMINUM_FLYWHEEL)
                    .unlockedBy(TFMGItems.ALUMINUM_INGOT::get)
                    .viaShaped(b -> b
                            .define('C', aluminumIngot())
                            .define('S', shaft())
                            .pattern("CCC")
                            .pattern("CSC")
                            .pattern("CCC")),
            STEEL_COGWHEEL = create(TFMGBlocks.STEEL_COGWHEEL).returns(4)
                    .unlockedBy(() -> TFMGItems.STEEL_INGOT)
                    .viaShapeless(b -> b
                            .requires(Ingredient.of(tfmgSteelIngot()))
                            .requires(Ingredient.of(AllBlocks.SHAFT))),
            LARGE_STEEL_COGWHEEL = create(TFMGBlocks.LARGE_STEEL_COGWHEEL).returns(2)
                    .unlockedBy(() -> TFMGItems.STEEL_INGOT)
                    .viaShapeless(b -> b
                            .requires(Ingredient.of(tfmgSteelIngot()))
                            .requires(Ingredient.of(tfmgSteelIngot()))
                            .requires(Ingredient.of(AllBlocks.SHAFT))),
            ALUMINUM_COGWHEEL = create(TFMGBlocks.ALUMINUM_COGWHEEL).returns(4)
                    .unlockedBy(() -> TFMGItems.ALUMINUM_INGOT)
                    .viaShapeless(b -> b
                            .requires(Ingredient.of(aluminumIngot()))
                            .requires(Ingredient.of(AllBlocks.SHAFT))),
            LARGE_ALUMINUM_COGWHEEL = create(TFMGBlocks.LARGE_ALUMINUM_COGWHEEL).returns(2)
                    .unlockedBy(() -> TFMGItems.ALUMINUM_INGOT)
                    .viaShapeless(b -> b
                            .requires(Ingredient.of(aluminumIngot()))
                            .requires(Ingredient.of(aluminumIngot()))
                            .requires(Ingredient.of(AllBlocks.SHAFT))),

    FIREPROOF_BRICKS = create(TFMGBlocks.FIREPROOF_BRICKS)
            .unlockedBy(TFMGItems.FIRECLAY_BALL::get)
            .viaShaped(b -> b
                    .define('B', TFMGItems.FIREPROOF_BRICK)
                    .pattern("BB ")
                    .pattern("BB ")
                    .pattern("   ")),
            FIRECLAY_BLOCK = create(TFMGBlocks.FIRECLAY)
                    .unlockedBy(TFMGItems.FIRECLAY_BALL::get)
                    .viaShaped(b -> b
                            .define('B', TFMGItems.FIRECLAY_BALL)
                            .pattern("BB ")
                            .pattern("BB ")
                            .pattern("   ")),

    BLAST_FURNACE_OUTPUT = create(TFMGBlocks.BLAST_FURNACE_OUTPUT)
            .unlockedBy(TFMGItems.FIRECLAY_BALL::get)
            .viaShaped(b -> b
                    .define('B', TFMGItems.FIRECLAY_BALL)
                    .define('C', castIronPipe())
                    .pattern("BCB")
                    .pattern("CCC")
                    .pattern("BCB")),
            AIR_INTAKE = create(TFMGBlocks.AIR_INTAKE)
                    .unlockedBy(AllItems.PROPELLER::get)
                    .viaShaped(b -> b
                            .define('B', AllBlocks.ANDESITE_BARS)
                            .define('T', castIronPipe())
                            .define('P', AllItems.PROPELLER)
                            .define('C', AllBlocks.INDUSTRIAL_IRON_BLOCK)
                            .define('G', AllBlocks.COGWHEEL)
                            .define('S', shaft())
                            .pattern("SPT")
                            .pattern("GCG")
                            .pattern(" B ")),

    LITHIUM_TORCH = create(TFMGBlocks.LITHIUM_TORCH).returns(4)
            .unlockedBy(TFMGItems.LITHIUM_INGOT::get)
            .viaShaped(b -> b
                    .define('L', lithiumIngot())
                    .define('A', aluminumIngot())
                    .pattern(" L ")
                    .pattern(" A ")
                    .pattern("   ")),

    STEEL_GEARBOX = create(TFMGBlocks.STEEL_GEARBOX)
            .unlockedBy(TFMGBlocks.HEAVY_MACHINERY_CASING::asItem)
            .viaShaped(b -> b
                    .define('S', shaft())
                    .define('C', heavyMachineryCasing())
                    .pattern(" S ")
                    .pattern("SCS")
                    .pattern(" S ")),

    STEEL_TANK = create(TFMGBlocks.STEEL_FLUID_TANK).returns(2)
            .unlockedBy(TFMGItems.STEEL_INGOT::get)
            .viaShaped(b -> b
                    .define('B', Blocks.BARREL)
                    .define('P', heavyPlate())
                    .pattern(" P ")
                    .pattern(" B ")
                    .pattern(" P ")),

    ALUMINUM_TANK = create(TFMGBlocks.ALUMINUM_FLUID_TANK)
            .unlockedBy(TFMGItems.ALUMINUM_INGOT::get)
            .viaShaped(b -> b
                    .define('B', Blocks.BARREL)
                    .define('P', aluminumIngot())
                    .pattern(" P ")
                    .pattern(" B ")
                    .pattern(" P ")),

    CAST_IRON_TANK = create(TFMGBlocks.CAST_IRON_FLUID_TANK)
            .unlockedBy(TFMGItems.CAST_IRON_INGOT::get)
            .viaShaped(b -> b
                    .define('B', Blocks.BARREL)
                    .define('P', castIronIngot())
                    .pattern(" P ")
                    .pattern(" B ")
                    .pattern(" P ")),

    EXHAUST = create(TFMGBlocks.EXHAUST)
            .unlockedBy(TFMGItems.CAST_IRON_INGOT::get)
            .viaShaped(b -> b
                    .define('B', Blocks.IRON_BARS)
                    .define('P', castIronPipe())
                    .define('C', castIronIngot())
                    .pattern("BPB")
                    .pattern("BPB")
                    .pattern("CPC")),

    FLARESTACK = create(TFMGBlocks.FLARESTACK)
            .unlockedBy(TFMGItems.STEEL_INGOT::get)
            .viaShaped(b -> b
                    .define('B', Blocks.IRON_BARS)
                    .define('P', castIronPipe())
                    .define('C', steelIngot())
                    .define('S', TFMGItems.SPARK_PLUG)
                    .pattern("SPS")
                    .pattern("BPB")
                    .pattern("CPC")),

    BRICK_SMOKESTACK = create(TFMGBlocks.BRICK_SMOKESTACK).returns(4)
            .unlockedBy(TFMGBlocks.INDUSTRIAL_PIPE::get)
            .viaShaped(b -> b
                    .define('B', Blocks.BRICKS)
                    .define('P', industrialPipe())
                    .pattern("BPB")
                    .pattern("BPB")
                    .pattern("BPB")),
            CONCRETE_SMOKESTACK = create(TFMGBlocks.CONCRETE_SMOKESTACK).returns(4)
                    .unlockedBy(TFMGBlocks.INDUSTRIAL_PIPE::get)
                    .viaShaped(b -> b
                            .define('B', TFMGBlocks.CONCRETE.block)
                            .define('P', industrialPipe())
                            .pattern("BPB")
                            .pattern("BPB")
                            .pattern("BPB")),
            METAL_SMOKESTACK = create(TFMGBlocks.METAL_SMOKESTACK).returns(4)
                    .unlockedBy(TFMGBlocks.INDUSTRIAL_PIPE::get)
                    .viaShaped(b -> b
                            .define('B', steelNugget())
                            .define('P', industrialPipe())
                            .pattern("BPB")
                            .pattern("BPB")
                            .pattern("BPB")),
            INDUSTRIAL_MIXER = create(TFMGBlocks.INDUSTRIAL_MIXER)
                    .unlockedBy(TFMGBlocks.HEAVY_MACHINERY_CASING::get)
                    .viaShaped(b -> b
                            .define('S', shaft())
                            .define('C', heavyMachineryCasing())
                            .define('K', TFMGBlocks.STEEL_COGWHEEL)
                            .define('M', steelMechanism())
                            .pattern(" S ")
                            .pattern("KCM")
                            .pattern(" S ")),

    HEAVY_PLATED_DOOR = create(TFMGBlocks.HEAVY_PLATED_DOOR).returns(3)
            .unlockedBy(TFMGItems.STEEL_INGOT::get)
            .viaShaped(b -> b
                    .define('I', steelIngot())
                    .pattern("II ")
                    .pattern("II ")
                    .pattern("II ")),
            ALUMINUM_DOOR = create(TFMGBlocks.ALUMINUM_DOOR).returns(3)
                    .unlockedBy(TFMGItems.ALUMINUM_INGOT::get)
                    .viaShaped(b -> b
                            .define('I', aluminumIngot())
                            .pattern("II ")
                            .pattern("II ")
                            .pattern("II ")),

    STEEL_CASING_DOOR = create(TFMGBlocks.STEEL_CASING_DOOR)
            .unlockedBy(() -> TFMGItems.STEEL_INGOT)
            .viaShapeless(b -> b
                    .requires(steelCasing())
                    .requires(ItemTags.WOODEN_DOORS)),
            HEAVY_CASING_DOOR = create(TFMGBlocks.HEAVY_CASING_DOOR)
                    .unlockedBy(() -> TFMGItems.STEEL_INGOT)
                    .viaShapeless(b -> b
                            .requires(heavyMachineryCasing())
                            .requires(ItemTags.WOODEN_DOORS));


    /// ////////////////////


    String currentFolder = "";

    Marker enterFolder(String folder) {
        currentFolder = folder;
        return new Marker();
    }

    GeneratedRecipeBuilder create(Supplier<ItemLike> result) {
        return new GeneratedRecipeBuilder(currentFolder, result);
    }

    GeneratedRecipeBuilder create(ResourceLocation result) {
        return new GeneratedRecipeBuilder(currentFolder, result);
    }

    GeneratedRecipeBuilder create(ItemProviderEntry<? extends ItemLike> result) {
        return create(result::get);
    }

    TFMGRecipeProvider.GeneratedRecipe createSpecial(Supplier<? extends SimpleCraftingRecipeSerializer<?>> serializer, String recipeType,
                                                     String path) {
        ResourceLocation location = TFMG.asResource(recipeType + "/" + currentFolder + "/" + path);
        return register(consumer -> {
            SpecialRecipeBuilder b = SpecialRecipeBuilder.special(serializer.get());
            b.save(consumer, location.toString());
        });
    }

    TFMGRecipeProvider.GeneratedRecipe blastCrushedMetal(Supplier<? extends ItemLike> result, Supplier<? extends ItemLike> ingredient) {
        return create(result::get).withSuffix("_from_crushed")
                .viaCooking(ingredient::get)
                .rewardXP(.1f)
                .inBlastFurnace();
    }

    TFMGRecipeProvider.GeneratedRecipe blastModdedCrushedMetal(ItemEntry<? extends Item> ingredient, CompatMetals metal) {
        String metalName = metal.getName();
        for (Mods mod : metal.getMods()) {
            ResourceLocation ingot = mod.ingotOf(metalName);
            String modId = mod.getId();
            create(ingot).withSuffix("_compat_" + modId)
                    .whenModLoaded(modId)
                    .viaCooking(ingredient::get)
                    .rewardXP(.1f)
                    .inBlastFurnace();
        }
        return null;
    }

    TFMGRecipeProvider.GeneratedRecipe recycleGlass(BlockEntry<? extends Block> ingredient) {
        return create(() -> Blocks.GLASS).withSuffix("_from_" + ingredient.getId()
                        .getPath())
                .viaCooking(ingredient::get)
                .forDuration(50)
                .inFurnace();
    }

    TFMGRecipeProvider.GeneratedRecipe recycleGlassPane(BlockEntry<? extends Block> ingredient) {
        return create(() -> Blocks.GLASS_PANE).withSuffix("_from_" + ingredient.getId()
                        .getPath())
                .viaCooking(ingredient::get)
                .forDuration(50)
                .inFurnace();
    }

    TFMGRecipeProvider.GeneratedRecipe metalCompacting(List<ItemProviderEntry<? extends ItemLike>> variants,
                                                       List<Supplier<TagKey<Item>>> ingredients) {
        TFMGRecipeProvider.GeneratedRecipe result = null;
        for (int i = 0; i + 1 < variants.size(); i++) {
            ItemProviderEntry<? extends ItemLike> currentEntry = variants.get(i);
            ItemProviderEntry<? extends ItemLike> nextEntry = variants.get(i + 1);
            Supplier<TagKey<Item>> currentIngredient = ingredients.get(i);
            Supplier<TagKey<Item>> nextIngredient = ingredients.get(i + 1);

            result = create(nextEntry).withSuffix("_from_compacting")
                    .unlockedBy(currentEntry::get)
                    .viaShaped(b -> b.pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .define('#', currentIngredient.get()));

            result = create(currentEntry).returns(9)
                    .withSuffix("_from_decompacting")
                    .unlockedBy(nextEntry::get)
                    .viaShapeless(b -> b.requires(nextIngredient.get()));
        }
        return result;
    }

    TFMGRecipeProvider.GeneratedRecipe conversionCycle(List<ItemProviderEntry<? extends ItemLike>> cycle) {
        TFMGRecipeProvider.GeneratedRecipe result = null;
        for (int i = 0; i < cycle.size(); i++) {
            ItemProviderEntry<? extends ItemLike> currentEntry = cycle.get(i);
            ItemProviderEntry<? extends ItemLike> nextEntry = cycle.get((i + 1) % cycle.size());
            result = create(nextEntry).withSuffix("from_conversion")
                    .unlockedBy(currentEntry::get)
                    .viaShapeless(b -> b.requires(currentEntry.get()));
        }
        return result;
    }

    TFMGRecipeProvider.GeneratedRecipe clearData(ItemProviderEntry<? extends ItemLike> item) {
        return create(item).withSuffix("_clear")
                .unlockedBy(item::get)
                .viaShapeless(b -> b.requires(item.get()));
    }

    class GeneratedRecipeBuilder {

        private String path;
        private String suffix;
        private Supplier<? extends ItemLike> result;
        private ResourceLocation compatDatagenOutput;
        List<ICondition> recipeConditions;

        private Supplier<ItemPredicate> unlockedBy;
        private int amount;

        private GeneratedRecipeBuilder(String path) {
            this.path = path;
            this.recipeConditions = new ArrayList<>();
            this.suffix = "";
            this.amount = 1;
        }

        public GeneratedRecipeBuilder(String path, Supplier<? extends ItemLike> result) {
            this(path);
            this.result = result;
        }

        public GeneratedRecipeBuilder(String path, ResourceLocation result) {
            this(path);
            this.compatDatagenOutput = result;
        }

        GeneratedRecipeBuilder returns(int amount) {
            this.amount = amount;
            return this;
        }

        GeneratedRecipeBuilder unlockedBy(Supplier<? extends ItemLike> item) {
            this.unlockedBy = () -> ItemPredicate.Builder.item()
                    .of(item.get())
                    .build();
            return this;
        }

        GeneratedRecipeBuilder unlockedByTag(Supplier<TagKey<Item>> tag) {
            this.unlockedBy = () -> ItemPredicate.Builder.item()
                    .of(tag.get())
                    .build();
            return this;
        }

        GeneratedRecipeBuilder whenModLoaded(String modid) {
            return withCondition(new ModLoadedCondition(modid));
        }

        GeneratedRecipeBuilder whenModMissing(String modid) {
            return withCondition(new NotCondition(new ModLoadedCondition(modid)));
        }

        GeneratedRecipeBuilder withCondition(ICondition condition) {
            recipeConditions.add(condition);
            return this;
        }

        GeneratedRecipeBuilder withSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        TFMGRecipeProvider.GeneratedRecipe viaShaped(UnaryOperator<ShapedRecipeBuilder> builder) {
            return register(consumer -> {
                ShapedRecipeBuilder b = builder.apply(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        TFMGRecipeProvider.GeneratedRecipe viaShapeless(UnaryOperator<ShapelessRecipeBuilder> builder) {
            return register(consumer -> {
                ShapelessRecipeBuilder b = builder.apply(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        TFMGRecipeProvider.GeneratedRecipe viaNetheriteSmithing(Supplier<? extends Item> base, Supplier<Ingredient> upgradeMaterial) {
            return register(consumer -> {
                SmithingTransformRecipeBuilder b =
                        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.of(base.get()), upgradeMaterial.get(), RecipeCategory.COMBAT, result.get()
                                        .asItem());
                b.unlocks("has_item", inventoryTrigger(ItemPredicate.Builder.item()
                        .of(base.get())
                        .build()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        private ResourceLocation createSimpleLocation(String recipeType) {
            return TFMG.asResource(recipeType + "/" + getRegistryName().getPath() + suffix);
        }

        private ResourceLocation createLocation(String recipeType) {
            return TFMG.asResource(recipeType + "/" + path + "/" + getRegistryName().getPath() + suffix);
        }

        private ResourceLocation getRegistryName() {
            return compatDatagenOutput == null ? CatnipServices.REGISTRIES.getKeyOrThrow(result.get()
                    .asItem()) : compatDatagenOutput;
        }

        GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder viaCooking(Supplier<? extends ItemLike> item) {
            return unlockedBy(item).viaCookingIngredient(() -> Ingredient.of(item.get()));
        }

        GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder viaCookingTag(Supplier<TagKey<Item>> tag) {
            return unlockedByTag(tag).viaCookingIngredient(() -> Ingredient.of(tag.get()));
        }

        GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder viaCookingIngredient(Supplier<Ingredient> ingredient) {
            return new GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder(ingredient);
        }

        class GeneratedCookingRecipeBuilder {

            private Supplier<Ingredient> ingredient;
            private float exp;
            private int cookingTime;

            private final RecipeSerializer<? extends AbstractCookingRecipe> FURNACE = RecipeSerializer.SMELTING_RECIPE,
                    SMOKER = RecipeSerializer.SMOKING_RECIPE, BLAST = RecipeSerializer.BLASTING_RECIPE,
                    CAMPFIRE = RecipeSerializer.CAMPFIRE_COOKING_RECIPE;

            GeneratedCookingRecipeBuilder(Supplier<Ingredient> ingredient) {
                this.ingredient = ingredient;
                cookingTime = 200;
                exp = 0;
            }

            GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder forDuration(int duration) {
                cookingTime = duration;
                return this;
            }

            GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder rewardXP(float xp) {
                exp = xp;
                return this;
            }

            TFMGRecipeProvider.GeneratedRecipe inFurnace() {
                return inFurnace(b -> b);
            }

            TFMGRecipeProvider.GeneratedRecipe inFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                return create(FURNACE, builder, 1);
            }

            TFMGRecipeProvider.GeneratedRecipe inSmoker() {
                return inSmoker(b -> b);
            }

            TFMGRecipeProvider.GeneratedRecipe inSmoker(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(FURNACE, builder, 1);
                create(CAMPFIRE, builder, 3);
                return create(SMOKER, builder, .5f);
            }

            TFMGRecipeProvider.GeneratedRecipe inBlastFurnace() {
                return inBlastFurnace(b -> b);
            }

            TFMGRecipeProvider.GeneratedRecipe inBlastFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(FURNACE, builder, 1);
                return create(BLAST, builder, .5f);
            }

            private TFMGRecipeProvider.GeneratedRecipe create(RecipeSerializer<? extends AbstractCookingRecipe> serializer,
                                                              UnaryOperator<SimpleCookingRecipeBuilder> builder, float cookingTimeModifier) {
                return register(consumer -> {
                    boolean isOtherMod = compatDatagenOutput != null;

                    SimpleCookingRecipeBuilder b = builder.apply(SimpleCookingRecipeBuilder.generic(ingredient.get(),
                            RecipeCategory.MISC, isOtherMod ? Items.DIRT : result.get(), exp,
                            (int) (cookingTime * cookingTimeModifier), serializer));

                    if (unlockedBy != null)
                        b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));

                    b.save(consumer::accept, createSimpleLocation(CatnipServices.REGISTRIES.getKeyOrThrow(serializer)
                            .getPath()));
                });
            }
        }
    }


    public TFMGStandardRecipeGen(PackOutput packOutput) {
        super(packOutput);
    }

    private static class ModdedCookingRecipeResult implements FinishedRecipe {

        private FinishedRecipe wrapped;
        private ResourceLocation outputOverride;
        private List<ICondition> conditions;

        public ModdedCookingRecipeResult(FinishedRecipe wrapped, ResourceLocation outputOverride,
                                         List<ICondition> conditions) {
            this.wrapped = wrapped;
            this.outputOverride = outputOverride;
            this.conditions = conditions;
        }

        @Override
        public ResourceLocation getId() {
            return wrapped.getId();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return wrapped.getType();
        }

        @Override
        public JsonObject serializeAdvancement() {
            return wrapped.serializeAdvancement();
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return wrapped.getAdvancementId();
        }

        @Override
        public void serializeRecipeData(JsonObject object) {
            wrapped.serializeRecipeData(object);
            object.addProperty("result", outputOverride.toString());

            JsonArray conds = new JsonArray();
            conditions.forEach(c -> conds.add(CraftingHelper.serialize(c)));
            object.add("conditions", conds);
        }

    }


}
