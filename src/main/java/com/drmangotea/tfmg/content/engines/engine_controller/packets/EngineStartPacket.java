package com.drmangotea.tfmg.content.engines.engine_controller.packets;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.content.engines.engine_controller.EngineControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;

public class EngineStartPacket extends EngineControllerPacketBase {





    public EngineStartPacket(BlockPos controllerPos) {
        super(controllerPos);
    }

    public EngineStartPacket(FriendlyByteBuf buffer) {
        super(buffer);

    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        super.write(buffer);
    }

    @Override
    protected void handleController(ServerPlayer player, EngineControllerBlockEntity controller) {
        TFMG.LOGGER.debug("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        controller.toggleEngine();
        //controller.engineStarted = !controller.engineStarted;
        //controller.accelerationRate = controller.engineStarted ? 4 : 0;
        //controller.sendData();
        //controller.setChanged();

    }
}
