package it.bohdloss.tfmg.content.electricity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IHaveContext {
    @Nullable Level getLevel();
    @Nullable BlockPos getBlockPos();
    @Nullable BlockState getBlockState();
    void sendData();
}
