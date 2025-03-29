package com.drmangotea.tfmg.content.electricity.measurement;

import com.drmangotea.tfmg.base.TFMGUtils;
import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;


public class VoltMeterBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {


    public LerpedFloat angle = LerpedFloat.angular();


    public float value = 0;

   // public int range = 500;

    public MeasureMode mode = MeasureMode.VOLTAGE;

    public VoltMeterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(10);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }


    @Override
    public void lazyTick() {
        super.lazyTick();
        //if(!level.isClientSide)
        //    return;
        BlockEntity beBehind = level.getBlockEntity(getBlockPos().relative(getBlockState().getValue(FACING).getOpposite()));

        if(beBehind instanceof IElectric be){
           // TFMG.LOGGER.debug("A "+ ElectricalNetwork.getCableCurrent(be));
           // TFMG.LOGGER.debug("id group "+be.getData().group.id);
           // TFMG.LOGGER.debug("resistance group "+be.getData().group.resistance);
            value = Math.min(getUnit(be), mode.defaultRange );

        } else value = 0;

    }
    public float getUnit(IElectric be){
        return switch (mode){
            case VOLTAGE,HIGH_VOLTAGE -> be.getData().getVoltage();
            case CURRENT -> be.getCurrent();
            case RESISTANCE,HIGH_RESISTANCE ->  be.resistance();
            case POWER -> be.powerGeneration() > 0 ? be.powerGeneration() : be.getPowerUsage();
        };
    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide)
            return;

        float value = (float) Math.abs(this.value) / getRange();
        if(value>1)
            value = 1;

        float targetAngle = Math.abs(value*180);

        angle.chase(Math.abs(targetAngle),0.05f, LerpedFloat.Chaser.EXP);
        angle.tickChaser();

    }


    public int getRange(){
        return mode.defaultRange;
    }

    @Override
    @SuppressWarnings("removal")
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        CreateLang.text(mode.displayName)
                .style(ChatFormatting.DARK_GRAY)
                .forGoggles(tooltip, 1);

        CreateLang.translate("goggles.gauge.value")
                .add(CreateLang.text(TFMGUtils.formatUnits(value,mode.unit)))
                .style(ChatFormatting.AQUA)
                .forGoggles(tooltip, 1);
        CreateLang.translate("goggles.voltmeter.range", mode.defaultRange)
                .style(ChatFormatting.DARK_AQUA)
                .forGoggles(tooltip, 1);

        return true;
    }

    public enum MeasureMode{

        VOLTAGE("Voltage","V",500),
        HIGH_VOLTAGE("Voltage (High)","V",10000),
        CURRENT("Current","A",16),
        RESISTANCE("Resistance","A",500),
        HIGH_RESISTANCE("Resistance (High)","Ω",500),
        POWER("Power","A",5000)


        ;
        public final String displayName;
        public final String unit;
        public final int defaultRange;

        MeasureMode(String displayName, String unit, int defaultRange){
            this.unit = unit;
            this.displayName = displayName;
            this.defaultRange = defaultRange;
        }

    }
}
