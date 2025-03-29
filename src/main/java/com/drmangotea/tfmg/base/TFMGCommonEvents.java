package com.drmangotea.tfmg.base;

import com.drmangotea.tfmg.TFMG;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TFMGCommonEvents {
    @SubscribeEvent
    public static void onLoadWorld(LevelEvent.Load event) {
        LevelAccessor world = event.getLevel();
        TFMG.NETWORK_MANAGER.onLoadWorld(world);
    }
    @SubscribeEvent
    public static void onUnloadWorld(LevelEvent.Unload event) {
        LevelAccessor world = event.getLevel();
        TFMG.NETWORK_MANAGER.onUnloadWorld(world);
    }
}
