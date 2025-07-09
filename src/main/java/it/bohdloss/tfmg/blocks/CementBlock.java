package it.bohdloss.tfmg.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

public class CementBlock extends FallingBlock {
    public static final MapCodec<CementBlock> CODEC = simpleCodec(CementBlock::new);

    public CementBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }
}
