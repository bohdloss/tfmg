package it.bohdloss.tfmg.content.machinery.metallurgy.coke_oven;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import it.bohdloss.tfmg.registry.TFMGBlocks;
import it.bohdloss.tfmg.registry.TFMGPartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class CokeOvenRenderer extends SafeBlockEntityRenderer<CokeOvenBlockEntity> {
    public CokeOvenRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(CokeOvenBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        CokeOvenBlockEntity controller = (CokeOvenBlockEntity) be.getControllerBE();
        if(controller == null) {
            return;
        }

        BlockState blockState = be.getBlockState();
        if(be.getLevel().getBlockState(be.getBlockPos().relative(blockState.getValue(FACING))).is(TFMGBlocks.COKE_OVEN.get())) {
            return;
        }
        int lightInFront = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(be.getBlockState().getValue(FACING)));

        PartialModel right_door = TFMGPartialModels.COKE_OVEN_DOOR_RIGHT;
        PartialModel left_door = TFMGPartialModels.COKE_OVEN_DOOR_LEFT;

        switch (blockState.getValue(CokeOvenBlock.CONTROLLER_TYPE)){
            case TOP_ON -> {
                right_door = TFMGPartialModels.COKE_OVEN_DOOR_RIGHT_TOP;
                left_door = TFMGPartialModels.COKE_OVEN_DOOR_LEFT_TOP;
            }
            case BOTTOM_ON ->{
                right_door = TFMGPartialModels.COKE_OVEN_DOOR_RIGHT_BOTTOM;
                left_door = TFMGPartialModels.COKE_OVEN_DOOR_LEFT_BOTTOM;
            }
            case MIDDLE_ON ->{
                right_door = TFMGPartialModels.COKE_OVEN_DOOR_RIGHT_MIDDLE;
                left_door = TFMGPartialModels.COKE_OVEN_DOOR_LEFT_MIDDLE;
            }
            case CASUAL -> {}
        }

        CachedBuffers.partial(right_door, blockState)
                .light(lightInFront)
                .center()
                .rotateYDegrees(blockState.getValue(FACING).getAxis() == Direction.Axis.Z ? Math.abs(blockState.getValue(FACING).toYRot()-180) : blockState.getValue(FACING).toYRot())
                .translateZ(-0.5f)
                .translateX(-0.5f)
                .rotateYDegrees(controller.doorAngle.getValue())
                .translateZ(0.5f)
                .translateX(0.5f)
                .uncenter()
                .renderInto(ms, bufferSource.getBuffer(RenderType.cutoutMipped()));
        CachedBuffers.partial(left_door, blockState)
                .light(lightInFront)
                .center()
                .rotateYDegrees(blockState.getValue(FACING).getAxis() == Direction.Axis.Z ? Math.abs(blockState.getValue(FACING).toYRot()-180) : blockState.getValue(FACING).toYRot())
                .translateZ(-0.5f)
                .translateX(0.5f)
                .rotateYDegrees(-controller.doorAngle.getValue())
                .translateZ(0.5f)
                .translateX(-0.5f)
                .uncenter()
                .renderInto(ms, bufferSource.getBuffer(RenderType.cutoutMipped()));

//          ms.popPose();
    }
}
