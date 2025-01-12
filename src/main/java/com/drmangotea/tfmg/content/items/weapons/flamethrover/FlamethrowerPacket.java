package com.drmangotea.tfmg.content.items.weapons.flamethrover;


import com.drmangotea.tfmg.TFMGClient;
import com.simibubi.create.content.equipment.zapper.ShootGadgetPacket;
import com.simibubi.create.content.equipment.zapper.ShootableGadgetRenderHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FlamethrowerPacket extends ShootGadgetPacket {



	public FlamethrowerPacket(Vec3 location, InteractionHand hand, boolean self) {
		super(location, hand, self);
	}

	public FlamethrowerPacket(FriendlyByteBuf buffer) {
		super(buffer);
	}

	@Override
	protected void readAdditional(FriendlyByteBuf buffer) {
	}

	@Override
	protected void writeAdditional(FriendlyByteBuf buffer) {
	}


	@Override
	@OnlyIn(Dist.CLIENT)
	protected void handleAdditional() {

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected ShootableGadgetRenderHandler getHandler() {
		return TFMGClient.FLAMETHROWER_RENDER_HANDLER;
	}

}
