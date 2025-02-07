package com.drmangotea.tfmg.content.electricity.lights.variants;

import com.drmangotea.tfmg.content.electricity.lights.LightBulbRenderer;
import com.drmangotea.tfmg.registry.TFMGPartialModels;
import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class AluminumLampRenderer extends LightBulbRenderer {
    public AluminumLampRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public PartialModel getLightModel() {
        return TFMGPartialModels.ALUMINUM_LAMP;
    }
}
