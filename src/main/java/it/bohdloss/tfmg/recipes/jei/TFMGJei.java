package it.bohdloss.tfmg.recipes.jei;

import com.simibubi.create.compat.jei.*;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.equipment.blueprint.BlueprintScreen;
import com.simibubi.create.content.logistics.filter.AbstractFilterScreen;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerScreen;
import com.simibubi.create.content.trains.schedule.ScheduleScreen;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.recipes.*;
import it.bohdloss.tfmg.registry.TFMGBlocks;
import it.bohdloss.tfmg.registry.TFMGItems;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class TFMGJei implements IModPlugin {
    private static final ResourceLocation ID = TFMG.asResource("jei_plugin");

    private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();
    private IIngredientManager ingredientManager;

    private void loadCategories() {
        allCategories.clear();

        CreateRecipeCategory<?>

                advancedDistillation = builder(DistillationRecipe.class)
                .addTypedRecipes(TFMGRecipeTypes.DISTILLATION)
                .catalyst(TFMGBlocks.STEEL_DISTILLATION_CONTROLLER::get)
                .catalyst(TFMGBlocks.STEEL_DISTILLATION_OUTPUT::get)
                .itemIcon(TFMGBlocks.STEEL_DISTILLATION_CONTROLLER.get())
                .emptyBackground(177, 150)
                .build("advanced_distillation", DistillationCategory::new),

                coking = builder(CokingRecipe.class)
                        .addTypedRecipes(TFMGRecipeTypes.COKING)
                        .catalyst(TFMGBlocks.COKE_OVEN::get)
                        .itemIcon(TFMGBlocks.COKE_OVEN.get())
                        .emptyBackground(177, 123)
                        .build("coking", CokingCategory::new),

//                chemical_vat = builder(VatMachineRecipe.class)
//                        .addTypedRecipes(TFMGRecipeTypes.VAT_MACHINE_RECIPE)
//                        .catalyst(TFMGBlocks.STEEL_CHEMICAL_VAT::get)
//                        .catalyst(TFMGBlocks.CAST_IRON_CHEMICAL_VAT::get)
//                        .catalyst(TFMGBlocks.FIREPROOF_CHEMICAL_VAT::get)
//                        .catalyst(TFMGBlocks.INDUSTRIAL_MIXER::get)
//                        .catalyst(TFMGBlocks.ELECTRODE_HOLDER::get)
//                        .itemIcon(TFMGBlocks.STEEL_CHEMICAL_VAT.get())
//                        .emptyBackground(177, 123)
//                        .build("chemical_vat", ChemicalVatCategory::new),

                industrial_blasting = builder(IndustrialBlastingRecipe.class)
                        .addTypedRecipes(TFMGRecipeTypes.INDUSTRIAL_BLASTING)
                        .catalyst(TFMGBlocks.BLAST_FURNACE_OUTPUT::get)
                        .catalyst(TFMGBlocks.FIREPROOF_BRICKS::get)
                        .catalyst(TFMGBlocks.FIREPROOF_BRICK_REINFORCEMENT::get)
                        .catalyst(TFMGBlocks.BLAST_FURNACE_REINFORCEMENT::get)
                        .catalyst(TFMGBlocks.BLAST_FURNACE_HATCH::get)
                        .itemIcon(TFMGBlocks.BLAST_FURNACE_OUTPUT.get())
                        .emptyBackground(177, 150)
                        .build("industrial_blasting", IndustrialBlastingCategory::new),


                hot_blast = builder(HotBlastRecipe.class)
                        .addTypedRecipes(TFMGRecipeTypes.HOT_BLAST)
                        .catalyst(TFMGBlocks.BLAST_STOVE::get)
                        .catalyst(TFMGBlocks.BLAST_FURNACE_HATCH::get)
                        .itemIcon(TFMGBlocks.BLAST_STOVE.get())
                        .emptyBackground(177, 110)
                        .build("hot_blast", HotBlastCategory::new),

//                polarizing = builder(PolarizingRecipe.class)
//                        .addTypedRecipes(TFMGRecipeTypes.POLARIZING)
//                        .catalyst(Blocks.LIGHTNING_ROD::asItem)
//                        .catalyst(TFMGBlocks.POLARIZER::get)
//                        .itemIcon(TFMGBlocks.POLARIZER.get())
//                        .emptyBackground(177, 53)
//                        .build("polarizing", PolarizingCategory::new),

                winding = builder(WindingRecipe.class)
                        .addTypedRecipes(TFMGRecipeTypes.WINDING)
                        .catalyst(TFMGBlocks.WINDING_MACHINE::get)
                        .catalyst(TFMGItems.COPPER_SPOOL::get)
                        .catalyst(TFMGItems.CONSTANTAN_SPOOL::get)
                        .itemIcon(TFMGBlocks.WINDING_MACHINE.get())
                        .emptyBackground(177, 53)
                        .build("winding", WindingCategory::new),

                casting = builder(CastingRecipe.class)
                        .addTypedRecipes(TFMGRecipeTypes.CASTING)
                        .catalyst(TFMGBlocks.CASTING_BASIN::get)
                        .itemIcon(TFMGBlocks.CASTING_BASIN.get())
                        .emptyBackground(177, 53)
                        .build("casting", CastingCategory::new);

    }

    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }

    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories();
        registration.addRecipeCategories(allCategories.toArray(IRecipeCategory[]::new));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ingredientManager = registration.getIngredientManager();

        allCategories.forEach(c -> c.registerRecipes(registration));

        registration.addRecipes(RecipeTypes.CRAFTING, ToolboxColoringRecipeMaker.createRecipes().toList());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        allCategories.forEach(c -> c.registerCatalysts(registration));
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new BlueprintTransferHandler(), RecipeTypes.CRAFTING);
    }

    /**
     * check
     */
    /*
    @Override
    public void registerFluidSubtypes(ISubtypeRegistration registration) {
        PotionFluidSubtypeInterpreter interpreter = new PotionFluidSubtypeInterpreter();
        PotionFluid potionFluid = AllFluids.POTION.get();
        registration.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, potionFluid.getSource(), interpreter);
        registration.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, potionFluid.getFlowing(), interpreter);
    }

     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(AbstractSimiContainerScreen.class, new SlotMover());

        registration.addGhostIngredientHandler(AbstractFilterScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(BlueprintScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(LinkedControllerScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(ScheduleScreen.class, new GhostIngredientHandler());
    }

    private class CategoryBuilder<T extends Recipe<?>> extends CreateRecipeCategory.Builder<T> {
        public CategoryBuilder(Class<? extends T> recipeClass) {
            super(recipeClass);
        }

        @Override
        public CreateRecipeCategory<T> build(ResourceLocation id, CreateRecipeCategory.Factory<T> factory) {
            CreateRecipeCategory<T> category = super.build(id, factory);
            allCategories.add(category);
            return category;
        }
    }

    public static void consumeAllRecipes(Consumer<? super RecipeHolder<?>> consumer) {
        Minecraft.getInstance()
                .getConnection()
                .getRecipeManager()
                .getRecipes()
                .forEach(consumer);
    }

    public static <T extends Recipe<?>> void consumeTypedRecipes(Consumer<T> consumer, RecipeType<?> type) {
        Map<ResourceLocation, Recipe<?>> map = (Map<ResourceLocation, Recipe<?>>) Minecraft.getInstance()
                .getConnection()
                .getRecipeManager().getRecipes();
        if (map != null) {
            map.values().forEach(recipe -> consumer.accept((T) recipe));
        }
    }

    public static List<Recipe<?>> getTypedRecipes(RecipeType<?> type) {
        List<Recipe<?>> recipes = new ArrayList<>();
        consumeTypedRecipes(recipes::add, type);
        return recipes;
    }

    public static List<Recipe<?>> getTypedRecipesExcluding(RecipeType<?> type, Predicate<Recipe<?>> exclusionPred) {
        List<Recipe<?>> recipes = getTypedRecipes(type);
        recipes.removeIf(exclusionPred);
        return recipes;
    }

    public static boolean doInputsMatch(Recipe<?> recipe1, Recipe<?> recipe2) {
        if (recipe1.getIngredients()
                .isEmpty()
                || recipe2.getIngredients()
                .isEmpty()) {
            return false;
        }
        ItemStack[] matchingStacks = recipe1.getIngredients()
                .get(0)
                .getItems();
        if (matchingStacks.length == 0) {
            return false;
        }
        return recipe2.getIngredients()
                .get(0)
                .test(matchingStacks[0]);
    }
}
