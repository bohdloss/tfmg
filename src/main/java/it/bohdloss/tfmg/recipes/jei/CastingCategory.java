package it.bohdloss.tfmg.recipes.jei;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import it.bohdloss.tfmg.recipes.CastingRecipe;
import it.bohdloss.tfmg.recipes.jei.machines.CastingSetup;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.NotNull;

public class CastingCategory extends CreateRecipeCategory<CastingRecipe> {
    private final CastingSetup castingSetup = new CastingSetup();

    public CastingCategory(Info<CastingRecipe> info) {
        super(info);
    }

    @Override
    protected void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull CastingRecipe recipe, @NotNull IFocusGroup focuses) {
        RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
        builder
                .addSlot(RecipeIngredientRole.OUTPUT, 130, 20)
                .setBackground(getRenderedSlot(), -1, -1)
                .addItemStack(recipe.getResultItem(registryAccess));

        addFluidSlot(builder, 15, 20, recipe.getIngredient());
    }

    @Override
    protected void draw(@NotNull CastingRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        castingSetup
                .draw(graphics, 72, 40);

        AllGuiTextures.JEI_ARROW.render(graphics, 78, 23);
    }
}
