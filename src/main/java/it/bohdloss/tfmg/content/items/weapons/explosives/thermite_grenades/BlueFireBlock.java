package it.bohdloss.tfmg.content.items.weapons.explosives.thermite_grenades;

import com.mojang.serialization.MapCodec;
import it.bohdloss.tfmg.registry.TFMGColoredFires;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlueFireBlock extends FireBlock implements ICustomFire {
    public static final MapCodec<FireBlock> CODEC = simpleCodec(BlueFireBlock::new);

    public BlueFireBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull MapCodec<FireBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull BlockState getStateForPlacement(@NotNull BlockGetter level, @NotNull BlockPos pos) {
        return super.getStateForPlacement(level, pos);
    }

    @Override
    public Block provideFireBlock() {
        return TFMGColoredFires.BLUE_FIRE.get();
    }
}
