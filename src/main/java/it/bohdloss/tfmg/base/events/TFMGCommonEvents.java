package it.bohdloss.tfmg.base.events;

import it.bohdloss.tfmg.TFMG;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber
public class TFMGCommonEvents {
    @SubscribeEvent
    public static void onLoadWorld(LevelEvent.Load event) {
        LevelAccessor world = event.getLevel();
        TFMG.ELECTRICITY_MANAGER.onLoadWorld(world);
    }

    @SubscribeEvent
    public static void onUnloadWorld(LevelEvent.Unload event) {
        LevelAccessor world = event.getLevel();
        TFMG.ELECTRICITY_MANAGER.onUnloadWorld(world);
    }
}
