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
    public CastingBasinRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(CastingBasinBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (be.tank.isEmpty())
            return;
        BlockState blockState = be.getBlockState();
        FluidRenderer.renderFluidBox(be.tank.getFluid().getFluid(), be.tank.getFluid().getAmount(), 0.1f, 0.1f, 0.1f, 0.9f, be.fluidLevel.getValue(partialTicks) / 400, 0.9f, buffer, ms, light, false, false, be.tank.getFluid().getTag());
        if (be.flowTimer > 0) {

            Direction facing = blockState.getValue(FACING);
            if (facing == Direction.NORTH) {
                FluidRenderer.renderFluidBox(be.tank.getFluid().getFluid(), be.tank.getFluid().getAmount(), (7 / 16f), (8 / 16f), (8 / 16f), (9 / 16f), (9 / 16f), (14 / 16f), buffer, ms, light, false, false, be.tank.getFluid().getTag());
                FluidRenderer.renderFluidBox(be.tank.getFluid().getFluid(), be.tank.getFluid().getAmount(), (7 / 16f), (1 / 16f), (8 / 16f), (9 / 16f), (8 / 16f), (10 / 16f), buffer, ms, light, false, false, be.tank.getFluid().getTag());
            }
            if (facing == Direction.SOUTH) {
                FluidRenderer.renderFluidBox(be.tank.getFluid().getFluid(), be.tank.getFluid().getAmount(), (7 / 16f), (2 / 16f), (6 / 16f), (9 / 16f), (9 / 16f), (8 / 16f), buffer, ms, light, false, false, be.tank.getFluid().getTag());
                FluidRenderer.renderFluidBox(be.tank.getFluid().getFluid(), be.tank.getFluid().getAmount(), (7 / 16f), (8 / 16f), (2 / 16f), (9 / 16f), (9 / 16f), (6 / 16f), buffer, ms, light, false, false, be.tank.getFluid().getTag());
            }
            if (facing == Direction.WEST) {
                FluidRenderer.renderFluidBox(be.tank.getFluid().getFluid(), be.tank.getFluid().getAmount(), (8 / 16f), (2 / 16f), (7 / 16f), (10 / 16f), (9 / 16f), (9 / 16f), buffer, ms, light, false, false, be.tank.getFluid().getTag());
                FluidRenderer.renderFluidBox(be.tank.getFluid().getFluid(), be.tank.getFluid().getAmount(), (10 / 16f), (8 / 16f), (7 / 16f), (14 / 16f), (9 / 16f), (9 / 16f), buffer, ms, light, false, false, be.tank.getFluid().getTag());
            }
            if (facing == Direction.EAST) {
                FluidRenderer.renderFluidBox(be.tank.getFluid().getFluid(), be.tank.getFluid().getAmount(), (6 / 16f), (2 / 16f), (7 / 16f), (8 / 16f), (9 / 16f), (9 / 16f), buffer, ms, light, false, false, be.tank.getFluid().getTag());
                FluidRenderer.renderFluidBox(be.tank.getFluid().getFluid(), be.tank.getFluid().getAmount(), (2 / 16f), (8 / 16f), (7 / 16f), (6 / 16f), (9 / 16f), (9 / 16f), buffer, ms, light, false, false, be.tank.getFluid().getTag());
            }

        }

    }
}
