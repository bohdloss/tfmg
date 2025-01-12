package com.drmangotea.tfmg.content.electricity.lights;

import com.drmangotea.tfmg.registry.TFMGPartialModels;
import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class CircularLightRenderer extends LightBulbRenderer{
    public CircularLightRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public PartialModel getLightModel() {
        return TFMGPartialModels.CIRCULAR_LIGHT;
    }
}
