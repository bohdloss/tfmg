package it.bohdloss.tfmg.mixin;

import it.bohdloss.tfmg.content.items.weapons.explosives.thermite_grenades.ICustomFire;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin implements ICustomFire {
    @Shadow protected abstract BlockState getStateForPlacement(BlockGetter level, BlockPos pos);

    @Inject(at = @At("HEAD"), method = "getStateWithAge", cancellable = true)
    void getStateWithAge(LevelAccessor level, BlockPos pos, int age, CallbackInfoReturnable<BlockState> cir) {
        Block fireBlock = tfmg$provideFireBlock();
        if(fireBlock != null) {
            BlockState blockstate = this.getStateForPlacement(level, pos);
            BlockState ret = blockstate.is(fireBlock) ? blockstate.setValue(FireBlock.AGE, Integer.valueOf(age)) : blockstate;
            cir.setReturnValue(ret);
        }
    }

    @Override
    public Block tfmg$provideFireBlock() {
        return Blocks.FIRE;
    }
}
