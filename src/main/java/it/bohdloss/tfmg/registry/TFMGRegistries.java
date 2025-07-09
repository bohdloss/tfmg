package it.bohdloss.tfmg.registry;

import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.content.electricity.connection.cable_type.CableType;
import it.bohdloss.tfmg.content.machinery.vat.electrode_holder.electrode.Electrode;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.HashMap;
import java.util.Map;

import static it.bohdloss.tfmg.TFMG.REGISTRATE;

public class TFMGRegistries {

    public static final Map<ResourceLocation, CableType> registeredCableTypes = new HashMap<>();
    public static final Map<ResourceLocation, Electrode> registeredElectrodes = new HashMap<>();

    public static final ResourceKey<Registry<CableType>> CABLE_TYPE = REGISTRATE.makeRegistry("cable_type", key -> new RegistryBuilder<>(key).defaultKey(TFMG.asResource("empty")));
    public static final ResourceKey<Registry<Electrode>> ELECTRODE = REGISTRATE.makeRegistry("electrode", key -> new RegistryBuilder<>(key).defaultKey(TFMG.asResource("none")));

    public static void init() {
    }
}
