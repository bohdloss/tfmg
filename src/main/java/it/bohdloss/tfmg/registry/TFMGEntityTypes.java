package it.bohdloss.tfmg.registry;

import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.base.BlueSpark;
import it.bohdloss.tfmg.base.GreenSpark;
import it.bohdloss.tfmg.base.Spark;
import it.bohdloss.tfmg.base.spark.BlueSparkRenderer;
import it.bohdloss.tfmg.base.spark.GreenSparkRenderer;
import it.bohdloss.tfmg.base.spark.SparkRenderer;
import it.bohdloss.tfmg.content.items.weapons.LithiumSpark;
import it.bohdloss.tfmg.content.items.weapons.LithiumSparkRenderer;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import it.bohdloss.tfmg.content.items.weapons.explosives.thermite_grenades.ThermiteGrenade;
import it.bohdloss.tfmg.content.items.weapons.explosives.thermite_grenades.ThermiteGrenadeRenderer;
import it.bohdloss.tfmg.content.items.weapons.pipe_bomb.PipeBomb;
import it.bohdloss.tfmg.content.items.weapons.pipe_bomb.PipeBombRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class TFMGEntityTypes {
    public static final EntityEntry<PipeBomb> PIPE_BOMB =
            register("pipe_bomb", PipeBomb::new, () -> PipeBombRenderer::new,
                    MobCategory.MISC, 4, 20, true, true, PipeBomb::build).register();

//    public static final EntityEntry<NapalmPotato> NAPALM_POTATO =
//            register("napalm_potato", NapalmPotato::new, () -> NapalmPotatoRenderer::new,
//                    MobCategory.MISC, 4, 20, true, true, NapalmPotato::build).register();
//
    public static final EntityEntry<ThermiteGrenade> THERMITE_GRENADE =
            register("thermite_grenade", ThermiteGrenade::new, () -> ThermiteGrenadeRenderer::regular,
                    MobCategory.MISC, 4, 20, true, true, ThermiteGrenade::build).register();
    public static final EntityEntry<ThermiteGrenade> ZINC_GRENADE =
            register("zinc_grenade", ThermiteGrenade::new, () -> ThermiteGrenadeRenderer::green,
                    MobCategory.MISC, 4, 20, true, true, ThermiteGrenade::build).register();
    public static final EntityEntry<ThermiteGrenade> COPPER_GRENADE =
            register("copper_grenade", ThermiteGrenade::new, () -> ThermiteGrenadeRenderer::blue,
                    MobCategory.MISC, 4, 20, true, true, ThermiteGrenade::build).register();
//    public static final EntityEntry<NapalmBombEntity> NAPALM_BOMB =
//            register("napalm_bomb_entity", NapalmBombEntity::new, () -> NapalmBombRenderer::new,
//                    MobCategory.MISC, 4, 20, true, true, NapalmBombEntity::build).register();

    public static final EntityEntry<Spark> SPARK =
            register("spark", Spark::new, () -> SparkRenderer::new,
                    MobCategory.MISC, 4, 20, true, true, Spark::build).register();
    public static final EntityEntry<GreenSpark> GREEN_SPARK =
            register("green_spark", GreenSpark::new, () -> GreenSparkRenderer::new,
                    MobCategory.MISC, 4, 20, true, true, GreenSpark::build).register();
    public static final EntityEntry<BlueSpark> BLUE_SPARK =
            register("blue_spark", BlueSpark::new, () -> BlueSparkRenderer::new,
                    MobCategory.MISC, 4, 20, true, true, BlueSpark::build).register();

    public static final EntityEntry<LithiumSpark> LITHIUM_SPARK =
            register("lithium_spark", LithiumSpark::new, () -> LithiumSparkRenderer::new,
                    MobCategory.MISC, 80, 20, true, true, LithiumSpark::build).register();

    private static <T extends Entity> CreateEntityBuilder<T, ?> register(String name, EntityType.EntityFactory<T> factory,
                                                                         NonNullSupplier<NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? super T>>> renderer,
                                                                         MobCategory group, int range, int updateFrequency, boolean sendVelocity, boolean immuneToFire,
                                                                         NonNullConsumer<EntityType.Builder<T>> propertyBuilder) {
        String id = CreateLang.asId(name);
        return (CreateEntityBuilder<T, ?>) TFMG.REGISTRATE
                .entity(id, factory, group)
                .properties(b -> b.setTrackingRange(range)
                        .setUpdateInterval(updateFrequency)
                        .setShouldReceiveVelocityUpdates(sendVelocity))
                .properties(propertyBuilder)
                .properties(b -> {
                    if (immuneToFire)
                        b.fireImmune();
                })
                .renderer(renderer);
    }



    public static void init() {}
}
