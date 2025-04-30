package com.drmangotea.tfmg.content.machinery.vat.electrode_holder;

import com.drmangotea.tfmg.config.TFMGConfigs;
import com.drmangotea.tfmg.content.electricity.base.ElectricBlockEntity;
import com.drmangotea.tfmg.content.machinery.vat.base.IVatMachine;
import com.drmangotea.tfmg.content.machinery.vat.base.VatBlock;
import com.drmangotea.tfmg.content.machinery.vat.base.VatBlockEntity;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.drmangotea.tfmg.registry.TFMGPartialModels;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Objects;

public class ElectrodeHolderBlockEntity extends ElectricBlockEntity implements IVatMachine {

    ElectrodeType electrodeType = ElectrodeType.NONE;
    boolean isTallEnough=true;
    public ElectrodeHolderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    @Override
    public boolean hasElectricitySlot(Direction direction) {
        return direction == Direction.UP;
    }


    public boolean setElectrode(ItemStack modeItem, boolean simulate) {
        for (ElectrodeType type : ElectrodeType.values()) {
            if (type.item.is(modeItem.getItem())) {
                if (!simulate) {
                    electrodeType = type;
                } else return true;
            }
        }
        if (!simulate&&hasLevel())
            VatBlock.updateVatState(getBlockState(), getLevel(), getBlockPos().relative(Direction.DOWN));
        sendData();
        return false;
    }


    @Override
    public float resistance() {

        if(electrodeType !=ElectrodeType.NONE){
            if(electrodeType == ElectrodeType.GRAPHITE){
                return 5;
            }else {
                return 20;
            }

        }

        return 0;
    }

    public boolean setElectrode(String name, boolean simulate) {
        for (ElectrodeType type : ElectrodeType.values()) {
            if (Objects.equals(type.name, name)) {
                if (!simulate) {
                    electrodeType = type;
                } else return true;
            }
        }
        if (!simulate&&hasLevel())
            VatBlock.updateVatState(getBlockState(), getLevel(), getBlockPos().relative(Direction.DOWN));
        sendData();
        return false;
    }

    @Override
    public void onNetworkChanged(int oldVoltage, int oldPower) {
        super.onNetworkChanged(oldVoltage, oldPower);
        VatBlock.updateVatState(getBlockState(), level, getBlockPos().relative(Direction.DOWN));
    }

    boolean isSuperheated(){
        return electrodeType == ElectrodeType.GRAPHITE && getCurrent() >= TFMGConfigs.common().machines.graphiteElectrodeCurrent.get();
    }
    boolean isOperational(){
        return getCurrent() >= TFMGConfigs.common().machines.electrolysisMinimumCurrent.get();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos()).setMinY(getBlockPos().getY()-2);
    }
    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        for (ElectrodeType electrode : ElectrodeType.values()) {
            if (electrode == electrodeType) {
                compound.putString("Electrode", electrode.name);
            }
        }
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        setElectrode(compound.getString("Electrode"),false);

    }
    @Override
    public String getOperationId() {
        return switch (electrodeType){

            case NONE -> "";
            case COPPER, ZINC -> isOperational() ? "tfmg:electrode" : "";
            case GRAPHITE -> isSuperheated() ? "tfmg:graphite_electrode" : isOperational() ? "tfmg:electrode" : "";
        };
    }

    @Override
    public int getWorkPercentage() {
        return (getPowerUsage()/5000)*100;
    }

    @Override
    public void vatUpdated(VatBlockEntity be) {
        IVatMachine.super.vatUpdated(be);
    }


    enum ElectrodeType {

        NONE("none", ItemStack.EMPTY, null),
        COPPER("copper", TFMGItems.COPPER_ELECTRODE.asStack(), TFMGPartialModels.COPPER_ELECTRODE),
        ZINC("zinc", TFMGItems.ZINC_ELECTRODE.asStack(), TFMGPartialModels.ZINC_ELECTRODE),
        GRAPHITE("graphite", TFMGItems.GRAPHITE_ELECTRODE.asStack(), TFMGPartialModels.GRAPHITE_ELECTRODE)

        ;

        public final String name;
        public final ItemStack item;
        public final PartialModel model;

        ElectrodeType(String name, ItemStack stack, PartialModel model) {
            this.name = name;
            this.item = stack;
            this.model = model;
        }

    }
}
