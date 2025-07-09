package it.bohdloss.tfmg.content.items.weapons;

import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.base.spark.AbstractSparkRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class LithiumSparkRenderer extends AbstractSparkRenderer<LithiumSpark> {
    private static final ResourceLocation TEXTURE_LOCATION = TFMG.asResource("textures/entity/lithium_spark.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE_LOCATION);

    public LithiumSparkRenderer(EntityRendererProvider.Context context) {
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
