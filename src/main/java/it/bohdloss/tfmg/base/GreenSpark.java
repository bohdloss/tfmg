package it.bohdloss.tfmg.base;

import it.bohdloss.tfmg.content.items.weapons.explosives.thermite_grenades.GreenFireBlock;
import it.bohdloss.tfmg.registry.TFMGColoredFires;
import it.bohdloss.tfmg.registry.TFMGEntityTypes;
import it.bohdloss.tfmg.registry.TFMGItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

public class GreenSpark extends SparkBase {
    public GreenSpark(EntityType<? extends GreenSpark> entityType, Level level) {
        super(entityType, level);

    }

    public GreenSpark(Level level, LivingEntity livingEntity) {
        super(TFMGEntityTypes.GREEN_SPARK.get(), level, livingEntity);
    }

    public GreenSpark(Level level, double x, double y, double z) {
        super(TFMGEntityTypes.GREEN_SPARK.get(), level, x, y, z);
    }

    protected Item getDefaultItem() {
        return TFMGItems.ZINC_GRENADE.get();
    }

    protected ParticleOptions getParticle() {
        return ParticleTypes.FLAME;
    }

    protected BlockState createFire(Level level, BlockPos blockPos) {
        return TFMGColoredFires.GREEN_FIRE.get().getStateForPlacement(this.level(), blockPos);
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        EntityType.Builder<GreenSpark> entityBuilder = (EntityType.Builder<GreenSpark>) builder;
        return entityBuilder.sized(.25f, .25f);
    }
}
