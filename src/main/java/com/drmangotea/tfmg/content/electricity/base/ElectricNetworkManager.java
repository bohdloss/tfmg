package com.drmangotea.tfmg.content.electricity.base;

import com.drmangotea.tfmg.content.electricity.connection.cables.IHaveCables;
import com.drmangotea.tfmg.content.electricity.connection.cables.WireNetwork;
import net.minecraft.world.level.LevelAccessor;
import org.apache.http.impl.conn.Wire;

import java.util.HashMap;
import java.util.Map;

public class ElectricNetworkManager {

    public static Map<LevelAccessor, Map<Long, ElectricalNetwork>> networks = new HashMap<>();

    public void onLoadWorld(LevelAccessor world) {
        networks.put(world, new HashMap<>());
    }
    public void onUnloadWorld(LevelAccessor world) {
        networks.remove(world);
    }
    public ElectricalNetwork getOrCreateNetworkFor(IElectric be) {
        Long id = be.getData().getId();
        ElectricalNetwork network;
        Map<Long, ElectricalNetwork> map = networks.computeIfAbsent(be.getLevelAccessor(), $ -> new HashMap<>());

        if (!map.containsKey(id)) {
            network = new ElectricalNetwork(id);

            if(be instanceof IElectric) {
                network.add(be);
                be.setNetwork(be.getData().getId());
            }
            map.put(id, network);
        }
        network = map.get(id);
        return network;
    }
}
