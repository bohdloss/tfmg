package it.bohdloss.tfmg.content.electricity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.content.electricity.base.ElectricData;
import net.createmod.catnip.levelWrappers.WorldHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@EventBusSubscriber
public class ElectricalNetworkManager extends SavedData {
    public static final Map<LevelAccessor, ElectricalNetworkManager> spaces = new HashMap<>();

    public static final Codec<ElectricalNetworkManager> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.list(ElectricalNetwork.CODEC).fieldOf("Networks").forGetter(x -> x.networks.values().stream().toList())
    ).apply(inst, ElectricalNetworkManager::fromCodec));

    public final Map<Long, ElectricalNetwork> networks = new HashMap<>();
    public ServerLevel level;

    private ElectricalNetworkManager() { }

    private ElectricalNetworkManager(LevelAccessor level) {
        this.level = level instanceof ServerLevel sLevel ? sLevel : null;
    }

    public static ElectricalNetworkManager fromCodec(List<ElectricalNetwork> networks) {
        ElectricalNetworkManager self = new ElectricalNetworkManager();
        for(ElectricalNetwork network : networks) {
            if(network.members.isEmpty()) {
                continue;
            }
            network.owner = self;
            self.networks.put(network.id, network);
        }
        return self;
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

//    /// Generates a new universally unique component id and assigns it
//    public static void generateComponentId(ElectricData data) {
//        if(data.isClient()) {
//            throw new IllegalStateException("Uninitialized level or client level");
//        }
//
//        Level level = data.getLevel();
//        RandomSource random = level.getRandom();
//        ElectricalNetworkManager space = spaces.computeIfAbsent(data.getLevel(), $ -> new ElectricalNetworkManager());
//
//        long id = random.nextLong();
//        while(space.allComponents.contains(id)) {
//            id = random.nextLong();
//        }
//
//        data.id = id;
//    }

    /// Finds an electrical network given its level and id
    public static ElectricalNetwork getNetworkById(LevelAccessor level, long id) {
        if(level == null || level.isClientSide()) {
            throw new IllegalStateException("Uninitialized level or client level");
        }

        ElectricalNetworkManager space = spaces.computeIfAbsent(level, ElectricalNetworkManager::new);
        return space.networks.get(id);
    }

    /// Creates a new electrical network with a universally unique id
    public static ElectricalNetwork createNewNetwork(LevelAccessor level) {
        if(level == null || level.isClientSide()) {
            throw new IllegalStateException("Uninitialized level or client level");
        }

        RandomSource random = level.getRandom();
        ElectricalNetworkManager space = spaces.computeIfAbsent(level, ElectricalNetworkManager::new);

        long id = random.nextLong();
        while(space.networks.containsKey(id)) {
            id = random.nextLong();
        }
        ElectricalNetwork network = new ElectricalNetwork(id);
        network.owner = space;
        space.networks.put(id, network);
        space.setDirty();

        return network;
    }

    /// Searches for the network this component belongs to given its id, prioritizing search in the network with the given id
    public static ElectricalNetwork findFor(ElectricData data, Long networkId) {
        if(data.isClient()) {
            throw new IllegalStateException("Uninitialized level or client level");
        }

        ElectricalNetworkManager space = spaces.computeIfAbsent(data.getLevel(), ElectricalNetworkManager::new);

        // Try with cache
        if(networkId != null) {
            ElectricalNetwork network = space.networks.get(networkId);
            if(network != null && network.contains(data.getBlockPos())) {
                return network;
            }
        }

        // Slow search on miss
        for(ElectricalNetwork network : space.networks.values()) {
            if(network.contains(data.getBlockPos())) {
                return network;
            }
        }

        return null; // You should create a new network
    }
}
