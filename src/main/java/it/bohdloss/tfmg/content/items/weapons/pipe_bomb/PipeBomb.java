package it.bohdloss.tfmg.content.items.weapons.pipe_bomb;

import it.bohdloss.tfmg.registry.TFMGEntityTypes;
import it.bohdloss.tfmg.registry.TFMGItems;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class PipeBomb extends ThrowableItemProjectile {
    public PipeBomb(EntityType<? extends PipeBomb> entityType, Level level) {
        super(entityType,level);
    }

    public PipeBomb(Level p_37399_, LivingEntity p_37400, EntityType bomb) {
        super(bomb, p_37400, p_37399_);
    }

    public PipeBomb(Level p_37394_, double p_37395_, double p_37396_, double p_37397_) {
        super(TFMGEntityTypes.PIPE_BOMB.get(), p_37395_, p_37396_, p_37397_, p_37394_);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return TFMGItems.PIPE_BOMB.get();
    }

    private ParticleOptions getParticle() {
        return ParticleTypes.FLAME;
    }

    @Override
    public void handleEntityEvent(byte p_37402_) {
        if (p_37402_ == 3) {
            ParticleOptions particleoptions = this.getParticle();

            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }

    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);

        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);

            this.level().explode(this, this.getX(), this.getY(0.0625D), this.getZ(), 2.0F, Level.ExplosionInteraction.NONE);
            this.discard();
        }

    }

    @SuppressWarnings("unchecked")
    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        EntityType.Builder<PipeBomb> entityBuilder = (EntityType.Builder<PipeBomb>) builder;
        return entityBuilder.sized(.25f, .25f);
    }
}
