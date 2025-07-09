package it.bohdloss.tfmg.content.decoration.pipes;

import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.GlassFluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.StraightPipeBlockEntity;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGPipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TFMGGlassPipeBlock extends GlassFluidPipeBlock {
    public final TFMGPipes.PipeMaterial pipeMaterial;

    public TFMGGlassPipeBlock(Properties properties, TFMGPipes.PipeMaterial pipeMaterial) {
        super(properties);
        this.pipeMaterial = pipeMaterial;
    }

    @Override
    public @NotNull BlockEntityType<? extends StraightPipeBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.GLASS_TFMG_PIPE.get();
    }

    @Override
    public BlockState toRegularPipe(LevelAccessor world, BlockPos pos, BlockState state) {
        Direction side = Direction.get(Direction.AxisDirection.POSITIVE, state.getValue(AXIS));
        Map<Direction, BooleanProperty> facingToPropertyMap = FluidPipeBlock.PROPERTY_BY_DIRECTION;
        return TFMGPipes.TFMG_PIPES.get(pipeMaterial).pipe().get()
                .updateBlockState(TFMGPipes.TFMG_PIPES.get(pipeMaterial).pipe().getDefaultState()
                        .setValue(facingToPropertyMap.get(side), true)
                        .setValue(facingToPropertyMap.get(side.getOpposite()), true), side, null, world, pos);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return TFMGPipes.TFMG_PIPES.get(pipeMaterial).pipe().asStack();
    }
}
