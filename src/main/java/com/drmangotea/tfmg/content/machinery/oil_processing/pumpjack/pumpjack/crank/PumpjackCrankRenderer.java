package com.drmangotea.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.crank;





import com.drmangotea.tfmg.registry.TFMGPartialModels;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class PumpjackCrankRenderer extends KineticBlockEntityRenderer<PumpjackCrankBlockEntity> {

    public PumpjackCrankRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(PumpjackCrankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        ms.pushPose();
        TransformStack msr = TransformStack.cast(ms);
        msr.translate(1 / 2f, 0.5, 1 / 2f);
        CachedBufferer.partialFacing(TFMGPartialModels.PUMPJACK_CRANK, blockState,blockState.getValue(FACING))
                .translate(-0.5, -0.5, -0.5)
                .centre()
                .rotate(be.angle-90,be.getBlockState().getValue(FACING).getCounterClockWise().getAxis())
                .unCentre()
                .light(light)
                .renderInto(ms,vb);
        ms.popPose();
    }

    @Override
    protected BlockState getRenderedBlockState(PumpjackCrankBlockEntity te) {
        return shaft(getRotationAxisOf(te));
    }

}
