package com.drmangotea.tfmg.content.electricity.lights;



import com.drmangotea.tfmg.base.WallMountBlock;
import com.drmangotea.tfmg.registry.TFMGPartialModels;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.RenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class LightBulbRenderer extends SafeBlockEntityRenderer<LightBulbBlockEntity> {



    public LightBulbRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    protected void renderSafe(LightBulbBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if(getLightModel() ==null)
            return;
        BlockState blockState = be.getBlockState();
        ms.pushPose();
        float glow = be.glow.getValue(partialTicks);
        int color =  Math.min(100,(int) (glow/0.2f));
        if(be.glow.getValue()!=0) {

                CachedBufferer.partialFacing(getLightModel(), blockState, blockState.getValue(WallMountBlock.FACING))
                        .light((int) glow * 3 + 40)
                        .color(color, color, (int) (color * 0.8), 255)
                        .disableDiffuse()
                        .renderInto(ms, buffer.getBuffer(RenderTypes.getAdditive()));
        }
        ms.popPose();
    }

    public PartialModel getLightModel(){
        return TFMGPartialModels.LIGHT_BULB;
    }
}