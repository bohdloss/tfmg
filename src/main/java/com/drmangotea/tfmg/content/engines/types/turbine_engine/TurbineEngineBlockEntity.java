package com.drmangotea.tfmg.content.engines.types.turbine_engine;

import com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.base.EngineBlock;
import com.drmangotea.tfmg.content.engines.base.EngineComponentsInventory;
import com.drmangotea.tfmg.content.engines.base.EngineProperties;
import com.drmangotea.tfmg.content.engines.types.AbstractSmallEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.types.regular_engine.RegularEngineBlockEntity;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.drmangotea.tfmg.registry.TFMGTags;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.item.SmartInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

import static com.drmangotea.tfmg.content.engines.base.EngineBlock.ENGINE_STATE;
import static com.drmangotea.tfmg.content.engines.base.EngineBlock.EngineState.*;
import static com.drmangotea.tfmg.content.engines.base.EngineBlock.SHAFT_FACING;
import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class TurbineEngineBlockEntity extends AbstractSmallEngineBlockEntity {

    public SmartInventory turbineBladeInventory;

    public TurbineEngineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        componentsInventory = new EngineComponentsInventory(this, EngineProperties.turbineEngineComponents());
        turbineBladeInventory = createInventory();
    }

    public SmartInventory createInventory() {
        return new SmartInventory(4, this)
                .withMaxStackSize(1)
                .whenContentsChanged(this::onInventoryChanged);
    }
    public void updateInventory() {
        turbineBladeInventory = createInventory();
    }
    @Override
    public boolean canWork() {

        if (level.getBlockEntity(controller) instanceof TurbineEngineBlockEntity controller) {

            for (Long position : controller.getAllEngines()) {

                if (level.getBlockEntity(BlockPos.of(position)) instanceof TurbineEngineBlockEntity be) {
                    for(int i =0;i<be.turbineBladeInventory.getSlots();i++){
                        if(be.turbineBladeInventory.getItem(i).isEmpty()) {
                            return false;
                        }
                    }
                }
            }
            return super.canWork();
        }
        return false;
    }
    private void onInventoryChanged(int integer) {
        updateRotation();
        sendData();
        setChanged();
    }

    @Override
    public boolean insertItem(ItemStack itemStack, boolean shifting, Player player, InteractionHand hand) {



        if (itemStack.is(TFMGItems.SCREWDRIVER.get())) {
            if (!turbineBladeInventory.isEmpty()) {
                for (int i = 0; i < turbineBladeInventory.getSlots(); i++) {
                    if (!turbineBladeInventory.getItem(i).isEmpty()) {
                        dropItem(turbineBladeInventory.getItem(i));
                        turbineBladeInventory.setItem(i, ItemStack.EMPTY);
                        playRemovalSound();
                        updateRotation();
                        setChanged();
                        sendData();
                        return true;
                    }
                }
            }
            for (int i = componentsInventory.components.size() - 1; i >= 0; i--) {
                if (!componentsInventory.getItem(i).isEmpty()) {
                    dropItem(componentsInventory.getItem(i));
                    componentsInventory.setItem(i, ItemStack.EMPTY);
                    playRemovalSound();
                    updateRotation();
                    setChanged();
                    sendData();
                    return true;
                }
            }

        }
        if (hasAllComponents())
            if (itemStack.is(TFMGItems.TURBINE_BLADE.get())) {
                for (int i = turbineBladeInventory.getSlots() - 1; i >= 0; i--) {
                    if (turbineBladeInventory.getItem(i).isEmpty()) {
                        ItemStack toInsert = itemStack.copy();
                        toInsert.setCount(1);
                        turbineBladeInventory.setItem(i, toInsert);
                        itemStack.shrink(1);
                        playInsertionSound();
                        updateRotation();
                        setChanged();
                        sendData();
                        return true;
                    }
                }
            }
        if (nextComponent().test(itemStack) && !isController()) {

            if (level.getBlockEntity(controller) instanceof AbstractSmallEngineBlockEntity be) {
                return be.insertItem(itemStack, shifting, player, hand);
            }

        }

        return super.insertItem(itemStack, shifting, player, hand);
    }

    @Override
    public float getGeneratedSpeed() {

        float speed;

        if (hasLevel())

            if (level.getBlockEntity(controller) instanceof AbstractEngineBlockEntity controller) {
                if (controller.fuelTank.isEmpty())
                    return 0;
                if (!controller.canWork())
                    return 0;
                speed = rpm / 40;
                if (reverse)
                    speed = speed * -1;

                return convertToDirection(Math.min((int) speed, 256), getBlockState().getValue(HORIZONTAL_FACING));
            }
        return 0;
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);

        compound.put("TurbineBlades", turbineBladeInventory.serializeNBT());
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        turbineBladeInventory.deserializeNBT(compound.getCompound("TurbineBlades"));
    }

    public void setBlockStates(AbstractSmallEngineBlockEntity be, BlockPos last) {
        if(last!=null){
            level.setBlock(last, level.getBlockState(last).setValue(ENGINE_STATE, BACK), 2);
            return;
        }

        if (be.isController()) {
            level.setBlock(be.getBlockPos(), be.getBlockState().setValue(ENGINE_STATE, SHAFT), 2);
        } else {
            level.setBlock(be.getBlockPos(), be.getBlockState().setValue(SHAFT_FACING, getBlockState().getValue(SHAFT_FACING).getOpposite()).setValue(ENGINE_STATE, NORMAL), 2);
        }

    }

    @Override
    public List<TagKey<Fluid>> getSupportedFuels() {
        return List.of(TFMGTags.TFMGFluidTags.KEROSENE.tag);
    }

    @Override
    public float efficiencyModifier() {
        return getFuelType().getEfficiency() * getUpgradeEfficiencyModifier();
    }

    @Override
    public float speedModifier() {
        return getFuelType().getSpeed() * getUpgradeSpeedModifier();
    }

    @Override
    public float torqueModifier() {
        return getFuelType().getStress() * getUpgradeTorqueModifier();
    }


    @Override
    public String engineId() {
        return "turbine";
    }
}
