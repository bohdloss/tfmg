package it.bohdloss.tfmg.content.decoration.encased;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogVisual;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import it.bohdloss.tfmg.registry.TFMGPartialModels;

public class TFMGEncasedCogwheelVisual extends EncasedCogVisual {
    public static TFMGEncasedCogwheelVisual singleVisualForAll(VisualizationContext modelManager, KineticBlockEntity blockEntity, float partialTick) {
        PartialModel model = TFMGPartialModels.cogwheel(true, blockEntity.getBlockState().getBlock());
        return new TFMGEncasedCogwheelVisual(modelManager, blockEntity, false, partialTick, Models.partial(model));
    }

    public TFMGEncasedCogwheelVisual(VisualizationContext modelManager, KineticBlockEntity blockEntity, boolean large, float partialTick, Model model) {
        super(modelManager, blockEntity, large, partialTick, model);
    }
}
