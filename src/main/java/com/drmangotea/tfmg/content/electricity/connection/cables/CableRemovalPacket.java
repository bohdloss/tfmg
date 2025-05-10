package com.drmangotea.tfmg.content.electricity.connection.cables;


import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityDataPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class CableRemovalPacket extends BlockEntityDataPacket<SmartBlockEntity> {


    public CableRemovalPacket(BlockPos pos) {
        super(pos);
    }
    public CableRemovalPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    protected void writeData(FriendlyByteBuf buffer) {}

    @Override
    protected void handlePacket(SmartBlockEntity blockEntity) {

        if(blockEntity instanceof CableConnectorBlockEntity be) {
           // be.removeConnections(true);
        }
    }
}