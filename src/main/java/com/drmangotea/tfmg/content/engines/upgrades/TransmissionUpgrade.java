package com.drmangotea.tfmg.content.engines.upgrades;

import com.drmangotea.tfmg.registry.TFMGItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;

import java.util.Optional;

public class TransmissionUpgrade extends EngineUpgrade{

    TransmissionState shift = TransmissionState.NEUTRAL;



    @Override
    public Optional<? extends EngineUpgrade> createUpgrade() {
        return Optional.of(new TransmissionUpgrade());
    }



    @Override
    public Item getItem() {
        return TFMGItems.TRANSMISSION.asItem();
    }

    public enum TransmissionState{
        REVERSE(0.9f,true),
        NEUTRAL(0),
        SHIFT_1(0.6f),
        SHIFT_2(0.7f),
        SHIFT_3(1.1f),
        SHIFT_4(1.4f),
        SHIFT_5(1.5f),
        SHIFT_6(1.9f)



        ;
        public final float value;
        public final boolean reverse;
        TransmissionState(float value){
            this(value,false);
        }
        TransmissionState(float value, boolean reverse){
            this.value = value;
            this.reverse = reverse;
        }
    }
}
