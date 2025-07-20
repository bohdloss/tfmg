package it.bohdloss.tfmg.recipes.jei;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import it.bohdloss.tfmg.recipes.CokingRecipe;
import it.bohdloss.tfmg.recipes.jei.machines.CokeOven;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class CokingCategory extends CreateRecipeCategory<CokingRecipe>  {
    private final CokeOven cokeOven = new CokeOven();

    public CokingCategory(Info<CokingRecipe> info) {
        super(info);
    }

    @Override
    protected void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull CokingRecipe recipe, @NotNull IFocusGroup focuses) {
        builder
                .addSlot(RecipeIngredientRole.INPUT, 1, 13)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getIngredients().get(0));

        builder
                .addSlot(RecipeIngredientRole.OUTPUT, 121, 90)
                .setBackground(getRenderedSlot(), -1, -1)
                .addItemStack(recipe.getRollableResults().get(0).getStack());

        //fluid

        if (!recipe.getFluidResults().isEmpty()) {
            addFluidSlot(builder, 160, 22, recipe.getPrimaryResult());
        }
        if (recipe.getFluidResults().size() >= 2) {
            addFluidSlot(builder, 160, 46, recipe.getSecondaryResult());
        }
        // builder
        //         .addSlot(RecipeIngredientRole.OUTPUT, 160, 46)
        //         .setBackground(getRenderedSlot(), -1, -1)
        //         .addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getFluidResults().get(1)))
        //         .addRichTooltipCallback(addFluidTooltip(recipe.getFluidResults().get(1).getAmount()));
//
        // builder
        //         .addSlot(RecipeIngredientRole.OUTPUT, 160, 22)
        //         .setBackground(getRenderedSlot(), -1, -1)
        //         .addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getFluidResults().get(0)))
        //         .addRichTooltipCallback(addFluidTooltip(recipe.getFluidResults().get(0).getAmount()));

    }

    @Override
    protected void draw(@NotNull CokingRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        cokeOven
                .draw(graphics, 65, 50);
        AllGuiTextures.JEI_ARROW.render(graphics, 20, 15);

        AllGuiTextures.JEI_ARROW.render(graphics, 115, 25);
        AllGuiTextures.JEI_ARROW.render(graphics, 115, 50);

        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 115, 73);
    }
}
