package com.drmangotea.tfmg.recipes.jei;


import com.drmangotea.tfmg.recipes.IndustrialBlastingRecipe;
import com.drmangotea.tfmg.recipes.jei.machines.BlastFurnace;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class IndustrialBlastingCategory extends CreateRecipeCategory<IndustrialBlastingRecipe> {

	private final BlastFurnace blastFurnace = new BlastFurnace();

	public IndustrialBlastingCategory(Info<IndustrialBlastingRecipe> info) {
		super(info);
	}




	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, IndustrialBlastingRecipe recipe, IFocusGroup focuses) {


		builder
				.addSlot(RecipeIngredientRole.INPUT, 25, 13)
				.setBackground(getRenderedSlot(), -1, -1)
				.addIngredients(recipe.getIngredients().get(0));

		builder
				.addSlot(RecipeIngredientRole.INPUT, 5, 13)
				.setBackground(getRenderedSlot(), -1, -1)
				.addItemStack(new ItemStack(TFMGItems.COAL_COKE_DUST.get(),2));

		//fluid

		builder
				.addSlot(RecipeIngredientRole.OUTPUT,140, 117)
				.setBackground(getRenderedSlot(), -1, -1)
				.addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getFluidResults().get(0)))
				.addTooltipCallback(addFluidTooltip(recipe.getFluidResults().get(0).getAmount()));

       builder
       		.addSlot(RecipeIngredientRole.OUTPUT,160, 117)
       		.setBackground(getRenderedSlot(), -1, -1)
       		.addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getFluidResults().get(1)))
       		.addTooltipCallback(addFluidTooltip(recipe.getFluidResults().get(1).getAmount()));

	}

	@Override
	public void draw(IndustrialBlastingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		blastFurnace
				.draw(graphics, 50, 135);


		AllGuiTextures.JEI_ARROW.render(graphics, 96, 121);

		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 45, 15);

	}

}
