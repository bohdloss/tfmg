package com.drmangotea.tfmg.recipes.jei;

import com.drmangotea.tfmg.recipes.CokingRecipe;
import com.drmangotea.tfmg.recipes.jei.machines.CokeOven;
import com.drmangotea.tfmg.registry.TFMGFluids;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CokingCategory extends CreateRecipeCategory<CokingRecipe> {

	private final CokeOven cokeOven = new CokeOven();

	public CokingCategory(Info<CokingRecipe> info) {
		super(info);
	}




	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CokingRecipe recipe, IFocusGroup focuses) {

		RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
		builder
				.addSlot(RecipeIngredientRole.INPUT, 1, 13)
				.setBackground(getRenderedSlot(), -1, -1)
				.addIngredients(recipe.getIngredients().get(0));

		builder
				.addSlot(RecipeIngredientRole.OUTPUT, 121, 90)
				.setBackground(getRenderedSlot(), -1, -1)
				.addItemStack(recipe.getResultItem(registryAccess));

		//fluid

		builder
				.addSlot(RecipeIngredientRole.OUTPUT,160, 46)
				.setBackground(getRenderedSlot(), -1, -1)
				.addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getFluidResults().get(1)))
				.addTooltipCallback(addFluidTooltip(recipe.getFluidResults().get(1).getAmount()));

       builder
       		.addSlot(RecipeIngredientRole.OUTPUT,160, 22)
       		.setBackground(getRenderedSlot(), -1, -1)
       		.addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getFluidResults().get(0)))
       		.addTooltipCallback(addFluidTooltip(recipe.getFluidResults().get(0).getAmount()));

	}

	@Override
	public void draw(CokingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		cokeOven
				.draw(graphics, 65, 50);
		AllGuiTextures.JEI_ARROW.render(graphics, 20, 15);


		AllGuiTextures.JEI_ARROW.render(graphics, 115, 25);
		AllGuiTextures.JEI_ARROW.render(graphics, 115, 50);

		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 115, 73);

	}

}
