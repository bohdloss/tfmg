package it.bohdloss.tfmg.base.spark;

import it.bohdloss.tfmg.base.Spark;
import it.bohdloss.tfmg.content.items.weapons.explosives.thermite_grenades.ThermiteGrenade;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SparkRenderer extends AbstractSparkRenderer<Spark> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/particle/lava.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE_LOCATION);

    public SparkRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation provideTextureLocation() {
        return TEXTURE_LOCATION;
    }

    @Override
    protected @NotNull RenderType provideRenderType() {
        return RENDER_TYPE;
    }
}
