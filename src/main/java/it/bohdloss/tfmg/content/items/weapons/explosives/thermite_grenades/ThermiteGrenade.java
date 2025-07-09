package it.bohdloss.tfmg.content.items.weapons.explosives.thermite_grenades;

import it.bohdloss.tfmg.base.SparkBase;
import it.bohdloss.tfmg.registry.TFMGEntityTypes;
import it.bohdloss.tfmg.registry.TFMGItems;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ThermiteGrenade extends ThrowableItemProjectile {
    public final ChemicalColor flameColor;
    public final Supplier<EntityType<? extends SparkBase>> sparkEntityType;

    public ThermiteGrenade(EntityType<? extends ThermiteGrenade> entityType, Level level) {
        super(entityType, level);
        this.flameColor = ChemicalColor.BLUE;
        this.sparkEntityType = TFMGEntityTypes.BLUE_SPARK::get;
    }

    public ThermiteGrenade(Level level, LivingEntity shooter, ChemicalColor color, EntityType grenade, Supplier<EntityType<? extends SparkBase>> sparkEntityType) {
        super(grenade, shooter, level);
        this.flameColor = color;
        this.sparkEntityType = sparkEntityType;
    }

    public ThermiteGrenade(Level level, double x, double y, double z) {
        super(TFMGEntityTypes.THERMITE_GRENADE.get(), x, y, z, level);
        this.flameColor = ChemicalColor.BLUE;
        this.sparkEntityType = TFMGEntityTypes.BLUE_SPARK::get;
    }

    protected @NotNull Item getDefaultItem() {
        return TFMGItems.THERMITE_GRENADE.get();
    }

    protected @Nullable ParticleOptions getParticle() {
        return ParticleTypes.FLAME;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleOptions particleoptions = this.getParticle();
            if(particleoptions != null) {
                for (int i = 0; i < 8; ++i) {
                    this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);

        if (this.level().isClientSide) {
            return;
        }
        this.level().broadcastEntityEvent(this, (byte) 3);

        for (int i=0; i<20;i++){
            float x = level().getRandom().nextFloat() * 360f;
            float y = level().getRandom().nextFloat() * 360f;
            float z = level().getRandom().nextFloat() * 360f;


            SparkBase spark = sparkEntityType.get().create(level());
            spark.moveTo(this.getX(), this.getY()+1, this.getZ());
            spark.shootFromRotation( this,x,y,z,0.2f,1);
            this.level().addFreshEntity(spark);
        }


        this.level().explode(this, this.getX(), this.getY(0.0625D), this.getZ(), 2.0F, Level.ExplosionInteraction.NONE);
        this.discard();

    }

    @SuppressWarnings("unchecked")
    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        EntityType.Builder<ThermiteGrenade> entityBuilder = (EntityType.Builder<ThermiteGrenade>) builder;
        return entityBuilder.sized(.25f, .25f);
    }

    public enum ChemicalColor {
        BASE,
        GREEN,
        BLUE
    }
}
