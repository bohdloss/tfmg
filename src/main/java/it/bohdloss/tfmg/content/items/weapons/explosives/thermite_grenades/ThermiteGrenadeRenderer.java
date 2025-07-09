package it.bohdloss.tfmg.content.items.weapons.explosives.thermite_grenades;

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

public class ThermiteGrenadeRenderer extends EntityRenderer<ThermiteGrenade> {
    private final ItemRenderer itemRenderer;
    private final ThermiteGrenade.ChemicalColor chemicalColor;
    public static ThermiteGrenadeRenderer regular(EntityRendererProvider.Context context) {
        return new ThermiteGrenadeRenderer(context, ThermiteGrenade.ChemicalColor.BASE);
    }
    public static ThermiteGrenadeRenderer green(EntityRendererProvider.Context context) {
        return new ThermiteGrenadeRenderer(context, ThermiteGrenade.ChemicalColor.GREEN);
    }
    public static ThermiteGrenadeRenderer blue(EntityRendererProvider.Context p_i48440_1_) {
        return new ThermiteGrenadeRenderer(p_i48440_1_, ThermiteGrenade.ChemicalColor.BLUE);
    }
    public ThermiteGrenadeRenderer(EntityRendererProvider.Context context, ThermiteGrenade.ChemicalColor color) {
        super(context);
        this.chemicalColor = color;
        this.itemRenderer = context.getItemRenderer();
    }


    public void render(@NotNull ThermiteGrenade grenade, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource p_114660_, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        if (chemicalColor == ThermiteGrenade.ChemicalColor.GREEN) {
            this.itemRenderer.renderStatic(TFMGItems.ZINC_GRENADE.get().getDefaultInstance(), ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, p_114660_,grenade.level(), grenade.getId());
        } else if (chemicalColor == ThermiteGrenade.ChemicalColor.BLUE) {
            this.itemRenderer.renderStatic(TFMGItems.COPPER_GRENADE.get().getDefaultInstance(), ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, p_114660_,grenade.level(), grenade.getId());
        } else {
            this.itemRenderer.renderStatic(TFMGItems.THERMITE_GRENADE.get().getDefaultInstance(), ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, p_114660_,grenade.level(), grenade.getId());
        }

        poseStack.popPose();
        super.render(grenade, entityYaw, partialTick, poseStack, p_114660_, packedLight);
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull ThermiteGrenade grenade) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
