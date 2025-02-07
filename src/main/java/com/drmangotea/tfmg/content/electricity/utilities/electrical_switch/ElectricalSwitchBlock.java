package com.drmangotea.tfmg.content.electricity.utilities.electrical_switch;

import com.drmangotea.tfmg.base.TFMGHorizontalDirectionalBlock;
import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.drmangotea.tfmg.registry.TFMGPackets;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

public class ElectricalSwitchBlock extends TFMGHorizontalDirectionalBlock implements IBE<ElectricalSwitchBlockEntity> {
    public ElectricalSwitchBlock(Properties p_54120_) {
        super(p_54120_);
    }
    @Override
    public void onPlace(BlockState pState, Level level, BlockPos pos, BlockState pOldState, boolean pIsMoving) {
        withBlockEntityDo(level,pos, IElectric::onPlaced);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
    }




        @Override
    public Class<ElectricalSwitchBlockEntity> getBlockEntityClass() {
        return ElectricalSwitchBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ElectricalSwitchBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.ELECTRICAL_SWITCH.get();
    }
}
