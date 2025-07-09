package it.bohdloss.tfmg.base;

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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

public abstract class SparkBase extends ThrowableProjectile {
    public SparkBase(EntityType<? extends SparkBase> entityType, Level level) {
        super(entityType, level);

    }
    public SparkBase(EntityType<? extends SparkBase> entityType, Level level, LivingEntity livingEntity) {
        super(entityType, livingEntity, level);
    }

    public SparkBase(EntityType<? extends SparkBase> entityType, Level level, double x, double y, double z) {
        super(entityType, x, y, z, level);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.02f;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {}

    public void tick(){
        super.tick();
        if (this.isInWaterOrRain()) {
            this.discard();
        }
        if(this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05D, -this.getDeltaMovement().y * 0.5D, this.random.nextGaussian() * 0.05D);
        }
    }

    public void handleEntityEvent(byte p_37402_) {
        if (p_37402_ == 3) {
            ParticleOptions particleoptions = this.getParticle();

            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.level().isClientSide) {
            return;
        }
        Entity entity = this.getOwner();

        if (!(entity instanceof Mob) && EventHooks.canEntityGrief(this.level(), this)) {
            BlockPos blockpos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
            if (this.level().isEmptyBlock(blockpos)) {
                this.level().setBlockAndUpdate(blockpos, createFire(level(), blockpos));
            }
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (this.level().isClientSide) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        int fireTicks = entity.getRemainingFireTicks();
        entity.setRemainingFireTicks(Math.max(fireTicks, 20 * 10));
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);

        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }

    // Override these

    abstract protected Item getDefaultItem();

    abstract protected ParticleOptions getParticle();

    abstract protected BlockState createFire(Level level, BlockPos blockPos);
}
