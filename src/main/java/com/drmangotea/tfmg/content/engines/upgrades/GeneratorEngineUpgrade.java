package com.drmangotea.tfmg.content.engines.upgrades;

import com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.regular_engine.RegularEngineBlockEntity;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import com.drmangotea.tfmg.registry.TFMGPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class GeneratorEngineUpgrade extends EngineUpgrade {
    @Override
    public Optional<? extends EngineUpgrade> createUpgrade() {
        return Optional.of(new GeneratorEngineUpgrade());
    }

    @Override
    public void render(AbstractEngineBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light) {




        BlockState state = be.getBlockState();
        Direction facing = state.getValue(FACING);
        boolean side = false;
        ms.pushPose();
        if (be instanceof RegularEngineBlockEntity blockEntity) {
            side = blockEntity.type.upgradesOnSide;
        }

        CachedBufferer.partial(TFMGPartialModels.ENGINE_GENERATOR, state)
                .centre()
                .translateY(side ? -2/16f :0)
                .rotateY(facing.toYRot())
                .rotateZ(side ? 90 : 0)
                .translateY(side ? 4 / 16f : 0)
                .unCentre()
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));

        ms.popPose();

    }

    @Override
    public Item getItem() {
        return TFMGBlocks.GENERATOR.asItem();
    }

    @Override
    public float getTorqueModifier(AbstractEngineBlockEntity engine) {
        return 0.7f;
    }
}
