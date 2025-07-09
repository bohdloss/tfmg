package it.bohdloss.tfmg.content.decoration.cogs;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import it.bohdloss.tfmg.registry.TFMGPartialModels;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

///
/// Taken and adapted from Create's source code
///
public class TFMGCogwheelVisual {
    public static BlockEntityVisual<BracketedKineticBlockEntity> singleVisualForAll(VisualizationContext context, BracketedKineticBlockEntity blockEntity, float partialTick) {
        Block block = blockEntity.getBlockState().getBlock();
        if (ICogWheel.isLargeCog(blockEntity.getBlockState())) {
            var model = Models.partial(TFMGPartialModels.cogwheel(true, block));
            return new TFMGCogwheelVisual.LargeCogVisual(context, blockEntity, partialTick, model);
        } else {
            var model = Models.partial(TFMGPartialModels.cogwheel(false, block));
            return new TFMGCogwheelVisual.CogVisual(context, blockEntity, partialTick, model);
        }
    }

    public static class CogVisual extends SingleAxisRotatingVisual<BracketedKineticBlockEntity> {
        public CogVisual(VisualizationContext context, BracketedKineticBlockEntity blockEntity, float partialTick, Model model) {
            super(context, blockEntity, partialTick, model);
        }
    }

    public static class LargeCogVisual extends SingleAxisRotatingVisual<BracketedKineticBlockEntity> {
        protected final RotatingInstance additionalShaft;

        private LargeCogVisual(VisualizationContext context, BracketedKineticBlockEntity blockEntity, float partialTick, Model model) {
            super(context, blockEntity, partialTick, model);

            Direction.Axis axis = KineticBlockEntityRenderer.getRotationAxisOf(blockEntity);

            additionalShaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.COGWHEEL_SHAFT))
                    .createInstance();

            additionalShaft.rotateToFace(axis)
                    .setup(blockEntity)
                    .setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, pos))
                    .setPosition(getVisualPosition())
                    .setChanged();
        }

        @Override
        public void update(float pt) {
            super.update(pt);
            additionalShaft.setup(blockEntity)
                    .setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(rotationAxis(), pos))
                    .setChanged();
        }

        @Override
        public void updateLight(float partialTick) {
            super.updateLight(partialTick);
            relight(additionalShaft);
        }

        @Override
        protected void _delete() {
            super._delete();
            additionalShaft.delete();
        }

        @Override
        public void collectCrumblingInstances(Consumer<Instance> consumer) {
            super.collectCrumblingInstances(consumer);
            consumer.accept(additionalShaft);
        }
    }
}
