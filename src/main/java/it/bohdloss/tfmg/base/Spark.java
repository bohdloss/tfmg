package it.bohdloss.tfmg.base;

import it.bohdloss.tfmg.registry.TFMGEntityTypes;
import it.bohdloss.tfmg.registry.TFMGItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class Spark extends SparkBase {
    public Spark(EntityType<? extends Spark> entityType, Level level) {
        super(entityType, level);
    }

    public Spark(Level level, LivingEntity livingEntity) {
        super(TFMGEntityTypes.SPARK.get(), level, livingEntity);
    }

    public Spark(Level level, double x, double y, double z) {
        super(TFMGEntityTypes.SPARK.get(), level, x, y, z);
    }

    protected Item getDefaultItem() {
        return TFMGItems.THERMITE_GRENADE.get();
    }

    protected ParticleOptions getParticle() {
        return ParticleTypes.FLAME;
    }

    protected BlockState createFire(Level level, BlockPos blockPos) {
        return BaseFireBlock.getState(this.level(), blockPos);
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        EntityType.Builder<Spark> entityBuilder = (EntityType.Builder<Spark>) builder;
        return entityBuilder.sized(.25f, .25f);
    }
}
