package com.drmangotea.tfmg.content.engines.engine_controller;

import com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityDataPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class TransmissionRemovePacket extends BlockEntityDataPacket<SmartBlockEntity> {

    
    public TransmissionRemovePacket(BlockPos pos) {
        super(pos);
    }
    public TransmissionRemovePacket(FriendlyByteBuf buffer) {
        super(buffer);
    }
    
    @Override
    protected void writeData(FriendlyByteBuf buffer) {}

    @Override
    protected void handlePacket(SmartBlockEntity blockEntity) {

        if(blockEntity instanceof AbstractEngineBlockEntity be) {
            be.highestSignal = 0;
        }
    }
}