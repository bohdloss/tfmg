package it.bohdloss.tfmg.recipes.jei;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import it.bohdloss.tfmg.recipes.HotBlastRecipe;
import it.bohdloss.tfmg.registry.TFMGGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class HotBlastCategory extends CreateRecipeCategory<HotBlastRecipe> {
    public HotBlastCategory(Info<HotBlastRecipe> info) {
        super(info);
    }

    @Override
    protected void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull HotBlastRecipe recipe, @NotNull IFocusGroup focuses) {
        addFluidSlot(builder,18,52,recipe.getPrimaryIngredient());
        addFluidSlot(builder,18,74,recipe.getSecondaryIngredient());

        addFluidSlot(builder,105,51,recipe.getPrimaryResult());
        addFluidSlot(builder,105,75,recipe.getSecondaryResult());
        //builder
        //        .addSlot(RecipeIngredientRole.INPUT, 18, 52)
        //        .setBackground(getRenderedSlot(), -1, -1)
        //        .addIngredients(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getFluidIngredients().get(0).getMatchingFluidStacks()))
        //        .addRichTooltipCallback(addFluidTooltip(recipe.getFluidIngredients().get(0).getRequiredAmount()));
//
        //builder
        //		.addSlot(RecipeIngredientRole.INPUT, 18, 74)
        //		.setBackground(getRenderedSlot(), -1, -1)
        //		.addIngredients(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getFluidIngredients().get(1).getMatchingFluidStacks()))
        //		.addRichTooltipCallback(addFluidTooltip(recipe.getFluidIngredients().get(1).getRequiredAmount()));
//
        ///// /
        //builder
        //        .addSlot(RecipeIngredientRole.OUTPUT, 105, 51)
        //        .setBackground(getRenderedSlot(), -1, -1)
        //        .addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getFluidResults().get(0)))
        //        .addRichTooltipCallback(addFluidTooltip(recipe.getFluidResults().get(0).getAmount()));
//
        //builder
        //		.addSlot(RecipeIngredientRole.OUTPUT, 105, 75)
        //		.setBackground(getRenderedSlot(), -1, -1)
        //		.addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getFluidResults().get(1)))
        //		.addRichTooltipCallback(addFluidTooltip(recipe.getFluidResults().get(1).getAmount()));
    }

    @Override
    protected void draw(@NotNull HotBlastRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        TFMGGuiTextures.BLAST_STOVE.render(graphics, 10, 0);

        AllGuiTextures.JEI_ARROW.render(graphics, 56, 55);
        AllGuiTextures.JEI_ARROW.render(graphics, 56, 78);
    }
}
