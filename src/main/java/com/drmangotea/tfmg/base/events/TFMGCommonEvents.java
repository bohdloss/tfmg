package com.drmangotea.tfmg.base.events;


import com.drmangotea.tfmg.TFMG;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TFMGCommonEvents {


    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        TFMG.DEPOSITS.playerLogin(player);
    }
    @SubscribeEvent
    public static void onLoadWorld(LevelEvent.Load event) {
        LevelAccessor world = event.getLevel();
        TFMG.DEPOSITS.levelLoaded(world);
    }
}
