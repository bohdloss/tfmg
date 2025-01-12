package com.drmangotea.tfmg.content.electricity.base;


import com.drmangotea.tfmg.content.electricity.utilities.diode.ElectricDiodeBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityDataPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class UpdateInFrontPacket extends BlockEntityDataPacket<SmartBlockEntity> {

    public UpdateInFrontPacket(BlockPos pos) {
        super(pos);


    }
    public UpdateInFrontPacket(FriendlyByteBuf buffer) {
        super(buffer);


    }

    @Override
    protected void writeData(FriendlyByteBuf buffer) {}

    @Override
    protected void handlePacket(SmartBlockEntity blockEntity) {

        if(blockEntity instanceof ElectricDiodeBlockEntity be) {
            be.updateInFrontNextTick();
        }

    }


}
