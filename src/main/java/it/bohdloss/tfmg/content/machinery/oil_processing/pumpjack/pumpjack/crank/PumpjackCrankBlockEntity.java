package it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.crank;

import it.bohdloss.tfmg.content.machinery.misc.machine_input.MachineInputPowered;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PumpjackCrankBlockEntity extends MachineInputPowered {
    public BlockPos pumpjackPosition;
    public float angle;
    public LerpedFloat lerpedAngle = LerpedFloat.angular();

    public PumpjackCrankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick() {
        super.tick();

        if(hasMachineInput()) {
            float time = level.getGameTime() % 1_728_000;
            float speed_amogus = Math.min(Math.abs(getMachineInputSpeed() / 6), (float) 10);
            if (speed_amogus != 0) {
                angle = (time * speed_amogus * 3 / 10f) % 360;
            } else {
                angle = 180;
            }
        } else {
            angle = 180;
        }
        lerpedAngle.chase(angle, 1, LerpedFloat.Chaser.EXP);
        lerpedAngle.tickChaser();
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        if(pumpjackPosition != null) {
            tag.put("Pumpjack", NbtUtils.writeBlockPos(pumpjackPosition));
        }

        if(!clientPacket) {
            tag.putFloat("Angle", angle);
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        pumpjackPosition = null;
        if(tag.contains("Pumpjack")) {
            pumpjackPosition = NbtUtils.readBlockPos(tag, "Pumpjack").get();
        }

        if(!clientPacket) {
            angle = tag.getFloat("Angle");
        }
    }
}
