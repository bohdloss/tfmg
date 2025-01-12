package com.drmangotea.tfmg.content.electricity.utilities.traffic_light;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class TrafficLightScrollSlot extends ValueBoxTransform {

	@Override
	public Vec3 getLocalOffset(BlockState state) {

		Direction direction = state.getValue(TrafficLightBlock.FACING);

		float x = 3.5f;
		float z = 8;

		if(direction ==Direction.WEST)
			x = 12.5f;

		if(direction == Direction.NORTH){
			x=8;
			z=12.5f;
		}
		if(direction == Direction.SOUTH){
			x=8;
			z=3.5f;
		}


		return VecHelper.voxelSpace(x, 8, z);
	}

	@Override
	public void rotate(BlockState state, PoseStack ms) {
		float yRot = AngleHelper.horizontalAngle(state.getValue(BlockStateProperties.HORIZONTAL_FACING)) + 180;
		TransformStack.cast(ms)
			.rotateY(yRot+180)
			.rotateX(0);
	}

	@Override
	public int getOverrideColor() {
		return 0x77FFAB;
	}
	
}
