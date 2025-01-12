package com.drmangotea.tfmg.content.electricity.utilities.polarizer;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PolarizerRenderer extends SafeBlockEntityRenderer<PolarizerBlockEntity> {

	public PolarizerRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	protected void renderSafe(PolarizerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
							  int light, int overlay) {
		ItemStack heldItem = be.inventory.getItem(0);
		if (heldItem.isEmpty())
			return;
		BlockState blockState = be.getBlockState();
		ItemRenderer itemRenderer = Minecraft.getInstance()
			.getItemRenderer();
		boolean blockItem = itemRenderer.getModel(heldItem, null, null, 0)
			.isGui3d();

		ms.pushPose();
		TransformStack msr = TransformStack.cast(ms)
			.centre()
			.rotateY(blockState.getValue(HorizontalDirectionalBlock.FACING).getAxis()== Direction.Axis.X ? 90 : 0)
			.translate(0, 0.4, 0)
			.rotateX(90)
			.scale(blockItem ? .5f : .375f);

		itemRenderer.renderStatic(heldItem, ItemDisplayContext.FIXED, light, overlay, ms, buffer,be.getLevel(), 0);

		ms.popPose();
	}
}
