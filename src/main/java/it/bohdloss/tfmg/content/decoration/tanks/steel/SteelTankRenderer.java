package it.bohdloss.tfmg.content.decoration.tanks.steel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import it.bohdloss.tfmg.registry.TFMGPartialModels;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class SteelTankRenderer extends SafeBlockEntityRenderer<SteelTankBlockEntity> {
    public SteelTankRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(SteelTankBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        if (!te.isController())
            return;

        renderAsFluidTank(te, partialTicks, ms, buffer, light, overlay);
        if (!te.access().tfmg$getWindow()) {
            if (te.distillationData.isActive()) {
                renderAsDistillationTower(te, partialTicks, ms, buffer, light, overlay);
            }
        }
    }

    protected void renderAsDistillationTower(SteelTankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                                             int light, int overlay) {
        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());
        ms.pushPose();
        var msr = TransformStack.of(ms);
        msr.translate(be.getWidth() / 2f, 0.5, be.getWidth() / 2f);

        float dialPivot = 5.75f / 16;

        for (Direction d : Iterate.horizontalDirections) {
            ms.pushPose();
            CachedBuffers.partial(TFMGPartialModels.TOWER_GAUGE, blockState)
                    .rotateYDegrees(d.toYRot())
                    .uncenter()
                    .translate(be.getWidth() / 2f - 6 / 16f, 0, 0)
                    .light(light)
                    .renderInto(ms, vb);
            float dialPivotY = 6f / 16;
            float dialPivotZ = 8f / 16;
            CachedBuffers.partial(AllPartialModels.BOILER_GAUGE_DIAL, blockState)
                    .rotateYDegrees(d.toYRot())
                    .uncenter()
                    .translate(be.getWidth() / 2f - 6 / 16f, 0, 0)
                    .translate(0, dialPivotY, dialPivotZ)
                    .rotateXDegrees(-be.distillationData.visualGaugeRotation.getValue(partialTicks) + 90)
                    .translate(0, -dialPivotY, -dialPivotZ)
                    .light(light)
                    .renderInto(ms, vb);
            ms.popPose();
        }
        ms.popPose();
    }

    // Could not override fluid tank block entity renderer due to generics, so the following segment is directly copied
    // from create's own fluid tank rendering code

    protected void renderAsFluidTank(SteelTankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        if (!be.access().tfmg$getWindow()) {
            if (be.boiler.isActive())
                renderAsBoiler(be, partialTicks, ms, buffer, light, overlay);
            return;
        }

        LerpedFloat fluidLevel = be.getFluidLevel();
        if (fluidLevel == null)
            return;

        float capHeight = 1 / 4f;
        float tankHullWidth = 1 / 16f + 1 / 128f;
        float minPuddleHeight = 1 / 16f;
        float totalHeight = be.getHeight() - 2 * capHeight - minPuddleHeight;

        float level = fluidLevel.getValue(partialTicks);
        if (level < 1 / (512f * totalHeight))
            return;
        float clampedLevel = Mth.clamp(level * totalHeight, 0, totalHeight);

        FluidTank tank = be.access().tfmg$getTankInventory();
        FluidStack fluidStack = tank.getFluid();

        if (fluidStack.isEmpty())
            return;

        boolean top = fluidStack.getFluid()
                .getFluidType()
                .isLighterThanAir();

        float xMin = tankHullWidth;
        float xMax = xMin + be.getWidth() - 2 * tankHullWidth;
        float yMin = totalHeight + capHeight + minPuddleHeight - clampedLevel;
        float yMax = yMin + clampedLevel;

        if (top) {
            yMin += totalHeight - clampedLevel;
            yMax += totalHeight - clampedLevel;
        }

        float zMin = tankHullWidth;
        float zMax = zMin + be.getWidth() - 2 * tankHullWidth;

        ms.pushPose();
        ms.translate(0, clampedLevel - totalHeight, 0);
        NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, xMin, yMin, zMin, xMax, yMax, zMax, buffer,
                ms, light, false, true);
        ms.popPose();
    }

    protected void renderAsBoiler(SteelTankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                                  int light, int overlay) {
        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.cutout());
        ms.pushPose();
        var msr = TransformStack.of(ms);
        msr.translate(be.getWidth() / 2f, 0.5, be.getWidth() / 2f);

        float dialPivotY = 6f / 16;
        float dialPivotZ = 8f / 16;
        float progress = be.boiler.gauge.getValue(partialTicks);

        for (Direction d : Iterate.horizontalDirections) {
            if (be.boiler.occludedDirections[d.get2DDataValue()])
                continue;
            ms.pushPose();
            float yRot = -d.toYRot() - 90;
            CachedBuffers.partial(AllPartialModels.BOILER_GAUGE, blockState)
                    .rotateYDegrees(yRot)
                    .uncenter()
                    .translate(be.getWidth() / 2f - 6 / 16f, 0, 0)
                    .light(light)
                    .renderInto(ms, vb);
            CachedBuffers.partial(AllPartialModels.BOILER_GAUGE_DIAL, blockState)
                    .rotateYDegrees(yRot)
                    .uncenter()
                    .translate(be.getWidth() / 2f - 6 / 16f, 0, 0)
                    .translate(0, dialPivotY, dialPivotZ)
                    .rotateXDegrees(-145 * progress + 90)
                    .translate(0, -dialPivotY, -dialPivotZ)
                    .light(light)
                    .renderInto(ms, vb);
            ms.popPose();
        }

        ms.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(SteelTankBlockEntity be) {
        return be.isController();
    }
}
