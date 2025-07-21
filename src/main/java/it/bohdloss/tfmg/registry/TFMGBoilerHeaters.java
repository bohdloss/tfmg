package it.bohdloss.tfmg.registry;

import com.simibubi.create.api.boiler.BoilerHeater;

public class TFMGBoilerHeaters {
    public static void registerDefaults() {
        BoilerHeater.REGISTRY.register(TFMGBlocks.FIREBOX.get(), FIREBOX);
    }

    public static BoilerHeater FIREBOX = BoilerHeater.BLAZE_BURNER;
}
