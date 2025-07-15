package it.bohdloss.tfmg.mixin_interfaces;

import net.minecraft.world.entity.player.Player;

public interface ILockablePipe {
    boolean tfmg$isPipeLocked();
    void tfmg$setPipeLocked(Player player, boolean locked);
    boolean tfmg$canPipeBeLocked();
}
