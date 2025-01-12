package com.drmangotea.tfmg.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class TFMGCommonConfig extends ConfigBase {

    public final MachineConfig machines = nested(0, MachineConfig::new, "Config options for TFMG's machinery");
    public final DepositConfig deposits = nested(1, DepositConfig::new, "Oil Deposit Config");

    @Override
    public String getName() {
        return "common";
    }


}
