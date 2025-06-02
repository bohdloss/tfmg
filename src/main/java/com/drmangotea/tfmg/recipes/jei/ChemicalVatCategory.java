package com.drmangotea.tfmg.recipes.jei;

import com.drmangotea.tfmg.recipes.PolarizingRecipe;
import com.drmangotea.tfmg.recipes.VatMachineRecipe;
import com.drmangotea.tfmg.recipes.jei.machines.Polarizer;
import com.drmangotea.tfmg.registry.TFMGGuiTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class ChemicalVatCategory extends CreateRecipeCategory<VatMachineRecipe> {

    public ChemicalVatCategory(Info<VatMachineRecipe> info) {
        super(info);
    }

    public void setRecipe(IRecipeLayoutBuilder builder, VatMachineRecipe recipe, IFocusGroup focuses) {
        //builder.addSlot(RecipeIngredientRole.INPUT, 15, 9).setBackground(getRenderedSlot(), -1, -1).addIngredients(recipe.getIngredients().get(0));
        //builder.addSlot(RecipeIngredientRole.OUTPUT, 140, 28).setBackground(getRenderedSlot(), -1, -1).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));

        int fluidCount = recipe.getFluidIngredients().size();
        int pos = 55;
        int width = ((fluidCount) * 20) / 2;
        int movement = fluidCount != 4 ? 1 : 0;
        if (fluidCount == 1)
            movement = 2;
        for (int i = 0; i < fluidCount; i++) {

            addFluidSlot(builder, pos - width + movement, recipe.getIngredients().isEmpty() ? 72 : 85, recipe.getFluidIngredients().get(i));

            pos += 21;
        }
        int itemCount = recipe.getIngredients().size();
        int itemPos = 55;
        int itemWidth = ((itemCount) * 20) / 2;
        int itemMovement = itemCount != 4 ? 1 : 0;
        if (itemCount == 1)
            itemMovement = 2;
        for (int i = 0; i < itemCount; i++) {

            builder.addSlot(RecipeIngredientRole.INPUT, itemPos - itemWidth + itemMovement, recipe.getFluidIngredients().isEmpty() ? 72 : 64).setBackground(getRenderedSlot(), -1, -1).addIngredients(recipe.getIngredients().get(i));

            itemPos += 21;
        }
        /////////////////////////////

        int fluidResultPos = 90;

        for (int i = 0; i < recipe.getFluidResults().size(); i++) {

            addFluidSlot(builder, 150, fluidResultPos, recipe.getFluidResults().get(i));

            fluidResultPos -= 21;
        }

        int itemResultPos = 90;

        for (int i = 0; i < recipe.getRollableResults().size(); i++) {

            builder
                    .addSlot(RecipeIngredientRole.OUTPUT, 128, itemResultPos)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addItemStack(recipe.getRollableResults().get(i).getStack())
                    .addRichTooltipCallback(addStochasticTooltip(recipe.getRollableResults().get(i)))
            ;

            itemResultPos -= 21;
        }
    }

    public void draw(VatMachineRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {

        List<String> machines = recipe.machines;
        List<String> allowedVatTypes = recipe.allowedVatTypes;


        TFMGGuiTextures.VAT.render(graphics, 0, 24);

        if (allowedVatTypes.contains("firebrick_lined_vat") && allowedVatTypes.size() == 1) {
            TFMGGuiTextures.FIREPROOF_BRICK_OVERLAY.render(graphics, 55 - 48, 32);
        }

        if (machines.contains("tfmg:mixing")) {
            TFMGGuiTextures.VAT_MACHINE.render(graphics, 55 - 12, 0);
            TFMGGuiTextures.MIXER.render(graphics, 55 - 19, 32);
        }
        if (machines.contains("tfmg:electrode")) {
            TFMGGuiTextures.VAT_MACHINE.render(graphics, 55 - 12 - 32, 0);
            TFMGGuiTextures.VAT_MACHINE.render(graphics, 55 - 12 + 32, 0);
            TFMGGuiTextures.ELECTRODE.render(graphics, 55 - 3 - 32, 32);
            TFMGGuiTextures.ELECTRODE.render(graphics, 55 - 3 + 32, 32);
        }
        if (machines.contains("tfmg:graphite_electrode")) {
            TFMGGuiTextures.VAT_MACHINE.render(graphics, 55 - 12 - 32, 0);
            TFMGGuiTextures.VAT_MACHINE.render(graphics, 55 - 12 + 32, 0);
            TFMGGuiTextures.VAT_MACHINE.render(graphics, 55 - 12, 0);
            TFMGGuiTextures.GRAPHITE_ELECTRODE.render(graphics, 55 - 4 - 32, 32);
            TFMGGuiTextures.GRAPHITE_ELECTRODE.render(graphics, 55 - 4 + 32, 32);
            TFMGGuiTextures.GRAPHITE_ELECTRODE.render(graphics, 55 - 4, 32);
        }
        if (recipe.getRequiredHeat() == HeatCondition.HEATED){
            TFMGGuiTextures.VAT_HEATER.render(graphics, 55 - 10, 109);
        }
        int pos = 55;
        int width = ((recipe.getFluidIngredients().size()) * 21) / 2;
        for (int i = 0; i < recipe.getFluidIngredients().size(); i++) {

            TFMGGuiTextures.SLOT.render(graphics, pos - width, recipe.getIngredients().isEmpty() ? 70 : 83);

            pos += 21;
        }
        int posItem = 55;
        int widthItem = ((recipe.getIngredients().size()) * 21) / 2;
        for (int i = 0; i < recipe.getIngredients().size(); i++) {

            TFMGGuiTextures.SLOT.render(graphics, posItem - widthItem, recipe.getFluidIngredients().isEmpty() ? 70 : 62);

            posItem += 21;
        }


        //AllGuiTextures.JEI_ARROW.render(graphics, 85, 32);
        //AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 4);


    }


}