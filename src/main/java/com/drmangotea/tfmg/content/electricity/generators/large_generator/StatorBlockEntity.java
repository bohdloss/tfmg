package com.drmangotea.tfmg.content.electricity.generators.large_generator;

import com.drmangotea.tfmg.content.electricity.base.ElectricBlockEntity;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;


public class StatorBlockEntity extends ElectricBlockEntity implements IHaveGoggleInformation {

    public BlockPos rotor=null;
    public StatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public void setRotor(RotorBlockEntity be){
        rotor = be.getBlockPos();
    }
    @Override
    public void lazyTick() {
        super.lazyTick();
        if(rotor!=null)
            if(!(level.getBlockEntity(rotor) instanceof RotorBlockEntity))
                rotor = null;


    }
}
