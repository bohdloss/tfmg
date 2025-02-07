package com.drmangotea.tfmg.content.machinery.misc.concrete_hose;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.pulley.AbstractPulleyRenderer;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyBlock;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyBlockEntity;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction.Axis;

public class ConcreteHoseRenderer extends AbstractPulleyRenderer<ConcreteHoseBlockEntity> {

	public ConcreteHoseRenderer(BlockEntityRendererProvider.Context context) {
		super(context, AllPartialModels.HOSE_HALF, AllPartialModels.HOSE_HALF_MAGNET);
	}

	@Override
	protected Axis getShaftAxis(ConcreteHoseBlockEntity be) {
		return be.getBlockState()
			.getValue(HosePulleyBlock.HORIZONTAL_FACING)
			.getClockWise()
			.getAxis();
	}

	@Override
	protected PartialModel getCoil() {
		return AllPartialModels.HOSE_COIL;
	}

	@Override
	protected SuperByteBuffer renderRope(ConcreteHoseBlockEntity be) {
		return CachedBufferer.partial(AllPartialModels.HOSE, be.getBlockState());
	}

	@Override
	protected SuperByteBuffer renderMagnet(ConcreteHoseBlockEntity be) {
		return CachedBufferer.partial(AllPartialModels.HOSE_MAGNET, be.getBlockState());
	}

	@Override
	protected float getOffset(ConcreteHoseBlockEntity be, float partialTicks) {
		return be.getInterpolatedOffset(partialTicks);
	}

	@Override
	protected boolean isRunning(ConcreteHoseBlockEntity be) {
		return true;
	}

}
