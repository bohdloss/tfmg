package it.bohdloss.tfmg.config;

import net.createmod.catnip.config.ConfigBase;

public class TFMGCommonConfig extends ConfigBase {
    public final MachineConfig machines = nested(0, MachineConfig::new, "Config options for TFMG's machinery");
    public final DepositConfig worldgen = nested(1, DepositConfig::new, "Worldgen Settings");

    @Override
    public String getName() {
        return "common";
    }
}
