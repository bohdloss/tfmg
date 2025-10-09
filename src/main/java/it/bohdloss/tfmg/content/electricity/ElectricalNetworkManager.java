package it.bohdloss.tfmg.content.electricity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.*;

@EventBusSubscriber
public class ElectricalNetworkManager extends SavedData {
    public static final Map<LevelAccessor, ElectricalNetworkManager> spaces = new HashMap<>();

    public static final Codec<ElectricalNetworkManager> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.list(UnloadedMember.CODEC).fieldOf("ElectricalComponents").forGetter(x -> x.members.values().stream().toList())
    ).apply(inst, ElectricalNetworkManager::fromCodec));

    public ServerLevel level;

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

    private ElectricalNetworkManager() { }

    private ElectricalNetworkManager(LevelAccessor level) {
        this.level = level instanceof ServerLevel sLevel ? sLevel : null;
    }

    public static ElectricalNetworkManager fromCodec(List<UnloadedMember> members) {
        ElectricalNetworkManager self = new ElectricalNetworkManager();
        for(UnloadedMember member : members) {
            self.members.put(member.pos, member);
        }

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
                (neverVisited, from, to) -> {
                    if(to.cluster != null) {
                        clusters.remove(to.cluster);
                    }
                    to.cluster = null;

                    return true;
                },
                (neverVisited, from, to) -> neverVisited && !goesThroughVoltageChanger(from, to)
        );
    }

    public static boolean goesThroughVoltageChanger(UnloadedMember from, UnloadedMember to) {
        return from.hasOutput(to.pos) || to.hasOutput(from.pos);
    }

    /// It is the caller's responsibility to clear the clusters from both the global hashmap and the individual members
    /// beforehand
    protected void calculateClustersFrom(BlockPos startingPos) {
        final ElectricalCluster[] cluster = { null };
        HashSet<BlockPos> plsDestroy = new HashSet<>();
        traverseAll(
                startingPos,
                (neverVisited, from, to) -> {
                    if(to.isSource()) {
                        if (cluster[0] == null) { // This delay in the creation prevents empty clusters
                            cluster[0] = createNewCluster();
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
                (neverVisited, from, to) -> neverVisited && !goesThroughVoltageChanger(from, to)
        );

        for(BlockPos pos : plsDestroy) {
            level.destroyBlock(pos, true);
        }
    }

    /// Creates a new electrical network with a universally unique id
    protected ElectricalCluster createNewCluster() {
        Random random = new Random();

        long id = random.nextLong();
        while(clusters.containsKey(id)) {
            id = random.nextLong();
        }
        ElectricalCluster network = new ElectricalCluster(id);
        clusters.put(id, network);
        setDirty();

        return network;
    }

    public void add(BlockPos pos) {
        if(!(level.getBlockEntity(pos) instanceof IElectric be)) {
            return;
        }
        ElectricData data = be.getElectricData();
        UnloadedMember member = members.computeIfAbsent(pos, UnloadedMember::new);
        boolean dirty = member.sync(data);

        // Calculate connections
        Set<BlockPos> neighbors = new HashSet<>(6);
        Set<BlockPos> outputs = new HashSet<>();
        Set<BlockPos> neighborNeighbors = new HashSet<>(6);
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
            boolean wasSource = member.isSource();
            float prevGenVoltage = member.generatedVoltage;
            float preGenResistance = member.generatorResistance;
            float preIOMul = member.inputOutputVoltageMultiplier;
            float oreOIMul = member.outputInputVoltageMultiplier;

            boolean dirty = member.sync(data);

            boolean isSource = member.isSource();
            float genVoltage = member.generatedVoltage;
            float genResistance = member.generatorResistance;
            float IOMul = member.inputOutputVoltageMultiplier;
            float OIMul = member.outputInputVoltageMultiplier;

            if(wasSource != isSource || prevGenVoltage != genVoltage || preGenResistance != genResistance || preIOMul != IOMul || oreOIMul != OIMul) {
                clearClustersFrom(pos);
                calculateClustersFrom(pos);
            }

            if (dirty) {
                update(pos);
            }
        }

        setDirty();
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
            TriConsumer<Boolean, UnloadedMember, UnloadedMember> callback
    ) {
        traverseAll(
                startingPos,
                (neverVisited, from, to) -> {
                    callback.accept(neverVisited, from, to);
                    return true;
                },
                (neverVisited, from, to) -> neverVisited
        );
    }

    protected void traverseAll(
            BlockPos startingPos,
            TriPredicate<Boolean, UnloadedMember, UnloadedMember> callback,
            TriPredicate<Boolean, UnloadedMember, UnloadedMember> shouldTraverse
    ) {
        UnloadedMember startingMember = members.get(startingPos);
        if(startingMember == null) {
            return;
        }

        Set<BlockPos> visited = new HashSet<>();
        List<Pair<UnloadedMember, UnloadedMember>> toVisit = new ArrayList<>();

        visited.add(startingPos);
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
            boolean keepGoing = callback.test(!visited.contains(memberPair.getSecond().pos), memberPair.getFirst(), memberPair.getSecond());

            // Stop traversing this branch
            if(!keepGoing) {
                continue;
            }

            for(BlockPos neighborPos : memberPair.getSecond().getConnections()) {
                UnloadedMember neighborMember = members.get(neighborPos);
                if(neighborMember == null) {
                    continue;
                }
                boolean neverVisited = !visited.contains(neighborPos);
                if(shouldTraverse.test(neverVisited, memberPair.getSecond(), neighborMember)) {
                    visited.add(neighborPos);
                    toVisit.add(Pair.of(memberPair.getSecond(), neighborMember));
                }
            }
        }
    }

    public void update(BlockPos startingPos) {
        final float[] totalWatts = { 0 };
        final float[] highestVoltage = { 0 };
        float totalProduction;
        float totalUsage;

        List<ElectricalCluster> foundClusters = new ArrayList<>();

        // First pass: reset compiled component data and find all clusters
        traverseAll(startingPos, (neverVisited, from, to) -> {
            ElectricalCluster cluster;
            if(to.cluster != null && (cluster = clusters.get(to.cluster)) != null) {
                foundClusters.add(cluster);
            }

            to.frequency = 0;
            to.voltage = 0;
            to.wattsConsumed = 0;
            to.wattsProvided = 0;
            to.wattsReceived = 0;
        });

        Set<BlockPos> plsDestroy = new HashSet<>();

        float[] remainingWatts = { 0 };
        for(ElectricalCluster cluster : foundClusters) {
            float clusterVoltage = cluster.highestVoltage;
            remainingWatts[0] += cluster.totalWatts;

            HashMap<BlockPos, Boolean> voltageChangerVisitedDirection = new HashMap<>();

            traverseAll(cluster.referenceSource, (neverVisited, from, to) -> {
                float voltage = from == null ? clusterVoltage : from.voltage;
                float frequency = from == null ? cluster.frequency : from.frequency;

                if(from != null && from.hasOutput(to.pos)) {
                    voltage *= from.inputOutputVoltageMultiplier;
                    frequency *= from.inputOutputFrequencyMultiplier;
                }
                if(from != null && to.hasOutput(from.pos)) {
                    voltage *= to.outputInputVoltageMultiplier;
                    frequency *= to.outputInputFrequencyMultiplier;
                }

                // If the voltage is 0 after traversing voltage modifiers, stop traversing this branch
                if(voltage == 0) {
                    return false;
                }

                to.voltage = Math.max(to.voltage, voltage);

                if(neverVisited) {
                    to.frequency = frequency;
                } else if(to.frequency != frequency){
                    plsDestroy.add(from.pos);
                }

                float consumedWatts = to.calcConsumedAmps(to.voltage) * to.voltage;
                float wattsDiff = consumedWatts - to.wattsConsumed;
                to.wattsConsumed += wattsDiff;
                if(neverVisited) {
                    to.wattsReceived += cluster.totalWatts;
                }
                remainingWatts[0] -= wattsDiff;

                if(to.isSource()) {
                    to.wattsProvided = to.calcGeneratedAmps() * to.generatedVoltage;
                }

                return true;
            },
            (neverVisited, from, to) -> {
                Boolean last = voltageChangerVisitedDirection.get(to.pos);
                if(to.isVoltageChanger() && !neverVisited && from.voltage >= to.voltage && last != null && !to.hasOutput(from.pos) == last) {
                    plsDestroy.add(to.pos);
                }
                if(from.isVoltageChanger() && !voltageChangerVisitedDirection.containsKey(from.pos)) {
                    voltageChangerVisitedDirection.put(from.pos, from.hasOutput(to.pos));
                }
                if(to.isVoltageChanger() && !voltageChangerVisitedDirection.containsKey(to.pos)) {
                    voltageChangerVisitedDirection.put(to.pos, !to.hasOutput(from.pos));
                }
                return neverVisited || (from.voltage > to.voltage && !to.isVoltageChanger());
            });
        }

        traverseAll(startingPos, (neverVisited, from, to) -> {
            if(level.isLoaded(to.pos) && level.getBlockEntity(to.pos) instanceof IElectric be) {
                ElectricData data = be.getElectricData();
                data.shortCircuit = remainingWatts[0] < 0;
                data.syncNextTick = true;
            }
        });

        for(BlockPos pos : plsDestroy) {
            level.destroyBlock(pos, true);
        }

        setDirty();
    }

    public static @NotNull ElectricalNetworkManager load(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider registries) {
        return CODEC.decode(NbtOps.INSTANCE, compoundTag).getOrThrow().getFirst();
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider registries) {
        return (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
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
