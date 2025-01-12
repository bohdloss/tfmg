package com.drmangotea.tfmg.content.machinery.metallurgy.blast_furnace;

import com.drmangotea.tfmg.content.machinery.misc.vat_machines.electrode_holder.ElectrodeHolderBlockEntity;
import com.drmangotea.tfmg.registry.TFMGPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class BlastFurnaceRenderer extends SafeBlockEntityRenderer<BlastFurnaceOutputBlockEntity> {

    public BlastFurnaceRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(BlastFurnaceOutputBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {


        BlockState blockState = be.getBlockState();

        float coalCokeLevel = be.coalCokeHeight.getValue() / 64;

        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        Direction facing = blockState.getValue(FACING);
        if (coalCokeLevel > 0)
            if (be.getSize() >= 3) {

                CachedBufferer.partial(TFMGPartialModels.COAL_COKE_DUST_LAYER, blockState)
                        .light(LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().above().relative(facing.getOpposite())))
                        .centre()
                        .rotateY(facing.getAxis() == Direction.Axis.X ?  facing.getCounterClockWise().toYRot() : facing.getClockWise().toYRot())
                        .translateY(coalCokeLevel + 1.1)
                        .translateX(1)

                        .unCentre()
                        .renderInto(ms, vb);
            }
    }

}
