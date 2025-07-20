package it.bohdloss.tfmg.recipes.jei.machines;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import it.bohdloss.tfmg.registry.TFMGBlocks;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class CokeOven extends AnimatedKinetics {
    public CokeOven() {}

    @Override
    public void draw(@NotNull GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale =  23;

        blockElement(TFMGBlocks.COKE_OVEN.getDefaultState())
                .scale(scale)
                .render(graphics);
        blockElement(TFMGBlocks.COKE_OVEN.getDefaultState())
                .atLocal(0,0,1)
                .scale(scale)
                .render(graphics);
        blockElement(TFMGBlocks.COKE_OVEN.getDefaultState())
                .atLocal(0,0,2)
                .scale(scale)
                .render(graphics);
        blockElement(TFMGBlocks.COKE_OVEN.getDefaultState())
                .atLocal(0,1,0)
                .scale(scale)
                .render(graphics);
        blockElement(TFMGBlocks.COKE_OVEN.getDefaultState())
                .atLocal(0,1,1)
                .scale(scale)
                .render(graphics);
        blockElement(TFMGBlocks.COKE_OVEN.getDefaultState())
                .atLocal(0,1,2)
                .scale(scale)
                .render(graphics);
        blockElement(TFMGBlocks.COKE_OVEN.getDefaultState())
                .atLocal(0,-1,0)
                .scale(scale)
                .render(graphics);
        blockElement(TFMGBlocks.COKE_OVEN.getDefaultState())
                .atLocal(0,-1,1)
                .scale(scale)
                .render(graphics);
        blockElement(TFMGBlocks.COKE_OVEN.getDefaultState())
                .atLocal(0,-1,2)
                .scale(scale)
                .render(graphics);

        matrixStack.scale(scale, -scale, scale);
        matrixStack.translate(0, -1.8, 0);
        matrixStack.popPose();
    }
}
