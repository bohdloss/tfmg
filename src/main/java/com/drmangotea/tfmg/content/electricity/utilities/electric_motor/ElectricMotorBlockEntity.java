package com.drmangotea.tfmg.content.electricity.utilities.electric_motor;

import com.drmangotea.tfmg.config.MachineConfig;
import com.drmangotea.tfmg.config.TFMGConfigs;
import com.drmangotea.tfmg.content.electricity.base.KineticElectricBlockEntity;
import com.electronwill.nightconfig.core.Config;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class ElectricMotorBlockEntity extends KineticElectricBlockEntity {

    public static final int DEFAULT_SPEED = 64;
    public static final int MAX_SPEED = 256;
    protected ScrollValueBehaviour generatedSpeed;

    public ElectricMotorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        int max = MAX_SPEED;
        generatedSpeed = new KineticScrollValueBehaviour(Lang.translateDirect("kinetics.creative_motor.rotation_speed"),
                this, new MotorValueBox());
        generatedSpeed.between(-max, max);
        generatedSpeed.value = DEFAULT_SPEED;
        generatedSpeed.withCallback(i -> this.updateGeneratedRotation());
        generatedSpeed.withCallback(i ->this.updateNextTick());
        behaviours.add(generatedSpeed);
    }


    @Override
    public void updateNetwork() {
        super.updateNetwork();
        updateGeneratedRotation();
    }

    @Override
    public void onNetworkChanged(int oldVoltage, int oldPower) {
        updateGeneratedRotation();
    }

    @Override
    public float getGeneratedSpeed() {

        MachineConfig machineConfig = TFMGConfigs.common().machines;
        if(getPowerUsage() >= machineConfig.electricMotorMinimumPower.get()&&getData().getVoltage()>=machineConfig.electricMotorMinimumVoltage.get()&&getPowerPercentage()>0){

            return Math.min(generatedSpeed.getValue(),getData().getVoltage())*((float) getPowerPercentage() /100);

        }

        return 0;
    }

    @Override
    public float resistance() {
        return (int) (13 * Math.min(generatedSpeed.getValue(),getData().getVoltage())*TFMGConfigs.common().machines.electricMotorPowerUsageModifier.get());
    }

    class MotorValueBox extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 12.5);
        }

        @Override
        public Vec3 getLocalOffset(BlockState state) {
            Direction facing = state.getValue(FACING);
            return super.getLocalOffset(state).add(Vec3.atLowerCornerOf(facing.getNormal())
                    .scale(-1 / 16f));
        }

        @Override
        public void rotate(BlockState state, PoseStack ms) {
            super.rotate(state, ms);
            Direction facing = state.getValue(FACING);
            if (facing.getAxis() == Direction.Axis.Y)
                return;
            if (getSide() != Direction.UP)
                return;
            TransformStack.cast(ms)
                    .rotateZ(-AngleHelper.horizontalAngle(facing) + 180);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            Direction facing = state.getValue(FACING);
            if (facing.getAxis() != Direction.Axis.Y && direction == Direction.DOWN||direction == Direction.UP)
                return false;
            return direction.getAxis() != facing.getAxis();
        }

    }
}
