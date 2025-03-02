package com.drmangotea.tfmg.content.engines.engine_controller.packets;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.content.engines.engine_controller.EngineControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class EngineControllerStopControllerPacket extends EngineControllerPacketBase {

	public EngineControllerStopControllerPacket(FriendlyByteBuf buffer) {
		super(buffer);
	}

	public EngineControllerStopControllerPacket(BlockPos lecternPos) {
		super(lecternPos);
	}

	@Override
	protected void handleController(ServerPlayer player, EngineControllerBlockEntity lectern) {
		TFMG.LOGGER.debug("Packet- Stop Using");
		lectern.tryStopUsing(player);
	}

	@Override
	protected void handleItem(ServerPlayer player, ItemStack heldItem) { }

}
