package com.drmangotea.tfmg.content.machinery.metallurgy.casting_basin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class CastingBasinRenderer extends SafeBlockEntityRenderer<CastingBasinBlockEntity> {
    public CastingBasinRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(CastingBasinBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (be.tank.isEmpty())
            return;
        BlockState blockState = be.getBlockState();

        if (be.flowTimer > 0) {

            Direction facing = blockState.getValue(FACING);
            if (facing== Direction.NORTH) {
                FluidRenderer.renderFluidBox(be.tank.getFluid(), (7/16f), (8/16f), (8/16f), (9/16f), (9/16f), (14/16f), buffer, ms, light, false);
                FluidRenderer.renderFluidBox(be.tank.getFluid(), (7/16f), (1/16f), (8/16f), (9/16f), (8/16f), (10/16f), buffer, ms, light, false);
            }
            if (facing== Direction.SOUTH) {
                FluidRenderer.renderFluidBox(be.tank.getFluid(), (7/16f), (2/16f), (6/16f), (9/16f), (9/16f), (8/16f), buffer, ms, light, false);
                FluidRenderer.renderFluidBox(be.tank.getFluid(), (7/16f), (8/16f), (2/16f), (9/16f), (9/16f), (6/16f), buffer, ms, light, false);
            }
            if (facing== Direction.WEST) {
                FluidRenderer.renderFluidBox(be.tank.getFluid(), (8/16f), (2/16f), (7/16f), (10/16f), (9/16f), (9/16f), buffer, ms, light, false);
                FluidRenderer.renderFluidBox(be.tank.getFluid(), (10/16f), (8/16f), (7/16f), (14/16f), (9/16f), (9/16f), buffer, ms, light, false);
            }
            if (facing== Direction.EAST) {
                FluidRenderer.renderFluidBox(be.tank.getFluid(), (6/16f), (2/16f), (7/16f), (8/16f), (9/16f), (9/16f), buffer, ms, light, false);
                FluidRenderer.renderFluidBox(be.tank.getFluid(), (2/16f), (8/16f), (7/16f), (6/16f), (9/16f), (9/16f), buffer, ms, light, false);
            }

        }

    }
}
