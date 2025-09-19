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
import net.neoforged.neoforge.event.level.LevelEvent;
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

    // Generated known networks
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

    protected void clearClustersFrom(BlockPos startingPos) {
        traverseAll(
                startingPos,
                (from, to) -> {
                    if(to.cluster != null) {
                        clusters.remove(to.cluster);
                    }
                    to.cluster = null;
                }
        );
    }

    /// It is the caller's responsibility to clear the clusters from both the global hashmap and the individual members
    /// beforehand
    protected void calculateClustersFrom(BlockPos startingPos) {
        final ElectricalCluster[] cluster = { null };
        traverseAll(
                startingPos,
                (from, to) -> {
                    if(to.isSource()) {
                        if (cluster[0] == null) { // This delay in the creation prevents empty clusters
                            cluster[0] = createNewCluster();
                        }
                        to.cluster = cluster[0].id;
                    }
                },
                (from, to) -> to.cluster == null && !to.isVoltageChanger() // Only keep traversing if the element's cluster is null
        );
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
        Set<BlockPos> neighborNeighbors = new HashSet<>(6);

        data.getPotentialNeighbors(neighbors);
        for(BlockPos neighborPos : neighbors) {
            if(neighborPos.equals(pos)) {
                continue;
            }

            if(!(level.getBlockEntity(neighborPos) instanceof IElectric neighborBe)) {
                continue;
            }

            ElectricData neighborData = neighborBe.getElectricData();
            neighborNeighbors.clear();
            neighborData.getPotentialNeighbors(neighborNeighbors);

            if(!neighborNeighbors.contains(pos)) {
                continue;
            }
            UnloadedMember neighborMember = members.get(neighborPos);
            if(neighborMember == null) {
                neighborData.connectNextTick = true;
                continue;
            }
            neighborMember.connections.add(pos);
            member.connections.add(neighborPos);
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
        for(BlockPos neighborPos : member.connections) {
            if(neighborPos.equals(pos)) {
                continue;
            }
            UnloadedMember neighborMember = members.get(neighborPos);
            if(neighborMember == null) {
                continue;
            }
            neighborMember.connections.remove(pos);
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
            boolean dirty = member.sync(data);
            boolean isSource = member.isSource();

            if(isSource && !wasSource) {
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
        if(!(level.getBlockEntity(pos) instanceof IElectric be)) {
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
            BiConsumer<UnloadedMember, UnloadedMember> callback
    ) {
        traverseAll(startingPos, callback, (x, y) -> true);
    }

    protected void traverseAll(
            BlockPos startingPos,
            BiConsumer<UnloadedMember, UnloadedMember> callback,
            BiPredicate<UnloadedMember, UnloadedMember> filter
    ) {
        UnloadedMember startingMember = members.get(startingPos);

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
            if(!filter.test(memberPair.getFirst(), memberPair.getSecond())) {
                continue;
            }

            for(BlockPos neighborPos : memberPair.getSecond().connections) {
                UnloadedMember neighborMember = members.get(neighborPos);
                if(neighborMember == null) {
                    continue;
                }
                if(visited.add(neighborPos)) {
                    toVisit.add(Pair.of(memberPair.getSecond(), neighborMember));
                }
            }

            callback.accept(memberPair.getFirst(), memberPair.getSecond());
        }
    }

    public void update(BlockPos startingPos) {
        // Dummy very stupid implementation: find source with the highest voltage and use that.
        // Then sum up the max amperage of all the sources combined and use that.

        // This doesn't account for components such as diodes or components that might change the voltage along the way
        final float[] totalAmps = {0};
        final float[] highestVoltage = {0};
        float totalProduction;
        float totalUsage;

        traverseAll(startingPos, (from, to) -> {
            if(to.isSource()) {
                if (to.generatedVoltage > highestVoltage[0]) {
                    highestVoltage[0] = to.generatedVoltage;
                }

                float generatedAmps = to.calcGeneratedAmps();
                to.ampsProvided = generatedAmps;
                totalAmps[0] += generatedAmps;
            }
        });
        totalProduction = totalAmps[0];

        // Calculate total consumption for all components and apply voltage
        totalAmps[0] = 0;

        traverseAll(startingPos, (from, to) -> {
            to.frequency = 0;
            to.voltage = highestVoltage[0];
            float consumedAmps = to.calcConsumedAmps();
            to.ampsConsumed = consumedAmps;
            if(!to.isSource()) {
                to.ampsProvided = 0;
            }
            totalAmps[0] += consumedAmps;
        });
        totalUsage = totalAmps[0];

        traverseAll(startingPos, (from, to) -> {
            if(level.isLoaded(to.pos) && level.getBlockEntity(to.pos) instanceof IElectric be) {
                ElectricData data = be.getElectricData();
                data.totalNetworkUsage = totalUsage;
                data.totalNetworkProduction = totalProduction;
                data.syncNextTick = true;
            }
        });

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
