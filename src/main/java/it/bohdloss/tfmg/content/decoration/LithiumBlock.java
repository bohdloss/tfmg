package it.bohdloss.tfmg.content.decoration;

import it.bohdloss.tfmg.content.items.weapons.LithiumSpark;
import it.bohdloss.tfmg.registry.TFMGEntityTypes;
import com.simibubi.create.Create;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

public class LithiumBlock extends Block {
    public LithiumBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState blockState) {
        return true;
    }

    @Override
    public void randomTick(@NotNull BlockState blockState, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource randomSource) {
        super.randomTick(blockState, level, pos, randomSource);

        for(Direction direction : Direction.values()){
            var fluidState = level.getFluidState(pos.relative(direction));
            if(fluidState.is(Fluids.WATER) || fluidState.is(Fluids.FLOWING_WATER) || level.isRainingAt(pos.above())) {
                for (int i = 0; i < 12; i++) {
                    float x = randomSource.nextFloat() * 360.0f;
                    float y = randomSource.nextFloat() * 360.0f;
                    float z = randomSource.nextFloat() * 360.0f;
                    LithiumSpark spark = TFMGEntityTypes.LITHIUM_SPARK.create(level);
                    spark.moveTo(pos.getX(), pos.getY() + 0.5, pos.getZ());

                    float f = -Mth.sin(y * ((float) Math.PI / 180F)) * Mth.cos(x * ((float) Math.PI / 180F));
                    float f1 = -Mth.sin((x + z) * ((float) Math.PI / 180F));
                    float f2 = Mth.cos(y * ((float) Math.PI / 180F)) * Mth.cos(x * ((float) Math.PI / 180F));
                    spark.shoot(f, f1, f2, 0.3f, 1);
                    level.addFreshEntity(spark);
                }
                level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 1, Level.ExplosionInteraction.NONE);

                level.destroyBlock(pos,false);
                break;
            }

        }


    }
}
