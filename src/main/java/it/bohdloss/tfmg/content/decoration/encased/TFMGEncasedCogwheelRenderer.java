package it.bohdloss.tfmg.content.decoration.encased;

import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import it.bohdloss.tfmg.registry.TFMGPartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class TFMGEncasedCogwheelRenderer extends EncasedCogRenderer {
    public static TFMGEncasedCogwheelRenderer large(BlockEntityRendererProvider.Context context) {
        return new TFMGEncasedCogwheelRenderer(context, true);
    }

    public static TFMGEncasedCogwheelRenderer small(BlockEntityRendererProvider.Context context) {
        return new TFMGEncasedCogwheelRenderer(context, false);
    }

    public TFMGEncasedCogwheelRenderer(BlockEntityRendererProvider.Context context, boolean large) {
        super(context, large);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(SimpleKineticBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacingVertical(Objects.requireNonNull(TFMGPartialModels.cogwheel(true, be.getBlockState().getBlock())), state,
                Direction.fromAxisAndDirection(state.getValue(EncasedCogwheelBlock.AXIS), Direction.AxisDirection.POSITIVE));
    }
}
