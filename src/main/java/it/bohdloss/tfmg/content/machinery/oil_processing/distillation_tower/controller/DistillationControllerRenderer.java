package it.bohdloss.tfmg.content.machinery.oil_processing.distillation_tower.controller;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import it.bohdloss.tfmg.registry.TFMGPartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import static it.bohdloss.tfmg.content.machinery.oil_processing.distillation_tower.controller.DistillationControllerBlock.FACING;

public class DistillationControllerRenderer extends SafeBlockEntityRenderer<DistillationControllerBlockEntity> {
    public DistillationControllerRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(DistillationControllerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        ms.pushPose();
        CachedBuffers.partial(TFMGPartialModels.DISTILLATION_CONTROLLER_DIAL,blockState)
                .center()
                .rotateYDegrees(blockState.getValue(FACING).getAxis() == Direction.Axis.Z ? Math.abs(blockState.getValue(FACING).toYRot() - 180) : blockState.getValue(FACING).toYRot())
                .translateY(0.01f)
                .rotateZDegrees(be.gaugeAngle.getValue(partialTicks))
                .translateX(0.09f)
                .uncenter()
                .renderInto(ms, vb);
        ms.popPose();
    }
}
