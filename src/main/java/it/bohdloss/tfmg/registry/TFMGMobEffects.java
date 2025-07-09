package it.bohdloss.tfmg.registry;

import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.base.HellFireEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.awt.*;

public class TFMGMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, TFMG.MOD_ID);

    public static final DeferredHolder<MobEffect, HellFireEffect> HELLFIRE = MOB_EFFECTS.register("hellfire", () -> new HellFireEffect(MobEffectCategory.HARMFUL, new Color(150, 0, 0, 200).getRGB()));


    public static void register(IEventBus modEventBus){
        MOB_EFFECTS.register(modEventBus);
    }
}
