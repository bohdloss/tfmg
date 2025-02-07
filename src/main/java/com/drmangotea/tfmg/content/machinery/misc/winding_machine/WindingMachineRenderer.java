package com.drmangotea.tfmg.content.machinery.misc.winding_machine;

import com.drmangotea.tfmg.registry.TFMGPartialModels;
import com.drmangotea.tfmg.registry.TFMGTags;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;
import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class WindingMachineRenderer extends KineticBlockEntityRenderer<WindingMachineBlockEntity> {
    public WindingMachineRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
    @Override
    protected void renderSafe(WindingMachineBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {

        BlockState blockState = be.getBlockState();
        ItemRenderer itemRenderer = Minecraft.getInstance()
                .getItemRenderer();


        VertexConsumer vb = bufferSource.getBuffer(RenderType.solid());
        if(!Minecraft.getInstance().isPaused()) {
            be.angle += be.spoolSpeed.getValue(partialTicks) * 3 / 10f;
            be.angle %= 360;
        }
        if(!be.spool.isEmpty()) {
            CachedBufferer.partial(TFMGPartialModels.SPOOL, blockState)
                    .centre()
                    .rotateY(blockState.getValue(HORIZONTAL_FACING).getAxis() == Direction.Axis.Z ? Math.abs(blockState.getValue(FACING).toYRot() - 180) : blockState.getValue(FACING).toYRot())
                    .translateZ(-0.4)
                    .translateY(0.4)
                    .rotateX(be.angle)
                    .unCentre()
                    .renderInto(ms, bufferSource.getBuffer(RenderType.cutoutMipped()));
            if(((SpoolItem)be.spool.getItem()).model !=null){
                CachedBufferer.partial(((SpoolItem)be.spool.getItem()).model, blockState)
                        .centre()
                        .rotateY(blockState.getValue(HORIZONTAL_FACING).getAxis() == Direction.Axis.Z ? Math.abs(blockState.getValue(FACING).toYRot() - 180) : blockState.getValue(FACING).toYRot())
                        .translateZ(-0.4)
                        .translateY(0.4)
                        .rotateX(be.angle)
                        .unCentre()
                        .renderInto(ms, vb);

                if(!be.inventory.isEmpty()){

                    CachedBufferer.partial(be.getSpeed()!=0 ? TFMGPartialModels.CONNNECTING_WIRE_ANIMATED : TFMGPartialModels.CONNNECTING_WIRE, blockState)
                            .centre()
                            .rotateY(blockState.getValue(HORIZONTAL_FACING).getAxis() == Direction.Axis.Z ? Math.abs(blockState.getValue(FACING).toYRot() - 180) : blockState.getValue(FACING).toYRot())
                            .translateY(0.4f)
                            .translateZ(0.1f)
                            .color(be.spool.getBarColor())
                            .rotateX(12)
                            .unCentre()
                            .renderInto(ms, vb);
                }
            }
        }
        if(!be.inventory.isEmpty()){

            ItemStack item = be.inventory.getItem(0);

            BakedModel bakedModel = itemRenderer.getModel(item, null, null, 0);
            boolean blockItem = bakedModel.isGui3d();


            ms.pushPose();
            TransformStack.cast(ms)
                    .centre()
                    .rotateY(blockState.getValue(HORIZONTAL_FACING).getAxis() == Direction.Axis.Z ? Math.abs(blockState.getValue(FACING).toYRot() - 180) : blockState.getValue(FACING).toYRot())
                    .translateZ(0.4)
                    .translateY(0.33)
                    .rotateX(be.angle)
                    .rotateZ(item.is(TFMGTags.TFMGItemTags.RODS.tag) ? 45 : 0   )
                    .rotateZ(blockItem ? 90 : 0)
                    .scale(blockItem ? .5f : .375f);
            ;


            itemRenderer.render(item, ItemDisplayContext.FIXED, false, ms, bufferSource, light, overlay, bakedModel);
            ms.popPose();
        }


        super.renderSafe(be, partialTicks, ms ,bufferSource,light,overlay);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(WindingMachineBlockEntity be, BlockState state) {
        return CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, state, state
                .getValue(FACING).getCounterClockWise());
    }


}
