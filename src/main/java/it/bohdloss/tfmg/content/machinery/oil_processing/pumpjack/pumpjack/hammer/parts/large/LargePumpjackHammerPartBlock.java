package it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.hammer.parts.large;


import com.simibubi.create.content.equipment.wrench.IWrenchable;
import it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.hammer.parts.PumpjackHammerPartBlock;
import it.bohdloss.tfmg.registry.TFMGShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class LargePumpjackHammerPartBlock extends PumpjackHammerPartBlock implements IWrenchable {
    public LargePumpjackHammerPartBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState pState, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return TFMGShapes.FULL;
    }
}
