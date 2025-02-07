package com.drmangotea.tfmg.content.machinery.oil_processing.distillation_tower.controller;

import com.drmangotea.tfmg.content.electricity.utilities.polarizer.PolarizerBlockEntity;
import com.drmangotea.tfmg.registry.TFMGPartialModels;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class DistillationControllerRenderer extends SafeBlockEntityRenderer<DistillationControllerBlockEntity> {



	public DistillationControllerRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	protected void renderSafe(DistillationControllerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
							  int light, int overlay) {
		BlockState blockState = be.getBlockState();
		VertexConsumer vb = buffer.getBuffer(RenderType.solid());
		ms.pushPose();
		CachedBufferer.partial(TFMGPartialModels.DISTILLATION_CONTROLLER_DIAL,blockState)
				.centre()
				.rotateY(blockState.getValue(FACING).getAxis() == Direction.Axis.Z ? Math.abs(blockState.getValue(FACING).toYRot() - 180) : blockState.getValue(FACING).toYRot())
				.translateY(0.01)
				.rotateZ(be.angle.getValue(partialTicks))
				.translateX(0.09)
				.unCentre()
				.renderInto(ms, vb);


		ms.popPose();
	}
}
