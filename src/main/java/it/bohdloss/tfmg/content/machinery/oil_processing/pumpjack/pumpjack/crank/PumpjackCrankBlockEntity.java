package it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.crank;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import it.bohdloss.tfmg.content.machinery.misc.machine_input.MachineInputBlockEntity;
import it.bohdloss.tfmg.content.machinery.misc.machine_input.MachineInputPowered;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class PumpjackCrankBlockEntity extends MachineInputPowered {
    protected int ticks;

    public float angle;

    public Direction direction;
    public float heightModifier = 0;
    public float crankRadius = 0.7f;

    public PumpjackCrankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick() {
        super.tick();
        direction = this.getBlockState().getValue(FACING);
        setAngle();
        heightModifier = (float) (crankRadius * Math.sin(Math.toRadians(angle)));
    }

    public float getAngle() {
        return angle;
    }

    public float calcNextAngle() {
        float time;
        if (level.isClientSide) {
            time = AnimationTickHolder.getRenderTime(getLevel());
        } else {
            time = level.getBlockTicks().count();
        }
        float speed_amogus = Math.min(getMachineInputSpeed() / 6, (float) 10);
        if (speed_amogus != 0) {
            return (time * speed_amogus * 3 / 10f) % 360;
        } else {
            return 180;
        }
    }

    private void setAngle() {
        if(hasMachineInput()) {
            angle = calcNextAngle();
        } else {
            angle = 180;
        }
    }
}
