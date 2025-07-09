package it.bohdloss.tfmg.content.decoration.cogs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import it.bohdloss.tfmg.registry.TFMGPartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

///
/// Taken and adapted from Create's source code
///
public class TFMGCogwheelRenderer extends BracketedKineticBlockEntityRenderer {

    public TFMGCogwheelRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(BracketedKineticBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        if (!ICogWheel.isLargeCog(be.getBlockState())) {
            super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
            return;
        }

        // Large cogs sometimes have to offset their teeth by 11.25 degrees in order to
        // mesh properly

        VertexConsumer vc = buffer.getBuffer(RenderType.solid());
        Direction.Axis axis = getRotationAxisOf(be);
        Direction facing = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        renderRotatingBuffer(be,
                CachedBuffers.partialFacingVertical(provideShaftlessLargeCogwheelModel(be), be.getBlockState(), facing),
                ms, vc, light);

        float angle = getAngleForLargeCogShaft(be, axis);
        SuperByteBuffer shaft =
                CachedBuffers.partialFacingVertical(AllPartialModels.COGWHEEL_SHAFT, be.getBlockState(), facing);
        kineticRotationTransform(shaft, be, axis, angle, light);
        shaft.renderInto(ms, vc);
    }

    protected @NotNull PartialModel provideShaftlessLargeCogwheelModel(@NotNull BracketedKineticBlockEntity be) {
        return Objects.requireNonNull(TFMGPartialModels.cogwheel(true, be.getBlockState().getBlock()));
    }
}
