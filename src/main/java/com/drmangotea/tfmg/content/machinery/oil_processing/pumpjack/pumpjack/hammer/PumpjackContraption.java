package com.drmangotea.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.hammer;



import com.drmangotea.tfmg.base.TFMGContraptions;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ContraptionType;
import com.simibubi.create.content.contraptions.bearing.AnchoredLighter;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.contraptions.render.ContraptionLighter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

public class PumpjackContraption extends BearingContraption {

	public PumpjackContraption() {}
	public PumpjackContraption(Direction facing) {
		this.facing = facing;
	}

	@Override
	public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
		BlockPos offset = pos.above();
		if (!searchMovedStructure(world, offset, null))
			return false;
		startMoving(world);
		expandBoundsAroundAxis(facing.getAxis());
        return !blocks.isEmpty();
    }

	@Override
	public ContraptionType getType() {
		return TFMGContraptions.PUMPJACK_CONTRAPTION;
	}

	@Override
	protected boolean isAnchoringBlockAt(BlockPos pos) {
		return pos.equals(anchor.below());
	}

    @OnlyIn(Dist.CLIENT)
	@Override
	public ContraptionLighter<?> makeLighter() {
		return new AnchoredLighter(this);
	}
}
