package com.drmangotea.tfmg.content.machinery.misc.vat_machines.industrial_mixer;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.content.machinery.misc.winding_machine.WindingMachineBlockEntity;
import com.drmangotea.tfmg.registry.TFMGPartialModels;
import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class IndustrialMixerRenderer extends KineticBlockEntityRenderer<IndustrialMixerBlockEntity> {


    public IndustrialMixerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(IndustrialMixerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {

        super.renderSafe(be,partialTicks,ms,buffer,light,overlay);
        BlockState blockState = be.getBlockState();
        int height = be.vatHeight;

        if (be.mixerMode == IndustrialMixerBlockEntity.MixerMode.NONE)
            return;
        if(!Minecraft.getInstance().isPaused()) {
            be.angle += be.visualSpeed.getValue(partialTicks) * 3 / 10f;
            be.angle %= 360;
        }

        for (int i = 0; i < height; i++) {
            if (be.mixerMode == IndustrialMixerBlockEntity.MixerMode.CENTRIFUGE) {
                break;
            }
            PartialModel model = i == height - 1 ? be.vatSize > 1 ? TFMGPartialModels.MIXER : TFMGPartialModels.SMALL_MIXER : TFMGPartialModels.MIXER_SHAFT;
            float posX = be.vatSize == 2 ?(be.vatPos.getX()-be.getBlockPos().getX()+0.5f) : 0f;
            float posZ = be.vatSize == 2 ?(be.vatPos.getZ()-be.getBlockPos().getZ()+0.5f) : 0f;
            CachedBufferer.partial(model, blockState)
                    .light(LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().below()))
                    .centre()
                    .translate(posX,-i-1, posZ)
                    .rotateY(be.angle)
                    .unCentre()
                    .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
        }

    }
    @Override
    protected SuperByteBuffer getRotatedModel(IndustrialMixerBlockEntity be, BlockState state) {
        return CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, state, Direction.UP);
    }
}

