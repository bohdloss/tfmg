package com.drmangotea.tfmg.content.electricity.generators.creative_generator;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.content.electricity.base.ElectricBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CreativeGeneratorBlockEntity extends ElectricBlockEntity {

    protected ScrollValueBehaviour outputVoltage;
    public CreativeGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        int max = 250;
        outputVoltage = new ScrollValueBehaviour(Lang.translateDirect("creative_generator.voltage_generation"),
                this, new CreativeGeneratorValueBox());
        outputVoltage.between(0, max);
        outputVoltage.value = 50;
        outputVoltage.withCallback(i -> update(this));
        behaviours.add(outputVoltage);
    }


    public static void update(CreativeGeneratorBlockEntity be){
        be.updateNextTick();;
    }

    @Override
    public int voltageGeneration() {
        return outputVoltage.getValue()*10;
    }

    @Override
    public int powerGeneration() {
        return outputVoltage.getValue()*100;
    }

    class CreativeGeneratorValueBox extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 16);
        }

        @Override
        public Vec3 getLocalOffset(BlockState state) {
            return super.getLocalOffset(state);
        }

        @Override
        public void rotate(BlockState state, PoseStack ms) {
            super.rotate(state, ms);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction == Direction.UP;
        }

    }
}
