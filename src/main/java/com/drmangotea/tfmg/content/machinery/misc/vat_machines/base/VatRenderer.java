package com.drmangotea.tfmg.content.machinery.misc.vat_machines.base;


import com.drmangotea.tfmg.registry.TFMGPartialModels;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class VatRenderer extends SafeBlockEntityRenderer<VatBlockEntity> {

    public VatRenderer(BlockEntityRendererProvider.Context context) {}




    @Override
    protected void renderSafe(VatBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (!be.isController())
            return;


        LerpedFloat fluidLevel = be.getFluidLevel();
        if (fluidLevel == null)
            return;

        float capHeight = 1 / 4f;
        float tankHullWidth = 1 / 16f + 1 / 128f;
        float minPuddleHeight = 1 / 16f;
        float totalHeight = be.height - 2 * capHeight - minPuddleHeight;

        float level = fluidLevel.getValue(partialTicks);
        if (level < 1 / (512f * totalHeight))
            return;
        float clampedLevel = Mth.clamp(level * totalHeight, 0, totalHeight);

        //FluidTank tank = be.tankInventory;
        //FluidStack fluidStack = tank.getFluid();

        //if (fluidStack.isEmpty())
        //    return;
//
        //boolean top = fluidStack.getFluid()
        //        .getFluidType()
        //        .isLighterThanAir();

        float xMin = tankHullWidth;
        float xMax = xMin + be.width - 2 * tankHullWidth;
        float yMin = totalHeight + capHeight + minPuddleHeight - clampedLevel;
        float yMax = yMin + clampedLevel;

        //if (top) {
        //    yMin += totalHeight - clampedLevel;
        //    yMax += totalHeight - clampedLevel;
        //}

        float zMin = tankHullWidth;
        float zMax = zMin + be.width - 2 * tankHullWidth;

        ms.pushPose();
        ms.translate(0, clampedLevel - totalHeight, 0);
        //FluidRenderer.renderFluidBox(fluidStack, xMin, yMin, zMin, xMax, yMax, zMax, bufferSource, ms, light, false);
        ms.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(VatBlockEntity te) {
        return te.isController();
    }

}
