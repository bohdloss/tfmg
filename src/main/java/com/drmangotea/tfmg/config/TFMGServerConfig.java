package com.drmangotea.tfmg.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class TFMGServerConfig extends ConfigBase {



	public final StressConfig stressValues = nested(0, StressConfig::new, "Fine tune the kinetic stats of individual components");

	@Override
	public String getName() {
		return "server";
	}
}
