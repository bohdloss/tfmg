package it.bohdloss.tfmg.content.items.weapons;

import it.bohdloss.tfmg.registry.TFMGEntityTypes;
import it.bohdloss.tfmg.registry.TFMGMobEffects;
import com.simibubi.create.content.trains.CubeParticleData;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class LithiumSpark extends ThrowableProjectile {
    public LithiumSpark(EntityType<? extends LithiumSpark> entityType, Level level) {
        super(entityType, level);

    }
    public LithiumSpark(Level level, LivingEntity livingEntity) {
        super(TFMGEntityTypes.LITHIUM_SPARK.get(), livingEntity, level);
    }

    public LithiumSpark(Level level, double x, double y, double z) {
        super(TFMGEntityTypes.LITHIUM_SPARK.get(), x, y, z, level);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.02;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
    }

    public void tick(){
        super.tick();
        //  if (this.isInWaterOrRain()) {
        //      this.discard();
        //  }
        if(this.level().isClientSide) {

            CubeParticleData data =
                    new CubeParticleData(100, 0, 0, .0125f + .0625f * random.nextFloat(), 30, false);
            level().addParticle(data, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05D, -this.getDeltaMovement().y * 0.5D, this.random.nextGaussian() * 0.05D);

        }
    }

    private ParticleOptions getParticle() {
        return ParticleTypes.FLAME;
    }

    public void burst(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
        Vec3 vec3 = (new Vec3(pX, pY, pZ)).normalize().add(this.random.triangle(0.0D, 0.0172275D * (double)pInaccuracy), 0, this.random.triangle(0.0D, 0.0172275D * (double)pInaccuracy)).scale((double)pVelocity);
        this.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
        this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    public void handleEntityEvent(byte state) {
        if (state == 3) {
            ParticleOptions particleoptions = this.getParticle();

            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);

    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (!this.level().isClientSide) {
            Entity entity = entityHitResult.getEntity();

            if(entity instanceof LivingEntity){
                ((LivingEntity)entity).addEffect(new MobEffectInstance(TFMGMobEffects.HELLFIRE,60));
            }
        }
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);

        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)3);

            //this.level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(), 2.0F, Explosion.BlockInteraction.NONE);
            this.discard();
        }
    }

    public static EntityType.Builder<?> build(EntityType.Builder<LithiumSpark> builder) {
        return builder.sized(.25f, .25f);
    }
}
