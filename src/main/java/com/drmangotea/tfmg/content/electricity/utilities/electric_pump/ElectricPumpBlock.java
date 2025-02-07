package com.drmangotea.tfmg.content.electricity.utilities.electric_pump;

import com.drmangotea.tfmg.content.electricity.base.ConnectNeightborsPacket;
import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.drmangotea.tfmg.registry.TFMGPackets;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

public class ElectricPumpBlock extends PumpBlock  {


    public ElectricPumpBlock(Properties p_i48415_1_) {
        super(p_i48415_1_);
    }



    //@Override
    //public void onPlace(BlockState pState, Level level, BlockPos pos, BlockState pOldState, boolean pIsMoving) {
    //    withBlockEntityDo(level,pos, IElectric::onPlaced);
    //}

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    public boolean isSmallCog() {
        return false;
    }




    @Override
    public BlockEntityType<? extends PumpBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.ELECTRIC_PUMP.get();
    }
}
