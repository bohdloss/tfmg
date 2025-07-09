package it.bohdloss.tfmg.content.decoration.pipes;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGPipes;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TFMGEncasedPipeBlock extends EncasedPipeBlock {
    public final TFMGPipes.PipeMaterial pipeMaterial;

    public TFMGEncasedPipeBlock(Properties properties, Supplier<Block> casing, TFMGPipes.PipeMaterial pipeMaterial) {
        super(properties, casing);
        this.pipeMaterial = pipeMaterial;
    }

    @Override
    public @NotNull BlockEntityType<? extends FluidPipeBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.ENCASED_TFMG_PIPE.get();
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return TFMGPipes.TFMG_PIPES.get(pipeMaterial).pipe().asStack();
    }

    // Taken from Create
    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (world.isClientSide)
            return InteractionResult.SUCCESS;

        context.getLevel()
                .levelEvent(2001, context.getClickedPos(), Block.getId(state));
        BlockState equivalentPipe = transferSixWayProperties(state, TFMGPipes.TFMG_PIPES.get(pipeMaterial).pipe().getDefaultState());

        Direction firstFound = Direction.UP;
        for (Direction d : Iterate.directions)
            if (state.getValue(FACING_TO_PROPERTY_MAP.get(d))) {
                firstFound = d;
                break;
            }

        FluidTransportBehaviour.cacheFlows(world, pos);
        world.setBlockAndUpdate(pos, TFMGPipes.TFMG_PIPES.get(pipeMaterial).pipe().get()
                .updateBlockState(equivalentPipe, firstFound, null, world, pos));
        FluidTransportBehaviour.loadFlows(world, pos);
        return InteractionResult.SUCCESS;
    }
}
