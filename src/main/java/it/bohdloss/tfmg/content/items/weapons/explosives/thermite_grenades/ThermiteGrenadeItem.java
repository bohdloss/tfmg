package it.bohdloss.tfmg.content.items.weapons.explosives.thermite_grenades;

import it.bohdloss.tfmg.base.SparkBase;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ThermiteGrenadeItem extends Item {
    public final ThermiteGrenade.ChemicalColor flameColor;
    public final Supplier<EntityType<? extends ThermiteGrenade>> entityType;
    public final Supplier<EntityType<? extends SparkBase>> sparkEntityType;

    public ThermiteGrenadeItem(Properties properties, ThermiteGrenade.ChemicalColor flameColor, Supplier<EntityType<? extends ThermiteGrenade>> entityType, Supplier<EntityType<? extends SparkBase>> sparkEntityType) {
        super(properties);
        this.flameColor = flameColor;
        this.entityType = entityType;
        this.sparkEntityType = sparkEntityType;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        player.getCooldowns().addCooldown(this, 60);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EGG_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!level.isClientSide) {
            ThermiteGrenade grenade = new ThermiteGrenade(level, player, flameColor, entityType.get(), sparkEntityType);
            grenade.setItem(itemstack);
            grenade.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.5F, 1.0F);
            level.addFreshEntity(grenade);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}
