package it.bohdloss.tfmg.base;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class HellFireEffect extends MobEffect {
    public HellFireEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if(livingEntity.isInWaterRainOrBubble()) {
            return true;
        }
        var fireTicks = livingEntity.getRemainingFireTicks();
        livingEntity.setRemainingFireTicks(Math.max(fireTicks, 20 /* 1 second */));
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
