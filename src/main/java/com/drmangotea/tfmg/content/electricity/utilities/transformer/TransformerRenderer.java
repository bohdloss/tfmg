package com.drmangotea.tfmg.content.electricity.utilities.transformer;

import com.drmangotea.tfmg.registry.TFMGPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;
import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class TransformerRenderer extends SafeBlockEntityRenderer<TransformerBlockEntity> {

    public TransformerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(TransformerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        if (!be.primaryCoil.isEmpty()) {
            CachedBufferer.partial(TFMGPartialModels.TRANSFORMER_COIL, blockState)
                    .centre()

                    .rotateY(blockState.getValue(HORIZONTAL_FACING).getAxis() == Direction.Axis.Z ? Math.abs(blockState.getValue(FACING).toYRot() - 180) : blockState.getValue(FACING).toYRot())
                    .light(light)
                    .translateX(0.5)
                    .unCentre()
                    .renderInto(ms, bufferSource.getBuffer(RenderType.cutoutMipped()));
        }

        if (!be.secondaryCoil.isEmpty()) {
            CachedBufferer.partial(TFMGPartialModels.TRANSFORMER_COIL, blockState)
                    .light(light)
                    .centre()
                    .rotateY(blockState.getValue(HORIZONTAL_FACING).getAxis() == Direction.Axis.Z ? Math.abs(blockState.getValue(FACING).toYRot() - 180) : blockState.getValue(FACING).toYRot())
                    .unCentre()
                    .renderInto(ms, bufferSource.getBuffer(RenderType.cutoutMipped()));
        }
    }
}
