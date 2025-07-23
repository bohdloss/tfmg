package it.bohdloss.tfmg.recipes.jei;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import it.bohdloss.tfmg.recipes.DistillationRecipe;
import it.bohdloss.tfmg.registry.TFMGGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class DistillationCategory extends CreateRecipeCategory<DistillationRecipe> {
    public DistillationCategory(Info<DistillationRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull DistillationRecipe recipe, @NotNull IFocusGroup focuses) {
        FluidIngredient fluidIngredient = recipe.getInputFluid();

        int outputCount = recipe.getOutputCount();
        int yModifier = 60 - (outputCount * 10);
        int y = 147 - yModifier;

        //builder
        //		.addSlot(RecipeIngredientRole.INPUT, 18, 130-yModifier)
        //		.setBackground(getRenderedSlot(), -1, -1)
        //		.addIngredients(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getInputFluid().getMatchingFluidStacks()))
        //		.addRichTooltipCallback(addFluidTooltip(recipe.getInputFluid().getRequiredAmount()));

        addFluidSlot(builder,18,130-yModifier,recipe.getInputFluid());

        for(int i = 0; i < outputCount; i++) {
            y -= 24;

            addFluidSlot(builder,105,y,recipe.getFluidResults().get(i));
            //builder
            //		.addSlot(RecipeIngredientRole.OUTPUT, 105, y)
            //		.setBackground(getRenderedSlot(), -1, -1)
            //		.addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getFluidResults().get(i)))
            //		.addRichTooltipCallback(addFluidTooltip(recipe.getFluidResults().get(i).getAmount()));
        }

    }

    @Override
    public void draw(@NotNull DistillationRecipe recipe, @NotNull IRecipeSlotsView iRecipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        int outputCount = recipe.getOutputCount();
        int yModifier = 60 -(outputCount*10);
        int y = 126-yModifier;


        TFMGGuiTextures.DISTILLATION_TOWER_BOTTOM.render(graphics, 10, y);
        //	TFMGGuiTextures.DISTILLATION_TOWER_FIRE.render(matrixStack,10,y+24);
        AllGuiTextures.JEI_ARROW.render(graphics, 56, y);

        for(int i = 0; i < (outputCount - 1); i++){
            y -= 24;
            TFMGGuiTextures.DISTILLATION_TOWER_MIDDLE.render(graphics,10, y);
            AllGuiTextures.JEI_ARROW.render(graphics, 56, y);
        }
        y -= 12;
        TFMGGuiTextures.DISTILLATION_TOWER_TOP.render(graphics,10, y);
    }
}
