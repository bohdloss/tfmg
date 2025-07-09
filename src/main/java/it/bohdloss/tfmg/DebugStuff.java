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
        Minecraft.getInstance().player.displayClientMessage(Component.literal(string), false);
    }
}
