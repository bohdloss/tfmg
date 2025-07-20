package it.bohdloss.tfmg.content.machinery.metallurgy.blast_furnace;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import it.bohdloss.tfmg.content.machinery.metallurgy.coke_oven.CokeOvenBlockEntity;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlastFurnaceHatchBlock extends Block implements IBE<BlastFurnaceHatchBlockEntity>, IWrenchable {
    public BlastFurnaceHatchBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof BlastFurnaceHatchBlockEntity blastFurnaceHatchBlockEntity))
                return;

            ItemHelper.dropContents(world, pos, blastFurnaceHatchBlockEntity.inventory.getHandler());
            world.removeBlockEntity(pos);
        }
    }

    @Override
    public Class<BlastFurnaceHatchBlockEntity> getBlockEntityClass() {
        return BlastFurnaceHatchBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BlastFurnaceHatchBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.BLAST_FURNACE_HATCH.get();
    }
}
