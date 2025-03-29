package com.drmangotea.tfmg.content.machinery.vat.base;


import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityDataPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class VatEvaluationPacket extends BlockEntityDataPacket<SmartBlockEntity> {


    public VatEvaluationPacket(BlockPos pos) {
        super(pos);
    }

    public VatEvaluationPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }
    @Override
    protected void writeData(FriendlyByteBuf buffer) {}

    @Override
    protected void handlePacket(SmartBlockEntity blockEntity) {

        if(blockEntity instanceof VatBlockEntity be) {
            be.evaluateNextTick =true;

        }

    }


}
