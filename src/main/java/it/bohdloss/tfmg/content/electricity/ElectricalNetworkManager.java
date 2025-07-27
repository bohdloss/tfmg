package it.bohdloss.tfmg.content.electricity;

import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.content.electricity.base.IElectric;
import net.createmod.catnip.levelWrappers.WorldHelper;
import net.minecraft.world.level.LevelAccessor;

import java.util.HashMap;
import java.util.Map;

public class ElectricalNetworkManager {

    public static Map<LevelAccessor, Map<Long, ElectricalNetwork>> networks = new HashMap<>();

    public void onLoadWorld(LevelAccessor world) {
        networks.put(world, new HashMap<>());
        TFMG.LOGGER.debug("Prepared Electrical Network Space for " + WorldHelper.getDimensionID(world));
    }

    public void onUnloadWorld(LevelAccessor world) {
        networks.remove(world);
        TFMG.LOGGER.debug("Removed Electrical Network Space for " + WorldHelper.getDimensionID(world));
    }

    public ElectricalNetwork getOrCreateNetworkFor(IElectric be) {
        Long id = be.getElectricalNetwork();
        ElectricalNetwork network;
        Map<Long, ElectricalNetwork> map = networks.computeIfAbsent(be.getLevel(), $ -> new HashMap<>());
        if(id == null) {
            return null;
        }

        if (!map.containsKey(id)) {
            network = new ElectricalNetwork();
            network.id = be.getElectricalNetwork();
            map.put(id, network);
        }
        network = map.get(id);
        return network;
    }
}
