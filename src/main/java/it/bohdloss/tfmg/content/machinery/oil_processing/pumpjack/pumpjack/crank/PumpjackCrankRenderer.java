package it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.crank;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import it.bohdloss.tfmg.DebugStuff;
import it.bohdloss.tfmg.registry.TFMGPartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class PumpjackCrankRenderer implements BlockEntityRenderer<PumpjackCrankBlockEntity> {

    public PumpjackCrankRenderer(BlockEntityRendererProvider.Context context) {
        super();
    }

    @Override
    public void render(PumpjackCrankBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState blockState = blockEntity.getBlockState();
        VertexConsumer vb = bufferSource.getBuffer(RenderType.solid());
        poseStack.pushPose();
        var msr = TransformStack.of(poseStack);
        msr.translate(1 / 2f, 0.5, 1 / 2f);

        float angle = blockEntity.angle + (blockEntity.calcNextAngle() - blockEntity.angle) * partialTick * 1.1f;
        CachedBuffers.partialFacing(TFMGPartialModels.PUMPJACK_CRANK, blockState,blockState.getValue(FACING))
                .translate(-0.5, -0.5, -0.5)
                .center()
                .rotateDegrees(angle-90,blockEntity.getBlockState().getValue(FACING).getCounterClockWise().getAxis())
                .uncenter()
                .light(packedLight)
                .renderInto(poseStack,vb);
        poseStack.popPose();
    }

//    @Override
//    protected void renderSafe(PumpjackCrankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
//                              int light, int overlay) {
//
//    }

//    @Override
//    protected BlockState getRenderedBlockState(PumpjackCrankBlockEntity te) {
//        return shaft(getRotationAxisOf(te));
//    }
}
