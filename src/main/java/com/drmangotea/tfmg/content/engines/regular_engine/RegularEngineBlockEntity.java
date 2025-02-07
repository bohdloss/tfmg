package com.drmangotea.tfmg.content.engines.regular_engine;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.drmangotea.tfmg.content.engines.base.EngineProperties.*;
import static com.drmangotea.tfmg.content.engines.regular_engine.RegularEngineBlock.EXTENDED;
import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class RegularEngineBlockEntity extends AbstractEngineBlockEntity {


    public EngineType type = EngineType.I;

    public SmartInventory pistonInventory;

    public RegularEngineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        pistonInventory = createInventory();

    }

    public void updateInventory(){
        pistonInventory = createInventory();
    }

    public SmartInventory createInventory(){
        return new SmartInventory(type.pistons.size(),this)
                .withMaxStackSize(1)
                .whenContentsChanged(this::onInventoryChanged)
                ;
    }

    private void onInventoryChanged(int integer) {
        updateRotation();
        sendData();
        setChanged();
    }

    @Override
    public boolean canWork() {

        for(int i =0;i<pistonInventory.getSlots();i++){
            if(pistonInventory.getStackInSlot(i).isEmpty())
                return false;
        }

        return super.canWork();
    }

    @Override
    public boolean insertItem(ItemStack itemStack, boolean shifting, Player player, InteractionHand hand) {


        if(itemStack.is(AllItems.EMPTY_SCHEMATIC.get())){
            boolean next = false;

            if(type == EngineType.BOXER){
                updateEngineType(EngineType.I);
                AllSoundEvents.CONFIRM.play(level, null, getBlockPos(), 1, 1);
                return true;
            }
            for(EngineType engineType : EngineType.values()){
                if(next){
                    updateEngineType(engineType);
                    AllSoundEvents.CONFIRM.play(level, null, getBlockPos(), 1, 1);
                    return true;
                }
                if(engineType == type){
                    next = true;
                }
            }
        }
        if(hasAllComponents())
            if(itemStack.is(TFMGItems.ENGINE_CYLINDER.get())){
                    for (int i = pistonInventory.getSlots() - 1; i >= 0; i--) {
                        if (pistonInventory.getItem(i).isEmpty()) {
                            pistonInventory.setItem(i,new ItemStack(itemStack.getItem(),1));
                            itemStack.shrink(1);
                            playInsertionSound();
                            setChanged();
                            sendData();
                            return true;
                        }
                    }
            }

        if (itemStack.is(TFMGItems.SCREWDRIVER.get())) {
            if(!pistonInventory.isEmpty()){
                for (int i = pistonInventory.getSlots()-1; i >= 0; i--) {
                    if (!pistonInventory.getItem(i).isEmpty()) {
                        dropItem(pistonInventory.getItem(i));
                        pistonInventory.setItem(i, ItemStack.EMPTY);
                        playRemovalSound();
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
                    setChanged();
                    sendData();
                    return true;
                }
            }

        }
        if (nextComponent().test(itemStack)&&!isController()) {

            if(level.getBlockEntity(controller) instanceof AbstractEngineBlockEntity be){
                return be.insertItem(itemStack,shifting,player,hand);
            }

        }

        return super.insertItem(itemStack, shifting,player, hand);
    }



    public boolean updateEngineType(EngineType newType){

        Direction updateDirection = getBlockState().getValue(HORIZONTAL_FACING).getAxis() == Direction.Axis.X ? Direction.WEST : Direction.NORTH;
        if (level.getBlockEntity(getBlockPos().relative(updateDirection)) instanceof RegularEngineBlockEntity be) {
            return be.updateEngineType(newType);
        }
        for (int i = 0; i <= engineLength(); i++) {
            BlockPos pos = getBlockPos().relative(updateDirection.getOpposite(), i);
            if(level.getBlockEntity(pos) instanceof RegularEngineBlockEntity be){
                //be.type = EngineType.I;
                if(!be.pistonInventory.isEmpty())
                    return false;
            }
        }
        for (int i = 0; i <= engineLength(); i++) {
            BlockPos pos = getBlockPos().relative(updateDirection.getOpposite(), i);
            if(level.getBlockEntity(pos) instanceof RegularEngineBlockEntity be){
                be.type = newType;
                be.updateInventory();
                level.setBlockAndUpdate(pos,be.getBlockState().setValue(EXTENDED, newType == EngineType.I||newType == EngineType.U));
            }
        }

        return true;
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putString("Type", type.name);

        compound.put("Cylinders", pistonInventory.serializeNBT());

    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        for(EngineType engineType : EngineType.values()){
            if(engineType.name.matches(compound.getString("Type"))){
                type = engineType;
                break;
            }
        }

        //TFMG.LOGGER.debug(compound.getString("Type"));
//
        //type = EngineType.V;
        //type = EngineType.valueOf(compound.getString("Type"));
//
        //TFMG.LOGGER.debug(compound.getString("TypeE"));

        pistonInventory.deserializeNBT(compound.getCompound("Cylinders"));

    }

    @Override
    public int effectiveSpeed() {
        return type.effectiveSpeed;
    }

    @Override
    public float efficiencyModifier() {
        return type.effeciencyModifier;
    }

    @Override
    public float speedModifier() {
        return type.speedModifier;
    }

    @Override
    public float torqueModifier() {
        return type.torqueModifier;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        Lang.text("Piston Inv Size: "+pistonInventory.getSlots()).forGoggles(tooltip);

        Lang.text("Type: "+type.name).forGoggles(tooltip);

         super.addToGoggleTooltip(tooltip, isPlayerSneaking);

         return true;
    }

    @Override
    public String engineId() {
        return type.name;
    }
    enum EngineType{
        I("engine_i",pistonsI(),12,1,1,1),
        V("engine_v",pistonsV(),14,1.2f,1.3f,0.8f),
        W("engine_w",pistonsW(),16,1.3f,1.1f,0.5f),
        U("engine_u",pistonsU(),12,1,1.5f,0.9f),
        BOXER("engine_boxer",pistonsBoxer(),11,1,0.8f,1.2f)
        ;
        public final int effectiveSpeed;
        public final float speedModifier;
        public final float torqueModifier;
        public final float effeciencyModifier;
        public final List<PistonPosition> pistons;
        public final List<Fluid> fluidBlacklist;
        public final String name;

        EngineType(String name, List<PistonPosition> positions, int effectiveSpeed, float speedModifier,
                   float torqueModifier, float efficiencyModifier){
             this(name,positions, effectiveSpeed, speedModifier, torqueModifier, efficiencyModifier, new ArrayList<>());
        }

        EngineType(String name, List<PistonPosition> positions, int effectiveSpeed, float speedModifier,
                   float torqueModifier, float efficiencyModifier , List<Fluid> fluidBlacklist){
            this.name = name;
            this.pistons = positions;
            this.effectiveSpeed = effectiveSpeed;
            this.speedModifier = speedModifier;
            this.torqueModifier = torqueModifier;
            this.effeciencyModifier = efficiencyModifier;
            this.fluidBlacklist = fluidBlacklist;

        }


    }

}
