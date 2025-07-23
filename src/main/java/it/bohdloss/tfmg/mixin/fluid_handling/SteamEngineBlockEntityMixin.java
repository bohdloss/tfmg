package it.bohdloss.tfmg.mixin.fluid_handling;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SteamEngineBlockEntity.class)
public class SteamEngineBlockEntityMixin {
    @WrapOperation(method = "isValid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    boolean isValid(BlockState instance, Block block, Operation<Boolean> original) {
        return block instanceof FluidTankBlock;
    }
}
