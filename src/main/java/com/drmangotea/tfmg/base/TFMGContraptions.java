package com.drmangotea.tfmg.base;



import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.hammer.PumpjackContraption;
import com.simibubi.create.content.contraptions.ContraptionType;

public class TFMGContraptions {

        public static final ContraptionType
                PUMPJACK_CONTRAPTION = ContraptionType.register(TFMG.asResource("pumpjack").toString(), PumpjackContraption::new);

        public static void prepare() {}
}
