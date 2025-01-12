package com.drmangotea.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.base;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.config.TFMGConfigs;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DepositSavedData extends SavedData {

    List<FluidReservoir> reservoirs = new ArrayList<>();

    //
    public void removeDeposit(long pos) {
        for (FluidReservoir reservoir : reservoirs) {
            if (reservoir.deposits.contains(pos)) {
                reservoir.deposits.remove(pos);
                if(reservoir.deposits.isEmpty())
                    reservoirs.remove(reservoir);
                return;
            }

        }
    }

    public void addDeposit(Level level, long pos) {
        if (!level.getBlockState(BlockPos.of(pos)).is(TFMGBlocks.OIL_DEPOSIT.get()))
            return;

        if (containsDeposit(pos))
            return;

        for (FluidReservoir reservoir : reservoirs) {
            if (reservoir.id != pos) {
                if (isReservoirNearby(BlockPos.of(pos))) {
                    return;
                }
            }
        }
        TFMG.LOGGER.debug("ADDED NEW RESERVOIR");

        RandomSource randomSource = level.random;
        FluidReservoir reservoir = new FluidReservoir(pos);
        reservoir.oilReserves = randomSource.nextInt(1000, TFMGConfigs.common().deposits.depositMaxReserves.get());
        if(!reservoir.deposits.isEmpty()) {
            reservoirs.add(reservoir);
            setDirty();
        }
    }

    public void removeData() {
        TFMG.LOGGER.debug("REMOVED DATA");
        reservoirs = new ArrayList<>();
        setDirty();
    }

    public boolean isReservoirNearby(BlockPos pos) {

        for (int x = -32; x < 32; x++) {
            for (int z = -32; z < 32; z++) {
                BlockPos checkedPos = pos.offset(x, 0, z);
                for (int i = 0; i < reservoirs.size(); i++) {
                    FluidReservoir reservoir = reservoirs.get(i);
                    if (reservoir.id == checkedPos.asLong()) {
                        TFMG.LOGGER.debug("INCREASED RESERVOIR SIZE");
                        reservoirs.get(i).deposits.add(pos.asLong());
                        return true;
                    }
                }

            }
        }

        return false;
    }

    public boolean containsDeposit(long pos) {
        for (FluidReservoir reservoir : reservoirs) {
            if (reservoir.deposits.contains(pos))
                return true;
        }
        return false;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        TFMG.LOGGER.debug("SAVE");
        compound.putInt("reservoirCount", reservoirs.size());
        for (int i = 0; i < reservoirs.size(); i++) {
            FluidReservoir reservoir = reservoirs.get(i);
            CompoundTag reservoirNBT = new CompoundTag();
            reservoirNBT.putLong("Id", reservoir.id);
            reservoirNBT.putInt("Reserves", reservoir.oilReserves);
            reservoirNBT.putLongArray("Deposits", reservoir.deposits);
            //
            compound.put("FluidReservoir" + i, reservoirNBT);
        }
        return compound;
    }

    public FluidReservoir getReservoirFor(long pos) {

        for (FluidReservoir reservoir : reservoirs) {
            if (reservoir.deposits.contains(pos))
                return reservoir;
        }
        return null;
    }

    private static DepositSavedData load(CompoundTag compound) {
        DepositSavedData data = new DepositSavedData();

        for (int i = 0; i < compound.getInt("reservoirCount"); i++) {
            CompoundTag reservoirNBT = compound.getCompound("FluidReservoir" + i);

            FluidReservoir reservoir = new FluidReservoir(reservoirNBT.getId());
            long[] depositArray = compound.getLongArray("Deposits");
            reservoir.deposits = Arrays.stream(depositArray).boxed().toList();
            reservoir.oilReserves = compound.getInt("Reserves");
            data.reservoirs.add(reservoir);

        }

        return data;
    }

    public static DepositSavedData load(MinecraftServer server) {
        return server.overworld()
                .getDataStorage()
                .computeIfAbsent(DepositSavedData::load, DepositSavedData::new, "tfmg_deposits");
    }
}
