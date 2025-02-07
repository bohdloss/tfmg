package com.drmangotea.tfmg.content.electricity.generators;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.config.TFMGConfigs;
import com.drmangotea.tfmg.content.electricity.base.KineticElectricBlockEntity;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GeneratorBlockEntity extends KineticElectricBlockEntity  {


    public GeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    @Override
    public int voltageGeneration() {
        return (int) Math.min(255,generation());
    }

    @Override
    public int powerGeneration() {
        return generation()*40;
    }

    @Override
    public void updateNetwork() {
        super.updateNetwork();
    }

    @Override
    public float calculateStressApplied() {
        if(getData().voltageSupply == 0)
            return 0;

        if(getNetworkResistance() ==0)
            return super.calculateStressApplied();

        return (super.calculateStressApplied()*getGeneratorLoad()*0.0001f);
    }



    @Override
    public void onNetworkChanged(int oldVoltage, int oldPower) {
        super.onNetworkChanged(oldVoltage, oldPower);
        updateStress();
        sendStuff();
    }

    public void updateStress(){
        if(getOrCreateNetwork() != null) {
            getOrCreateNetwork().remove(this);
            getOrCreateNetwork().add(this);
        }
    }

    public int generation() {
        float modifier = TFMGConfigs.common().machines.smallGeneratorFeModifier.getF();

        return  (int) (((Math.log(Math.abs(getSpeed()))/Math.log(1.026))-22)*modifier*3.5f);
    }




}
