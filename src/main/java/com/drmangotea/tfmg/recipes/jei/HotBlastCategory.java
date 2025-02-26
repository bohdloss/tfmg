package com.drmangotea.tfmg.recipes.jei;

import com.drmangotea.tfmg.recipes.DistillationRecipe;
import com.drmangotea.tfmg.recipes.HotBlastRecipe;
import com.drmangotea.tfmg.registry.TFMGGuiTextures;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class HotBlastCategory extends CreateRecipeCategory<HotBlastRecipe> {

    public HotBlastCategory(Info<HotBlastRecipe> info) {
        super(info);
    }


    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HotBlastRecipe recipe, IFocusGroup focuses) {


        builder
                .addSlot(RecipeIngredientRole.INPUT, 18, 52)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getFluidIngredients().get(0).getMatchingFluidStacks()))
                .addTooltipCallback(addFluidTooltip(recipe.getFluidIngredients().get(0).getRequiredAmount()));

		builder
				.addSlot(RecipeIngredientRole.INPUT, 18, 74)
				.setBackground(getRenderedSlot(), -1, -1)
				.addIngredients(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getFluidIngredients().get(1).getMatchingFluidStacks()))
				.addTooltipCallback(addFluidTooltip(recipe.getFluidIngredients().get(1).getRequiredAmount()));

        /// /
        builder
                .addSlot(RecipeIngredientRole.OUTPUT, 105, 51)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getFluidResults().get(0)))
                .addTooltipCallback(addFluidTooltip(recipe.getFluidResults().get(0).getAmount()));

		builder
				.addSlot(RecipeIngredientRole.OUTPUT, 105, 75)
				.setBackground(getRenderedSlot(), -1, -1)
				.addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getFluidResults().get(1)))
				.addTooltipCallback(addFluidTooltip(recipe.getFluidResults().get(1).getAmount()));


    }

    @Override
    public void draw(HotBlastRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {


        TFMGGuiTextures.BLAST_STOVE.render(graphics, 10, 0);

        AllGuiTextures.JEI_ARROW.render(graphics, 56, 55);
        AllGuiTextures.JEI_ARROW.render(graphics, 56, 78);


    }

}
