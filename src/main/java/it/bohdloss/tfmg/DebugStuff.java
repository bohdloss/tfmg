package it.bohdloss.tfmg;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class DebugStuff {
    private DebugStuff() {}

    @OnlyIn(Dist.CLIENT)
    public static void show(Object display) {
        String string = display.toString();
        if(Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(Component.literal(string), false);
        }
        TFMG.LOGGER.debug(display.toString());
    }

    @OnlyIn(Dist.CLIENT)
    public static void stackTrace() {
        String string = "";
        var elems = Thread.currentThread().getStackTrace();
        for(var elem : elems) {
            string += elem + "\n";
        }
        show(string);
    }
}
