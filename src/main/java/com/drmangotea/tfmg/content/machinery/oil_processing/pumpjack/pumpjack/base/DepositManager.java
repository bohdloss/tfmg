package com.drmangotea.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.base;


import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public class DepositManager {

    public DepositSavedData depositData;

    public void playerLogin(Player player) {
        loadData(player.getServer());
    }

    public void levelLoaded(LevelAccessor level) {
        loadData(level.getServer());
    }

    public void loadData(MinecraftServer server){
        if (depositData != null)
            return;
        depositData = DepositSavedData.load(server);

    }
}
