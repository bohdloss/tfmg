package com.drmangotea.tfmg.content.engines.upgrades;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.regular_engine.RegularEngineBlockEntity;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.drmangotea.tfmg.registry.TFMGPartialModels;
import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class TurboUpgradeData extends EngineUpgrade {


    LerpedFloat speed = LerpedFloat.linear();

    float angle;

    public TurboUpgradeData() {
    }

    @Override
    public Optional<? extends EngineUpgrade> createUpgrade() {
        return Optional.of(new TurboUpgradeData());
    }


    public PartialModel getModel() {
        return TFMGPartialModels.TURBO;
    }

    @Override
    public void tickUpgrade(AbstractEngineBlockEntity engine) {
        if (!engine.getLevel().isClientSide)
            return;




    }


    @Override
    public void render(AbstractEngineBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light) {

        if(!Minecraft.getInstance().isPaused()) {

            speed.chase(be.rpm / 100, 1 / 32f, LerpedFloat.Chaser.EXP);
            speed.tickChaser();
           // angle += speed.getValue(partialTicks) * 3 / 10f;
            angle+=be.rpm/100;
            angle %= 360;
        }
        BlockState state = be.getBlockState();
        Direction facing = state.getValue(FACING);
        boolean side = false;
        ms.pushPose();
        if (be instanceof RegularEngineBlockEntity blockEntity) {
            side = blockEntity.type.upgradesOnSide;
        }

        CachedBufferer.partial(getModel(), state)
                .centre()
                .rotateY(facing.toYRot())
                .translateX(side ? -4/16f : 0)
                .rotateZ(side ? 90 : 0)
                .unCentre()
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));

        CachedBufferer.partial(TFMGPartialModels.TURBO_PROPELLER, state)
                .centre()
                .rotateY(facing.toYRot())
                .translateX(side ? -4/16f : 0)
                .rotateZ(side ? 90 : 0)
                .rotateY(angle)
                .unCentre()
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));

        ms.popPose();
    }

    @Override
    public float getTorqueModifier(AbstractEngineBlockEntity engine) {
        return  1.2f;
    }

    @Override
    public Item getItem() {
        return TFMGItems.TURBO.asItem();
    }

    @Override
    public float getSpeedModifier(AbstractEngineBlockEntity engine) {
        return 1.3f;
    }

    @Override
    public float getEfficiencyModifier(AbstractEngineBlockEntity engine) {
        return 0.7f;
    }
}
