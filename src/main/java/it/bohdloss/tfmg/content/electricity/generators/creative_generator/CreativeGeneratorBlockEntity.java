package it.bohdloss.tfmg.content.electricity.generators.creative_generator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.content.electricity.base.GeneratingElectricBlockEntity;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CreativeGeneratorBlockEntity extends GeneratingElectricBlockEntity {
    protected ScrollValueBehaviour outputVoltage;

    public CreativeGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        int max = 250;
        outputVoltage = new ScrollValueBehaviour(CreateLang.translateDirect("creative_generator.voltage_generation"),
                this, new CreativeGeneratorValueBox());
        outputVoltage.between(0, max);
        outputVoltage.value = 50;
        outputVoltage.withCallback(i -> updateGeneratedVoltage());
        behaviours.add(outputVoltage);
    }

    @Override
    public void initialize() {
        super.initialize();
        if (!hasElectricalSource() || getGeneratedVoltage() > getTheoreticalVoltage()) {
            updateGeneratedVoltage();
        }
    }

    @Override
    public float getGeneratedVoltage() {
        return outputVoltage.value * 10;
    }

    @Override
    public float calculateAmpsGenerated1Volt() {
        return 1;
    }

    static class CreativeGeneratorValueBox extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 16);
        }


        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            super.rotate(level,pos,state, ms);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction == Direction.UP;
        }

    }
}
