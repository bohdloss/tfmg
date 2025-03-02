package com.drmangotea.tfmg.content.engines.engine_controller.packets;

import com.drmangotea.tfmg.content.engines.engine_controller.EngineControllerBlockEntity;
import com.simibubi.create.content.redstone.link.controller.LecternControllerBlockEntity;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerServerHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class EngineControllerInputPacket extends EngineControllerPacketBase {

	private Collection<Integer> activatedButtons;
	private boolean press;

	public EngineControllerInputPacket(Collection<Integer> activatedButtons, boolean press) {
		this(activatedButtons, press, null);
	}

	public EngineControllerInputPacket(Collection<Integer> activatedButtons, boolean press, BlockPos lecternPos) {
		super(lecternPos);
		this.activatedButtons = activatedButtons;
		this.press = press;
	}

	public EngineControllerInputPacket(FriendlyByteBuf buffer) {
		super(buffer);
		activatedButtons = new ArrayList<>();
		press = buffer.readBoolean();
		int size = buffer.readVarInt();
		for (int i = 0; i < size; i++)
			activatedButtons.add(buffer.readVarInt());
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		super.write(buffer);
		buffer.writeBoolean(press);
		buffer.writeVarInt(activatedButtons.size());
		activatedButtons.forEach(buffer::writeVarInt);
	}

	@Override
	protected void handleController(ServerPlayer player, EngineControllerBlockEntity lectern) {
		if (lectern.isUsedBy(player))
			handleItem(player, lectern.getController());
	}

	@Override
	protected void handleItem(ServerPlayer player, ItemStack heldItem) {
		Level world = player.getCommandSenderWorld();
		UUID uniqueID = player.getUUID();
		BlockPos pos = player.blockPosition();

		if (player.isSpectator() && press)
			return;
		
		LinkedControllerServerHandler.receivePressed(world, pos, uniqueID, activatedButtons.stream()
			.map(i -> LinkedControllerItem.toFrequency(heldItem, i))
			.collect(Collectors.toList()), press);
	}

}
