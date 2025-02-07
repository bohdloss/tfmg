package com.drmangotea.tfmg.ponder;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.registry.TFMGFluids;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.simibubi.create.foundation.ponder.PonderTag;
import net.minecraft.resources.ResourceLocation;

public class TFMGPonderTag extends PonderTag {
    public static final PonderTag TFMG_MACHINERY = create("tfmg_machinery")
            .defaultLang("TFMG Machinery", "Misc machines from Create: The Factory Must Grow")
            .item(TFMGItems.STEEL_MECHANISM.get(), true, false).addToIndex();

    public static final PonderTag METALLURGY = create("metallurgy")
            .defaultLang("Metallurgy", "Machines related to metal processing")
            .item(TFMGItems.STEEL_INGOT.get(), true, false).addToIndex();



        public TFMGPonderTag(ResourceLocation id) {
            super(id);
        }

        private static PonderTag create(String id) {
            return new PonderTag(TFMG.asResource(id));
        }
    }

