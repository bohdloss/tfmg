package it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.crank;

import it.bohdloss.tfmg.content.machinery.misc.machine_input.MachineInputPowered;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PumpjackCrankBlockEntity extends MachineInputPowered {
    public BlockPos pumpjackPosition;

    public float angle;
    public Direction direction;


    public PumpjackCrankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick() {
        super.tick();
        setAngle();
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
