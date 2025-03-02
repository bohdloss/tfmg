package com.drmangotea.tfmg.base.events;

import com.drmangotea.tfmg.content.engines.engine_controller.EngineControllerClientHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import static com.jozufozu.flywheel.backend.Backend.isGameActive;

@EventBusSubscriber(Dist.CLIENT)
public class TFMGClientEvents {

	@SubscribeEvent
	public static void onTick(ClientTickEvent event) {
		if (!isGameActive())
			return;


		if (event.phase == Phase.START) {
			EngineControllerClientHandler.tick();

		}

	}
}
