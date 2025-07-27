package it.bohdloss.tfmg.content.electricity;

import com.simibubi.create.infrastructure.config.AllConfigs;
import it.bohdloss.tfmg.content.electricity.base.IElectric;
import it.bohdloss.tfmg.content.electricity.base.IElectricBlock;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;
import java.util.List;

public class ElectricityPropagator {
    private static final int MAX_FLICKER_SCORE = 128;

    /**
     * Insert the added position to the electric network.
     *
     * @param worldIn
     * @param pos
     */
    public static void handleAdded(Level worldIn, BlockPos pos, IElectric addedTE) {
        if (worldIn.isClientSide) {
            return;
        }
        if (!worldIn.isLoaded(pos)) {
            return;
        }
        propagateNewSource(addedTE);
    }

    /**
     * Remove the given entity from the network.
     *
     * @param worldIn
     * @param pos
     * @param removedBE
     */
    public static void handleRemoved(Level worldIn, BlockPos pos, IElectric removedBE) {
        if (worldIn.isClientSide) {
            return;
        }
        if (removedBE == null) {
            return;
        }
        if (removedBE.getTheoreticalVoltage() == 0) {
            return;
        }

        for (BlockPos neighbourPos : getPotentialNeighbourLocations(removedBE)) {
            BlockState neighbourState = worldIn.getBlockState(neighbourPos);
            if (!(neighbourState.getBlock() instanceof IElectricBlock)) {
                continue;
            }
            BlockEntity blockEntity = worldIn.getBlockEntity(neighbourPos);
            if (!(blockEntity instanceof IElectric neighbourBE)) {
                continue;
            }

            if (!neighbourBE.hasElectricalSource() || !neighbourBE.getElectricalSource().equals(pos)) {
                continue;
            }

            propagateMissingSource(neighbourBE);
        }
    }

    /**
     * Search for sourceless networks attached to the given entity and update them.
     *
     * @param currentTE
     */
    private static void propagateNewSource(IElectric currentTE) {
        BlockPos pos = currentTE.getBlockPos();
        Level world = currentTE.getLevel();

        for (IElectric neighbourTE : getConnectedNeighbours(currentTE)) {
            float voltageOfCurrent = currentTE.getTheoreticalVoltage();
            float voltageOfNeighbour = neighbourTE.getTheoreticalVoltage();
            float newVoltage = getConveyedVoltage(currentTE, neighbourTE);
            float oppositeVoltage = getConveyedVoltage(neighbourTE, currentTE);

            if (newVoltage == 0 && oppositeVoltage == 0) {
                continue;
            }

            boolean tooHigh = Math.abs(newVoltage) > AllConfigs.server().kinetics.maxRotationSpeed.get()
                    || Math.abs(oppositeVoltage) > AllConfigs.server().kinetics.maxRotationSpeed.get();
            // Check for both the new voltage and the opposite voltage, just in case

            boolean voltageChangedTooOften = currentTE.getVoltageFlickerScore() > MAX_FLICKER_SCORE;
            if (/*tooHigh ||*/ voltageChangedTooOften) {
                world.destroyBlock(pos, true); // TODO handle destruction
                return;
            }

            // Neighbour higher voltage, overpower the incoming tree
            if (Math.abs(oppositeVoltage) > Math.abs(voltageOfCurrent)) {
                float prevVoltage = currentTE.getVoltage();
                currentTE.setElectricalSource(neighbourTE.getBlockPos());
                currentTE.setVoltage(getConveyedVoltage(neighbourTE, currentTE));
                currentTE.onVoltageChanged(prevVoltage);
                currentTE.sendData();

                propagateNewSource(currentTE);
                return;
            }

            // Current higher voltage, overpower the neighbours' tree
            if (Math.abs(newVoltage) >= Math.abs(voltageOfNeighbour)) {

                // Do not overpower you own network -> cycle
                if (!currentTE.hasElectricalNetwork() || currentTE.getElectricalNetwork().equals(neighbourTE.getElectricalNetwork())) {
                    float epsilon = Math.abs(voltageOfNeighbour) / 256f / 256f;
                    if (Math.abs(newVoltage) > Math.abs(voltageOfNeighbour) + epsilon) {
                        world.destroyBlock(pos, true);
                    }
                    continue;
                }

                if (currentTE.hasElectricalSource() && currentTE.getElectricalSource().equals(neighbourTE.getBlockPos())) {
                    currentTE.removeElectricalSource();
                }

                float prevVoltage = neighbourTE.getVoltage();
                neighbourTE.setElectricalSource(currentTE.getBlockPos());
                neighbourTE.setVoltage(getConveyedVoltage(currentTE, neighbourTE));
                neighbourTE.onVoltageChanged(prevVoltage);
                neighbourTE.sendData();
                propagateNewSource(neighbourTE);
                continue;
            }

            if (neighbourTE.getTheoreticalVoltage() == newVoltage) {
                continue;
            }

            float prevVoltage = neighbourTE.getVoltage();
            neighbourTE.setVoltage(newVoltage);
            neighbourTE.setElectricalSource(currentTE.getBlockPos());
            neighbourTE.onVoltageChanged(prevVoltage);
            neighbourTE.sendData();
            propagateNewSource(neighbourTE);
        }
    }

    /**
     * Clear the entire subnetwork depending on the given entity and find a new
     * source
     *
     * @param updateTE
     */
    private static void propagateMissingSource(IElectric updateTE) {
        final Level world = updateTE.getLevel();

        List<IElectric> potentialNewSources = new LinkedList<>();
        List<BlockPos> frontier = new LinkedList<>();
        frontier.add(updateTE.getBlockPos());
        BlockPos missingSource = updateTE.hasElectricalSource() ? updateTE.getElectricalSource() : null;

        while (!frontier.isEmpty()) {
            final BlockPos pos = frontier.remove(0);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof IElectric currentBE)) {
                continue;
            }

            currentBE.removeElectricalSource();
            currentBE.sendData();

            for (IElectric neighbourBE : getConnectedNeighbours(currentBE)) {
                if (neighbourBE.getBlockPos().equals(missingSource)) {
                    continue;
                }
                if (!neighbourBE.hasElectricalSource()) {
                    continue;
                }

                if (!neighbourBE.getElectricalSource().equals(pos)) {
                    potentialNewSources.add(neighbourBE);
                    continue;
                }

                if (neighbourBE.isElectricalSource()) {
                    potentialNewSources.add(neighbourBE);
                }

                frontier.add(neighbourBE.getBlockPos());
            }
        }

        for (IElectric newSource : potentialNewSources) {
            if (newSource.hasElectricalSource() || newSource.isElectricalSource()) {
                propagateNewSource(newSource);
                return;
            }
        }
    }

    private static List<IElectric> getConnectedNeighbours(IElectric be) {
        List<IElectric> neighbours = new LinkedList<>();
        for (BlockPos neighbourPos : getPotentialNeighbourLocations(be)) {
            final IElectric neighbourBE = findConnectedNeighbour(be, neighbourPos);
            if (neighbourBE == null)
                continue;

            neighbours.add(neighbourBE);
        }
        return neighbours;
    }

    private static List<BlockPos> getPotentialNeighbourLocations(IElectric be) {
        List<BlockPos> neighbours = new LinkedList<>();
        BlockPos blockPos = be.getBlockPos();
        Level level = be.getLevel();

        if (!level.isLoaded(blockPos)) {
            return neighbours;
        }

        for (Direction facing : Iterate.directions) {
            BlockPos relative = blockPos.relative(facing);
            if (level.isLoaded(relative)) {
                neighbours.add(relative);
            }
        }

        BlockState blockState = be.getBlockState();
        if (!(blockState.getBlock() instanceof IElectricBlock block)) {
            return neighbours;
        }
        return be.addElectricPropagationLocations(block, blockState, neighbours);
    }

    public static boolean isConnected(IElectric from, IElectric to) {
        final BlockState stateFrom = from.getBlockState();
        final BlockState stateTo = to.getBlockState();
        return getVoltageModifier(from, to) != 0
                || from.isCustomElectricConnection(to, stateFrom, stateTo);
    }

    private static IElectric findConnectedNeighbour(IElectric currentTE, BlockPos neighbourPos) {
        BlockState neighbourState = currentTE.getLevel().getBlockState(neighbourPos);

        if (!(neighbourState.getBlock() instanceof IElectricBlock)) {
            return null;
        }
        if (!neighbourState.hasBlockEntity()) {
            return null;
        }
        BlockEntity neighbourBE = currentTE.getLevel().getBlockEntity(neighbourPos);

        if (!(neighbourBE instanceof IElectric neighbourEBE)) {
            return null;
        }
        if (!(neighbourEBE.getBlockState().getBlock() instanceof IElectricBlock)) {
            return null;
        }
        if (!isConnected(currentTE, neighbourEBE) && !isConnected(neighbourEBE, currentTE)) {
            return null;
        }
        return neighbourEBE;
    }

    private static float getVoltageModifier(IElectric from, IElectric to) {
        final BlockState stateFrom = from.getBlockState();
        final BlockState stateTo = to.getBlockState();

        Block fromBlock = stateFrom.getBlock();
        Block toBlock = stateTo.getBlock();
        if (!(fromBlock instanceof IElectricBlock definitionFrom && toBlock instanceof IElectricBlock definitionTo)) {
            return 0;
        }

        final BlockPos diff = to.getBlockPos().subtract(from.getBlockPos());
        final Direction direction = Direction.getNearest(diff.getX(), diff.getY(), diff.getZ());
        final Level world = from.getLevel();

        boolean alignedAxes = true;
        for (Direction.Axis axis : Direction.Axis.values()) {
            if (axis != direction.getAxis()) {
                if (axis.choose(diff.getX(), diff.getY(), diff.getZ()) != 0) {
                    alignedAxes = false;
                }
            }
        }

        boolean connectedByAxis =
                alignedAxes && definitionFrom.hasConnectorTowards(world, from.getBlockPos(), stateFrom, direction)
                        && definitionTo.hasConnectorTowards(world, to.getBlockPos(), stateTo, direction.getOpposite());

        float custom = from.propagateVoltageTo(to, stateFrom, stateTo, diff, connectedByAxis);
        if (custom != 0) {
            return custom;
        }

        // Axis <-> Axis
        if (connectedByAxis) {
            float axisModifier = getAxisModifier(to, direction.getOpposite());
            if (axisModifier != 0) {
                axisModifier = 1 / axisModifier;
            }
            return getAxisModifier(from, direction) * axisModifier;
        }

        return 0;
    }

    private static float getConveyedVoltage(IElectric from, IElectric to) {
        return from.getTheoreticalVoltage() * getVoltageModifier(from, to);
    }

    private static float getAxisModifier(IElectric be, Direction direction) {
        return 1;
    }
}
