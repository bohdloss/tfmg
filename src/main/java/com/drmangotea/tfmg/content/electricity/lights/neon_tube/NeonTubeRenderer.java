package com.drmangotea.tfmg.content.electricity.lights.neon_tube;


import com.drmangotea.tfmg.base.WallMountBlock;
import com.drmangotea.tfmg.content.electricity.lights.LightBulbBlockEntity;
import com.drmangotea.tfmg.registry.TFMGPartialModels;
import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.RenderTypes;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class NeonTubeRenderer extends SafeBlockEntityRenderer<NeonTubeBlockEntity> {


    public NeonTubeRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    protected void renderSafe(NeonTubeBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {


        BlockState blockState = be.getBlockState();
        ms.pushPose();
        float glow = be.glow.getValue(partialTicks);
        int color = Math.min(100, (int) (glow / 0.2f));
        if (be.glow.getValue() != 0) {

            SuperByteBuffer lightModel = CachedBufferer.partial(TFMGPartialModels.NEON_TUBE_LIGHT_CENTER, blockState)
                    .light((int) glow * 3 + 40)
                    .color(color, color, (int) (color * 0.8), 255)
                    .disableDiffuse();

            PipeBlock.PROPERTY_BY_DIRECTION.forEach((d, p) -> {
                if (blockState.getValue(p)) {

                    int xRotation = d.getAxis().isHorizontal() ? 90 : d == Direction.DOWN ? 180 : 0;
                    int yRotation = 0;

                    if(d==Direction.NORTH)
                        yRotation = 180;
                    if(d==Direction.WEST)
                        yRotation = 270;
                    if(d==Direction.EAST)
                        yRotation = 90;
                    SuperByteBuffer sideModel = CachedBufferer.partial(TFMGPartialModels.NEON_TUBE_LIGHT_SIDE, blockState)
                            .centre()
                            .rotateY(yRotation)
                            .rotateX(xRotation)
                            .unCentre()
                            .light((int) glow * 3 + 40)
                            .color(color, color, (int) (color * 0.8), 255)
                            .disableDiffuse();


                    if (be.color == DyeColor.WHITE) {
                        sideModel.color(color, color, (int) (color * 0.8), 255);
                    } else sideModel.color(be.color.getTextColor());
                    sideModel.renderInto(ms, buffer.getBuffer(RenderTypes.getAdditive()));

                }
            });

            if (be.color == DyeColor.WHITE) {
                lightModel.color(color, color, (int) (color * 0.8), 255);
            } else lightModel.color(be.color.getTextColor());

            lightModel.renderInto(ms, buffer.getBuffer(RenderTypes.getAdditive()));
        }
        ms.popPose();
    }


}