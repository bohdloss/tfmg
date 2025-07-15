package it.bohdloss.tfmg.mixin.fluid_handling;

import com.simibubi.create.content.fluids.transfer.FluidManipulationBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Set;

@Mixin(FluidManipulationBehaviour.class)
public interface FluidManipulationBehaviourAccessor {
    @Accessor int getRevalidateIn();
    @Accessor void setRevalidateIn(int revalidateIn);

    @Accessor BoundingBox getAffectedArea();
    @Accessor void setAffectedArea(BoundingBox boundingBox);

    @Accessor BlockPos getRootPos();
    @Accessor void setRootPos(BlockPos blockPos);

    @Accessor boolean getInfinite();
    @Accessor void setInfinite(boolean infinite);

    @Accessor List<FluidManipulationBehaviour.BlockPosEntry> getFrontier();
    @Accessor Set<BlockPos> getVisited();
}
