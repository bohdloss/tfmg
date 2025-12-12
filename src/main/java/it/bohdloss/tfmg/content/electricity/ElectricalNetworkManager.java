package it.bohdloss.tfmg.content.electricity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.bohdloss.tfmg.DebugStuff;
import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.content.electricity.base.ElectricData;
import it.bohdloss.tfmg.content.electricity.base.IElectric;
import it.bohdloss.tfmg.content.electricity.base.UnloadedMember;
import net.createmod.catnip.levelWrappers.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriPredicate;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.*;

@EventBusSubscriber
public class ElectricalNetworkManager extends SavedData {
    public static final Map<LevelAccessor, ElectricalNetworkManager> spaces = new HashMap<>();

    public static final Codec<ElectricalNetworkManager> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.list(UnloadedMember.CODEC).fieldOf("ElectricalComponents").orElse(List.of()).forGetter(x -> x.members.values().stream().toList()),
            Codec.list(Codec.pair(BlockPos.CODEC, Codec.LONG)).fieldOf("ScheduledTicks").orElse(List.of()).forGetter(x -> x.scheduledTicks.entrySet().stream().map(y -> new Pair<BlockPos, Long>(y.getKey(), y.getValue())).toList()),
            Codec.LONG.fieldOf("Time").orElse(0L).forGetter(x -> x.time)
    ).apply(inst, ElectricalNetworkManager::fromCodec));

    public ServerLevel level;

    // Absolute time value after which an update should take place
    // The next scheduled update is set to the estimated time until the first accumulator will run out of power.
    public final HashMap<BlockPos, Long> scheduledTicks = new HashMap<>();

    // The time of this electrical network manager. Similar to LevelData::getGameTime except more reliable
    public long time;

    // The source of truth
    public final HashMap<BlockPos, UnloadedMember> members = new HashMap<>();

    // Generated known clusters.
    // Clusters are electrical sources that are connected to one another through non-voltage-altering
    // components/connectors, effectively meaning they act as a single combined electrical source.
    //
    // Since the time complexity of the electricity propagator is roughly O(N*M), where N is the number of
    // components in one network and M is the number of electrical sources, by using clusters we can reduce the value of
    // M by taking advantage of a pattern in the play-style of the create mod in which players tend to build many sources
    // next to each other.
    public final HashMap<Long, ElectricalCluster> clusters = new HashMap<>();

    // TODO
    // HashMap<ChunkPos, List<Long>> type of thing
    // Optimize for networks that lay completely outside of the loaded chunk range.
    // But why, if they're outside chunk range they just won't be updated, no?

    private ElectricalNetworkManager() { }

    private ElectricalNetworkManager(LevelAccessor level) {
        this.level = level instanceof ServerLevel sLevel ? sLevel : null;
    }

    public static ElectricalNetworkManager fromCodec(List<UnloadedMember> members, List<Pair<BlockPos, Long>> scheduledTicks, Long time) {
        ElectricalNetworkManager self = new ElectricalNetworkManager();
        for(UnloadedMember member : members) {
            self.members.put(member.pos, member);
        }
        for(Pair<BlockPos, Long> scheduledTick : scheduledTicks) {
            self.scheduledTicks.put(scheduledTick.getFirst(), scheduledTick.getSecond());
        }
        self.time = time;

        self.calculateAllClusters();

        return self;
    }

    public static ElectricalNetworkManager getInstance(LevelAccessor level) {
        if(level == null || level.isClientSide()) {
            throw new IllegalStateException("Uninitialized level or client level");
        }

        return spaces.computeIfAbsent(level, ElectricalNetworkManager::new);
    }

    /// Expensive!!
    protected void clearAllClusters() {
        members.values().forEach(x -> x.cluster = null);
        clusters.clear();
    }

    /// It is the caller's responsibility to clear the clusters from both the global hashmap and the individual members
    /// beforehand
    protected void calculateAllClusters() {
        for(UnloadedMember member : members.values()) {
            calculateClustersFrom(member.pos);
        }
    }

    /// DOES stop at voltage changers
    protected void clearClustersFrom(BlockPos startingPos) {
        traverseAll(
                startingPos,
                (nVisited, from, to) -> {
                    if(to.cluster != null) {
                        clusters.remove(to.cluster);
                    }
                    to.cluster = null;

                    return true;
                },
                (nVisited, from, to) -> nVisited == 0 && !goesThroughVoltageChanger(from, to)
        );
    }

    public static boolean goesThroughVoltageChanger(UnloadedMember from, UnloadedMember to) {
        return (from.isVoltageChanger() && from.hasOutput(to.pos)) || (to.isVoltageChanger() && to.hasOutput(from.pos));
    }

    public static float transferredVoltage(float voltage, UnloadedMember from, UnloadedMember to) {
        if(from != null && from.hasOutput(to.pos)) {
            voltage *= from.inputOutputVoltageMultiplier;
        }
        if(from != null && to.hasOutput(from.pos)) {
            voltage *= to.outputInputVoltageMultiplier;
        }
        return voltage;
    }

    public static float transferredFrequency(float frequency, UnloadedMember from, UnloadedMember to) {
        if(from != null && from.hasOutput(to.pos)) {
            frequency *= from.inputOutputFrequencyMultiplier;
        }
        if(from != null && to.hasOutput(from.pos)) {
            frequency *= to.outputInputFrequencyMultiplier;
        }
        return frequency;
    }

    /// It is the caller's responsibility to clear the clusters from both the global hashmap and the individual members
    /// beforehand
    protected void calculateClustersFrom(BlockPos startingPos) {
        final ElectricalCluster[] cluster = { null };
        HashSet<BlockPos> plsDestroy = new HashSet<>();
        traverseAll(
                startingPos,
                (nVisited, from, to) -> {
                    if(to.accumulator != null) {
                        // Before we check if it is a source, we MUST update the charge of the accumulator
                        to.accumulator.updateCharge(to, time);

                        // Schedule next tick while we're at it
                        long nextUpdate = to.accumulator.ticksUntilCharged(to);

                        // We only really need to schedule the update if it's DISCHARGING
                        if(nextUpdate < 0f) {
                            DebugStuff.show("Scheduling update " + (-nextUpdate) + " ticks from now at " + to.pos.toShortString());
                            scheduledTicks.put(to.pos, time - nextUpdate); // Minus because we normalize it back to positive
                        } else {
                            scheduledTicks.remove(to.pos);
                        }
                    } else {
                        scheduledTicks.remove(to.pos);
                    }
                    if(to.isSource()) {
                        if (cluster[0] == null) { // This delay in the creation prevents empty clusters
                            cluster[0] = createNewCluster(); // FIXME under some conditions this currently leaks "ghost" orphaned clusters.
                            cluster[0].frequency = to.generatorFrequency;
                        }

                        cluster[0].referenceSource = to.pos;
                        if(to.generatedVoltage > cluster[0].highestVoltage) {
                            cluster[0].highestVoltage = to.generatedVoltage;
                        }
                        // Calculate amount of watts generated by this source
                        float generatedWatts = to.calcGeneratedAmps() * to.generatedVoltage;
                        cluster[0].totalWatts += generatedWatts;
                        if(to.generatorFrequency != cluster[0].frequency) {
                            plsDestroy.add(to.pos); // Incompatible!
                        }

                        to.cluster = cluster[0].id;
                    }

                    return true;
                },
                // We don't need to re-visit already seen components
                // We also don't go past voltage changers' output connections
                (nVisited, from, to) -> nVisited == 0 && !goesThroughVoltageChanger(from, to)
        );

        for(BlockPos pos : plsDestroy) {
            level.destroyBlock(pos, true);
        }
    }

    /// Creates a new electrical cluster with a universally unique id
    protected ElectricalCluster createNewCluster() {
        Random random = new Random();

        long id = random.nextLong();
        while(clusters.containsKey(id)) {
            id = random.nextLong();
        }
        ElectricalCluster cluster = new ElectricalCluster(id);
        clusters.put(id, cluster);
        setDirty();

        return cluster;
    }

    public void add(BlockPos pos) {
        if(!(level.getBlockEntity(pos) instanceof IElectric be)) {
            return;
        }
        ElectricData data = be.getElectricData();
        UnloadedMember member = members.get(pos);
        if(member != null) {
            remove(pos); // Because otherwise some constraints about clusters are not respected
        }
        member = members.computeIfAbsent(pos, UnloadedMember::new);
        int status = member.sync(time, data);
        boolean dirty = (status & UnloadedMember.COMPONENT_DIRTY) != 0;

        // Calculate connections
        Set<BlockPos> neighbors = new HashSet<>();
        Set<BlockPos> outputs = new HashSet<>();
        Set<BlockPos> neighborNeighbors = new HashSet<>();
        Set<BlockPos> neighborOutputs = new HashSet<>();

        data.getPotentialNeighbors(neighbors);
        data.getOutputConnections(outputs);

        for(BlockPos neighborPos : neighbors) {
            if(neighborPos.equals(pos)) {
                continue;
            }

            if(!(level.getBlockEntity(neighborPos) instanceof IElectric neighborBe)) {
                continue;
            }

            ElectricData neighborData = neighborBe.getElectricData();
            neighborNeighbors.clear();
            neighborOutputs.clear();
            neighborData.getPotentialNeighbors(neighborNeighbors);
            neighborData.getOutputConnections(neighborOutputs);

            if(!neighborNeighbors.contains(pos)) {
                continue;
            }
            UnloadedMember neighborMember = members.get(neighborPos);
            if(neighborMember == null) {
                neighborData.connectNextTick = true;
                continue;
            }
            neighborMember.addConnection(pos, neighborOutputs.contains(pos));
            member.addConnection(neighborPos, outputs.contains(neighborPos));
            dirty = true;
        }

        clearClustersFrom(pos);
        calculateClustersFrom(pos);

        if(dirty) {
            update(pos);
        }

        setDirty();
    }

    public void remove(BlockPos pos) {
        UnloadedMember member = members.get(pos);
        if(member == null) {
            return;
        }
        scheduledTicks.remove(pos);

        clearClustersFrom(pos);

        members.remove(pos);

        List<BlockPos> validConnections = new ArrayList<>();

        // Disconnect neighbors
        for(BlockPos neighborPos : member.getConnections()) {
            if(neighborPos.equals(pos)) {
                continue;
            }
            UnloadedMember neighborMember = members.get(neighborPos);
            if(neighborMember == null) {
                continue;
            }
            neighborMember.removeConnection(pos);
            validConnections.add(neighborPos);
        }

        for(BlockPos neighbor : validConnections) {
            calculateClustersFrom(neighbor);
        }

        for(BlockPos neighbor : validConnections) {
            update(neighbor);
        }

        setDirty();
    }

    public void sync(BlockPos pos) {
        if(!(level.getBlockEntity(pos) instanceof IElectric be)) {
            return;
        }
        ElectricData data = be.getElectricData();
        UnloadedMember member = members.get(pos);
        if(member == null) {
            add(pos);
        } else {
            int status = member.sync(time, data);

            if((status & UnloadedMember.CLUSTER_DIRTY) != 0) {
                clearClustersFrom(pos);
                calculateClustersFrom(pos);
            }

            if ((status & UnloadedMember.COMPONENT_DIRTY) != 0) {
                update(pos);
            }
        }

        setDirty();
    }

    public void syncCharge(BlockPos pos) {
        if(!(level.getBlockEntity(pos) instanceof IElectric be)) {
            return;
        }
        ElectricData data = be.getElectricData();
        UnloadedMember member = members.get(pos);
        if(member != null && member.accumulator != null) {
            member.accumulator.updateCharge(member, time);
            setDirty();
            data.onChargeChange(member.accumulator.charge);
        }
    }

    public ElectricalCluster clusterFor(BlockPos pos) {
        if(!(level.getBlockEntity(pos) instanceof IElectric)) {
            return null;
        }
        UnloadedMember member = members.get(pos);
        if(member != null) {
            return clusters.get(member.cluster);
        }
        return null;
    }

    protected void traverseAll(
            BlockPos startingPos,
            TriConsumer<Integer, @Nullable UnloadedMember, UnloadedMember> callback
    ) {
        traverseAll(
                startingPos,
                (nVisited, from, to) -> {
                    callback.accept(nVisited, from, to);
                    return true;
                },
                (nVisited, from, to) -> nVisited == 0
        );
    }

    protected void traverseAll(
            BlockPos startingPos,
            TriPredicate<Integer, @Nullable UnloadedMember, UnloadedMember> callback,
            TriPredicate<Integer, UnloadedMember, UnloadedMember> shouldTraverse
    ) {
        UnloadedMember startingMember = members.get(startingPos);
        if(startingMember == null) {
            return;
        }

        Map<BlockPos, Integer> visited = new HashMap<>();
        List<Pair<UnloadedMember, UnloadedMember>> toVisit = new ArrayList<>();

        visited.put(startingPos, 1);
        toVisit.add(Pair.of(null, startingMember));

        Pair<UnloadedMember, UnloadedMember> memberPair;

        Supplier<Pair<UnloadedMember, UnloadedMember>> removeLast = () -> {
            if(toVisit.isEmpty()) {
                return null;
            } else {
                return toVisit.removeLast();
            }
        };

        while((memberPair = removeLast.get()) != null) {
            boolean keepGoing = callback.test(visited.getOrDefault(memberPair.getSecond().pos, 1) - 1, memberPair.getFirst(), memberPair.getSecond());

            // Stop traversing this branch
            if(!keepGoing) {
                continue;
            }

            for(BlockPos neighborPos : memberPair.getSecond().getConnections()) {
                UnloadedMember neighborMember = members.get(neighborPos);
                if(neighborMember == null) {
                    continue;
                }
                int nVisited = visited.getOrDefault(neighborPos, 0);
                if(shouldTraverse.test(nVisited, memberPair.getSecond(), neighborMember)) {
                    visited.put(neighborPos, nVisited + 1);
                    toVisit.add(Pair.of(memberPair.getSecond(), neighborMember));
                }
            }
        }
    }

    public void update(BlockPos startingPos) {
        Set<ElectricalCluster> foundClusters = new HashSet<>();

        // First pass: reset compiled component data and find all clusters
        traverseAll(startingPos, (neverVisited, from, to) -> {
            ElectricalCluster cluster;
            if(to.cluster != null && (cluster = clusters.get(to.cluster)) != null) {
                foundClusters.add(cluster);
            }

            if(to.accumulator != null) {
                to.accumulator.updateCharge(to, time); // Update charge before updating voltage to avoid incorrect calculations
            }

            to.current = 0;
            to.frequency = -1; // Uninitialized, invalid value
            to.voltage = 0;
            to.wattsConsumed = 0;
            to.wattsProvided = 0;
            to.wattsReceived = 0;
        });

        Set<BlockPos> plsDestroy = new HashSet<>();
//        Set<BlockPos> shortedClusters = new HashSet<>();

        float[] remainingWatts = { 0 };
        for(ElectricalCluster cluster : foundClusters) {
            float clusterVoltage = cluster.highestVoltage;
            remainingWatts[0] += cluster.totalWatts;

            final int MAX_ITERATIONS = 100;

            traverseAll(cluster.referenceSource, (nVisited, from, to) -> {
                float voltage = from == null ? clusterVoltage : from.voltage;
                float frequency = from == null ? cluster.frequency : from.frequency;

                voltage = transferredVoltage(voltage, from, to);
                frequency = transferredFrequency(frequency, from, to);

                if(to.isVoltageChanger() && nVisited > MAX_ITERATIONS) {
                    plsDestroy.add(to.pos);
                    return false;
                }

                // If the voltage is 0 after traversing voltage modifiers, stop traversing this branch
                if(voltage == 0) {
                    return false;
                }
                if(voltage < to.voltage) {
                    return false;
                }
                to.voltage = voltage;

                if(to.frequency == -1) {
                    to.frequency = frequency;
                } else if(to.frequency != frequency){
                    plsDestroy.add(from.pos);
                    return false;
                }

                float consumedWatts = to.calcConsumedAmps(to.voltage) * to.voltage;
                float wattsDiff = consumedWatts - to.wattsConsumed;
                to.wattsConsumed += wattsDiff;
                if(nVisited == 0) {
                    to.wattsReceived += cluster.totalWatts;
                }
                remainingWatts[0] -= wattsDiff;

                if(to.isSource()) {
                    to.wattsProvided = to.calcGeneratedAmps() * to.generatedVoltage;
                }

                return true;
            },
            (nVisited, from, to) -> {
                float transferredVoltage = transferredVoltage(from.voltage, from, to);
                return nVisited == 0 || transferredVoltage > to.voltage;
            });
        }

        traverseAll(startingPos, (neverVisited, from, to) -> {
            to.satisfaction = (remainingWatts[0] < 0) ? 0 : to.wattsConsumed;
            if(level.isLoaded(to.pos) && level.getBlockEntity(to.pos) instanceof IElectric be) {
                ElectricData data = be.getElectricData();
                data.shortCircuit = remainingWatts[0] < 0;
                data.syncNextTick = true;
            }
        });

        setDirty();

        for(BlockPos pos : plsDestroy) {
            level.destroyBlock(pos, true);
        }
    }

    public static @NotNull ElectricalNetworkManager load(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider registries) {
        return CODEC.decode(NbtOps.INSTANCE, compoundTag).getOrThrow().getFirst();
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider registries) {
        return (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
    }

    @SubscribeEvent
    public static void tickWorld(LevelTickEvent.Post event) {
        if(event.getLevel() instanceof ServerLevel level) {
            ElectricalNetworkManager manager = getInstance(level);

            manager.time++; // <<= !!!!!!!

            List<BlockPos> doUpdate = null;

            for(Map.Entry<BlockPos, Long> clock : manager.scheduledTicks.entrySet()) {
                if(manager.time >= clock.getValue()) {
                    if(doUpdate == null) {
                        doUpdate = new ArrayList<>();
                    }
                    doUpdate.add(clock.getKey());
                } else {
                    clock.setValue(clock.getValue() - 1);
                }
            }
            if(doUpdate != null) {
                for(BlockPos pos : doUpdate) {
                    manager.scheduledTicks.remove(pos);
                }
                for(BlockPos pos : doUpdate) {
                    DebugStuff.show("Running update at " + pos.toShortString());
                    manager.clearClustersFrom(pos);
                    manager.calculateClustersFrom(pos);
                    manager.update(pos);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLoadWorld(LevelEvent.Load event) {
        if(event.getLevel() instanceof ServerLevel level) {
            ElectricalNetworkManager manager = level.getDataStorage().computeIfAbsent(
                    new Factory<>(ElectricalNetworkManager::new,
                    ElectricalNetworkManager::load),
                    "electrical_networks"
            );
            manager.level = level;
            spaces.put(level, manager);
            TFMG.LOGGER.debug("Prepared Electric Network Space for " + WorldHelper.getDimensionID(level));
        }
    }

    @SubscribeEvent
    public static void onUnloadWorld(LevelEvent.Unload event) {
        if(event.getLevel() instanceof ServerLevel level) {
            spaces.remove(level);
            TFMG.LOGGER.debug("Removed Electric Network Space for " + WorldHelper.getDimensionID(level));
        }
    }
}
