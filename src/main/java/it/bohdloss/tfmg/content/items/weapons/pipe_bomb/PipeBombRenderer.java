package it.bohdloss.tfmg.content.items.weapons.pipe_bomb;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.bohdloss.tfmg.registry.TFMGItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

public class PipeBombRenderer extends EntityRenderer<PipeBomb> {
    private final ItemRenderer itemRenderer;

    public PipeBombRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    public void render(PipeBomb grenade, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource source, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));


        this.itemRenderer.renderStatic(TFMGItems.PIPE_BOMB.get().getDefaultInstance(), ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, source,grenade.level(), grenade.getId());

        poseStack.popPose();
        super.render(grenade, entityYaw, partialTick, poseStack, source, packedLight);
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull PipeBomb entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
