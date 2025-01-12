package com.drmangotea.tfmg.content.decoration.pipes;

import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import java.util.function.Supplier;

public class TFMGEncasedPipeBlock extends EncasedPipeBlock {

    public final TFMGPipes.PipeMaterial material;
    public TFMGEncasedPipeBlock(Properties p_i48339_1_, Supplier<Block> casing, TFMGPipes.PipeMaterial material) {
        super(p_i48339_1_, casing);
        this.material = material;
    }
    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return TFMGPipes.TFMG_PIPES.get(material).get(0).asStack();
    }
    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (world.isClientSide)
            return InteractionResult.SUCCESS;

        context.getLevel()
                .levelEvent(2001, context.getClickedPos(), Block.getId(state));
        BlockState equivalentPipe = transferSixWayProperties(state, TFMGPipes.TFMG_PIPES.get(material).get(0).getDefaultState());

        Direction firstFound = Direction.UP;
        for (Direction d : Iterate.directions)
            if (state.getValue(FACING_TO_PROPERTY_MAP.get(d))) {
                firstFound = d;
                break;
            }
        FluidTransportBehaviour.cacheFlows(world, pos);
        world.setBlockAndUpdate(pos, ((TFMGPipeBlock)TFMGPipes.TFMG_PIPES.get(material).get(0).get())
                .updateBlockState(equivalentPipe, firstFound, null, world, pos));
        FluidTransportBehaviour.loadFlows(world, pos);
        return InteractionResult.SUCCESS;
    }
    @Override
    public Class<FluidPipeBlockEntity> getBlockEntityClass() {
        return FluidPipeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FluidPipeBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.ENCASED_TFMG_PIPE.get();
    }
}
