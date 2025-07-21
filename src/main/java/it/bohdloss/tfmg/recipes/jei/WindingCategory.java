package it.bohdloss.tfmg.recipes.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedDeployer;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import it.bohdloss.tfmg.content.electricity.connection.SpoolAmount;
import it.bohdloss.tfmg.content.electricity.connection.SpoolItem;
import it.bohdloss.tfmg.recipes.WindingRecipe;
import it.bohdloss.tfmg.recipes.jei.machines.WindingMachine;
import it.bohdloss.tfmg.registry.TFMGDataComponents;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class WindingCategory extends CreateRecipeCategory<WindingRecipe> {
    private final WindingMachine windingMachine = new WindingMachine();

    public WindingCategory(Info<WindingRecipe> info) {
        super(info);
    }

    public void setRecipe(IRecipeLayoutBuilder builder, WindingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 15, 9).setBackground(getRenderedSlot(), -1, -1).addIngredients(recipe.getIngredients().get(0));

        ItemStack coil = recipe.getIngredients().get(1).getItems()[0];
        coil.set(TFMGDataComponents.SPOOL_AMOUNT, new SpoolAmount(recipe.getProcessingDuration()));

        builder.addSlot(RecipeIngredientRole.INPUT, 15, 30).setBackground(getRenderedSlot(), -1, -1).addItemStack(coil);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 140, 28).setBackground(getRenderedSlot(), -1, -1).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
    }

    public void draw(WindingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {

        AllGuiTextures.JEI_ARROW.render(graphics, 85, 32);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 4);

        int coilColor = 0;

        if (recipe.getIngredients().get(1).getItems()[0].getItem() instanceof SpoolItem) {
            coilColor = recipe.getIngredients().get(1).getItems()[0].getBarColor();
        }
        this.windingMachine.draw(graphics, 48, 27,coilColor,true);
        graphics.drawString(Minecraft.getInstance().font, recipe.getProcessingDuration() + " Turns", 86.0F, 9.0F, 4210752, false);
    }

    public static class AssemblyWinding extends SequencedAssemblySubCategory {
        private final WindingMachine windingMachine = new WindingMachine();
        private final AnimatedDeployer deployer = new AnimatedDeployer();
        public AssemblyWinding() {
            super(25);
        }

        public void setRecipe(IRecipeLayoutBuilder builder, SequencedRecipe<?> recipe, IFocusGroup focuses, int x) {
            ItemStack coil = recipe.getRecipe().getIngredients().get(1).getItems()[0];
            coil.set(TFMGDataComponents.SPOOL_AMOUNT, new SpoolAmount(recipe.getRecipe().getProcessingDuration()));
            builder.addSlot(RecipeIngredientRole.INPUT, x + 4, 15).setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1).addItemStack(coil);
        }

        public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {

            PoseStack ms = graphics.pose();

            int coilColor = 0;

            if (recipe.getRecipe().getIngredients().get(1).getItems()[0].getItem() instanceof SpoolItem) {
                coilColor = recipe.getRecipe().getIngredients().get(1).getItems()[0].getBarColor();
            }
            windingMachine.offset = index;
            ms.pushPose();
            ms.translate(0.0, 67, 0.0);
            ms.scale(0.7F, 0.7F, 0.7F);
            this.windingMachine.draw(graphics, this.getWidth() / 2, 0,coilColor,false);
            ms.popPose();


        }
    }
}
