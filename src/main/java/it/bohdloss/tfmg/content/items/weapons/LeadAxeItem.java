package it.bohdloss.tfmg.content.items.weapons;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.NotNull;

public class LeadAxeItem extends AxeItem {
    public LeadAxeItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        stack.hurtAndBreak(2, attacker, EquipmentSlot.MAINHAND);
        MobEffectInstance poison = target.getEffect(MobEffects.POISON);

        if(poison != null) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 100 + poison.getDuration()));
        }
        target.addEffect(new MobEffectInstance(MobEffects.POISON,100));
        return true;
    }
}
