package it.bohdloss.tfmg.base.spark;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public abstract class AbstractSparkRenderer<T extends Entity> extends EntityRenderer<T> {
    public AbstractSparkRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected int getBlockLightLevel(@NotNull T entity, @NotNull BlockPos pos) {
        return 15;
    }

    @Override
    public void render(@NotNull T pEntity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(provideRenderType());
        vertex(posestack$pose, vertexconsumer, matrix4f, matrix3f, packedLight, 0.0F, 0, 0, 1);
        vertex(posestack$pose, vertexconsumer, matrix4f, matrix3f, packedLight, 1.0F, 0, 1, 1);
        vertex(posestack$pose, vertexconsumer, matrix4f, matrix3f, packedLight, 1.0F, 1, 1, 0);
        vertex(posestack$pose, vertexconsumer, matrix4f, matrix3f, packedLight, 0.0F, 1, 0, 0);
        poseStack.popPose();
        super.render(pEntity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, int light, float x, int y, int u, int v) {
        vertexConsumer
                .addVertex(matrix4f, x - 0.5F, (float)y - 0.25F, 0.0F)
                .setColor(255, 255, 255, 255)
                .setUv((float)u, (float)v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return provideTextureLocation();
    }

    protected abstract @NotNull ResourceLocation provideTextureLocation();
    protected abstract @NotNull RenderType provideRenderType();
}
