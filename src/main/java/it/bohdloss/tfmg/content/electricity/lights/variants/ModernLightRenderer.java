package it.bohdloss.tfmg.content.electricity.lights.variants;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import it.bohdloss.tfmg.content.electricity.lights.LightBulbRenderer;
import it.bohdloss.tfmg.registry.TFMGPartialModels;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ModernLightRenderer extends LightBulbRenderer {
    public ModernLightRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public PartialModel getLightModel() {
        return TFMGPartialModels.MODERN_LIGHT;
    }
}
