package it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.crank;

import it.bohdloss.tfmg.content.machinery.misc.machine_input.MachineInputPowered;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PumpjackCrankBlockEntity extends MachineInputPowered {
    public BlockPos pumpjackPosition;
    public float angle;

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
        float time = level.getGameTime() % 1_728_000;
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

    public float getInterpolated(float partialTick) {
        float next = calcNextAngle();
        float beangle = angle;
        if(next < beangle) {
            next += 360;
        }
        float angle = beangle + (next - beangle) * partialTick * 1.1f;
        angle = angle % 360;
        return angle;
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
