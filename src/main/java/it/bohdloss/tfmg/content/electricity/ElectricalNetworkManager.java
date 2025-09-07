package it.bohdloss.tfmg.content.electricity;

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
import net.neoforged.neoforge.event.level.LevelEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@EventBusSubscriber
public class ElectricalNetworkManager extends SavedData {
    public static final Map<LevelAccessor, ElectricalNetworkManager> spaces = new HashMap<>();

    public static final Codec<ElectricalNetworkManager> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.list(UnloadedMember.CODEC).fieldOf("ElectricalComponents").forGetter(x -> x.members.values().stream().toList())
    ).apply(inst, ElectricalNetworkManager::fromCodec));

    public ServerLevel level;
    public final HashMap<BlockPos, UnloadedMember> members = new HashMap<>();

    private ElectricalNetworkManager() { }

    private ElectricalNetworkManager(LevelAccessor level) {
        this.level = level instanceof ServerLevel sLevel ? sLevel : null;
    }

    public static ElectricalNetworkManager fromCodec(List<UnloadedMember> members) {
        ElectricalNetworkManager self = new ElectricalNetworkManager();
        for(UnloadedMember member : members) {
            self.members.put(member.pos, member);
        }
        return self;
    }

    public static ElectricalNetworkManager getInstance(LevelAccessor level) {
        if(level == null || level.isClientSide()) {
            throw new IllegalStateException("Uninitialized level or client level");
        }

        return spaces.computeIfAbsent(level, ElectricalNetworkManager::new);
    }

    public static void add(LevelAccessor level, BlockPos pos) {
        ElectricalNetworkManager space = getInstance(level);
        if(!(level.getBlockEntity(pos) instanceof IElectric be)) {
            return;
        }
        ElectricData data = be.getElectricData();
        UnloadedMember member = space.members.computeIfAbsent(pos, UnloadedMember::new);
        boolean dirty = member.sync(data);

        // Calculate connections
        Set<BlockPos> neighbors = new HashSet<>(6);
        Set<BlockPos> neighborNeighbors = new HashSet<>(6);

        data.getPotentialNeighbors(neighbors);
        for(BlockPos neighborPos : neighbors) {
            if(neighborPos.equals(pos)) {
                continue;
            }

            if(!(space.level.getBlockEntity(neighborPos) instanceof IElectric neighborBe)) {
                continue;
            }

            ElectricData neighborData = neighborBe.getElectricData();
            neighborNeighbors.clear();
            neighborData.getPotentialNeighbors(neighborNeighbors);

            if(!neighborNeighbors.contains(pos)) {
                continue;
            }
            UnloadedMember neighborMember = space.members.get(neighborPos);
            if(neighborMember == null) {
                neighborData.connectNextTick = true;
                continue;
            }
            neighborMember.connections.add(pos);
            member.connections.add(neighborPos);
            dirty |= true;
        }

        if(dirty) {
            update(level, pos);
        }

        space.setDirty();
    }

    public static void remove(LevelAccessor level, BlockPos pos) {
        ElectricalNetworkManager space = getInstance(level);
        UnloadedMember member = space.members.remove(pos);
        if(member == null) {
            return;
        }

        List<BlockPos> validConnections = new ArrayList<>();

        // Disconnect neighbors
        for(BlockPos neighborPos : member.connections) {
            if(neighborPos.equals(pos)) {
                continue;
            }
            UnloadedMember neighborMember = space.members.get(neighborPos);
            if(neighborMember == null) {
                continue;
            }
            neighborMember.connections.remove(pos);
            validConnections.add(neighborPos);
        }

        for(BlockPos neighbor : validConnections) {
            update(level, neighbor);
        }

        space.setDirty();
    }

    public static void sync(LevelAccessor level, BlockPos pos) {
        ElectricalNetworkManager space = getInstance(level);
        if(!(level.getBlockEntity(pos) instanceof IElectric be)) {
            return;
        }
        ElectricData data = be.getElectricData();
        UnloadedMember member = space.members.get(data.getBlockPos());
        if(member == null) {
            add(level, pos);
        } else {
            boolean dirty = member.sync(data);
            if (dirty) {
                update(level, data.getBlockPos());
            }
        }

        space.setDirty();
    }

    protected void traverseAll(BlockPos startingPos, Consumer<UnloadedMember> callback) {
        Set<BlockPos> visited = new HashSet<>();
        List<BlockPos> toVisit = new ArrayList<>();

        visited.add(startingPos);
        toVisit.add(startingPos);

        BlockPos nextPos;

        Supplier<BlockPos> removeLast = () -> {
            if(toVisit.isEmpty()) {
                return null;
            } else {
                return toVisit.removeLast();
            }
        };

        while((nextPos = removeLast.get()) != null) {
            UnloadedMember member = members.get(nextPos);
            if(member == null) {
                continue;
            }

            for(BlockPos neighborPos : member.connections) {
                if(visited.add(neighborPos)) {
                    toVisit.add(neighborPos);
                }
            }

            callback.accept(member);
        }
    }

    public static void update(LevelAccessor level, BlockPos startingPos) {
        ElectricalNetworkManager space = getInstance(level);
        // Dummy very stupid implementation: find source with the highest voltage and use that.
        // Then sum up the max amperage of all the sources combined and use that.

        // This doesn't account for components such as diodes or components that might change the voltage along the way
        final float[] totalAmps = {0};
        final float[] highestVoltage = {0};
        float totalProduction;
        float totalUsage;

        space.traverseAll(startingPos, source -> {
            if(source.isSource()) {
                if (source.generatedVoltage > highestVoltage[0]) {
                    highestVoltage[0] = source.generatedVoltage;
                }

                float generatedAmps = source.calcGeneratedAmps();
                source.ampsProvided = generatedAmps;
                totalAmps[0] += generatedAmps;
            }
        });
        totalProduction = totalAmps[0];

        // Calculate total consumption for all components and apply voltage
        totalAmps[0] = 0;

        space.traverseAll(startingPos, member -> {
            member.frequency = 0;
            member.voltage = highestVoltage[0];
            float consumedAmps = member.calcConsumedAmps();
            member.ampsConsumed = consumedAmps;
            if(!member.isSource()) {
                member.ampsProvided = 0;
            }
            totalAmps[0] += consumedAmps;
        });
        totalUsage = totalAmps[0];

        space.traverseAll(startingPos, member -> {
            if(space.level.isLoaded(member.pos) && space.level.getBlockEntity(member.pos) instanceof IElectric be) {
                ElectricData data = be.getElectricData();
                data.totalNetworkUsage = totalUsage;
                data.totalNetworkProduction = totalProduction;
                data.syncNextTick = true;
            }
        });

        space.setDirty();
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
