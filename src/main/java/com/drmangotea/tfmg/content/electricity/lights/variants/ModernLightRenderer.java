package com.drmangotea.tfmg.content.electricity.lights.variants;

import com.drmangotea.tfmg.content.electricity.lights.LightBulbRenderer;
import com.drmangotea.tfmg.registry.TFMGPartialModels;
import com.jozufozu.flywheel.core.PartialModel;
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
