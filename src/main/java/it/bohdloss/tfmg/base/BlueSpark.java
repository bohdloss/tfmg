package it.bohdloss.tfmg.base;

import it.bohdloss.tfmg.content.items.weapons.explosives.thermite_grenades.BlueFireBlock;
import it.bohdloss.tfmg.registry.TFMGColoredFires;
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

public class BlueSpark extends SparkBase {
    public BlueSpark(EntityType<? extends BlueSpark> entityType, Level level) {
        super(entityType, level);

    }

    public BlueSpark(Level level, LivingEntity livingEntity) {
        super(TFMGEntityTypes.BLUE_SPARK.get(), level, livingEntity);
    }

    public BlueSpark(Level level, double x, double y, double z) {
        super(TFMGEntityTypes.BLUE_SPARK.get(), level, x, y, z);
    }

    protected Item getDefaultItem() {
        return TFMGItems.COPPER_GRENADE.get();
    }

    protected ParticleOptions getParticle() {
        return ParticleTypes.FLAME;
    }

    protected BlockState createFire(Level level, BlockPos blockPos) {
        return TFMGColoredFires.BLUE_FIRE.get().getStateForPlacement(this.level(), blockPos);
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        EntityType.Builder<BlueSpark> entityBuilder = (EntityType.Builder<BlueSpark>) builder;
        return entityBuilder.sized(.25f, .25f);
    }
}
