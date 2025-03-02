package com.drmangotea.tfmg.content.electricity.debug;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.base.TFMGUtils;
import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.drmangotea.tfmg.content.electricity.connection.cables.CableConnection;
import com.drmangotea.tfmg.content.electricity.connection.cables.CableConnectorBlockEntity;
import com.drmangotea.tfmg.content.electricity.generators.GeneratorBlockEntity;
import com.drmangotea.tfmg.content.electricity.storage.AccumulatorBlockEntity;
import com.drmangotea.tfmg.content.electricity.utilities.electric_motor.ElectricMotorBlockEntity;
import com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.engine_controller.EngineControllerBlockEntity;
import com.drmangotea.tfmg.content.engines.regular_engine.RegularEngineBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.blast_stove.BlastStoveBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.casting_basin.CastingBasinBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.coke_oven.CokeOvenBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.concrete_hose.ConcreteHoseBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.vat_machines.base.VatBlockEntity;
import com.drmangotea.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.base.PumpjackBaseBlockEntity;
import com.drmangotea.tfmg.content.machinery.oil_processing.surface_scanner.SurfaceScannerBlockEntity;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.ticks.TickPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.simibubi.create.content.fluids.pipes.FluidPipeBlock.isOpenAt;

public class DebugCinderBlockItem extends Item {
    public DebugCinderBlockItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        if(level.getBlockEntity(pos) instanceof EngineControllerBlockEntity be){
            context.getPlayer().getPersistentData().remove("IsUsingEngineController");
            TFMG.LOGGER.debug("REMOVED PERSISTENT DATA");
            return InteractionResult.SUCCESS;
        }
        if(level.getBlockEntity(pos) instanceof RegularEngineBlockEntity be){
            be.updateRotation();
            return InteractionResult.SUCCESS;
        }
        //if(level.getBlockEntity(pos) instanceof GeneratorBlockEntity be){
        //    be.updateStress();
        //    return InteractionResult.SUCCESS;
        //}
        //if(level.getBlockEntity(pos) instanceof ElectricMotorBlockEntity be){
        //    be.onPlaced();
        //    return InteractionResult.SUCCESS;
        //}

        if(level.getBlockEntity(pos) instanceof ConcreteHoseBlockEntity be){

            be.filler.reset();

            return InteractionResult.SUCCESS;
        }

        if(level.getBlockEntity(pos) instanceof CableConnectorBlockEntity be){
            if(context.getPlayer().isShiftKeyDown()) {
                TFMG.LOGGER.debug("///////////////////////////////");
                for(CableConnection connection : be.connections) {
                    TFMG.LOGGER.debug("Primary Pos "+connection.blockPos1.getX()+" "+connection.blockPos1.getY()+" "+connection.blockPos1.getZ());
                }
                TFMG.LOGGER.debug("///////////////////////////////");
                return InteractionResult.SUCCESS;
            }
            TFMG.LOGGER.debug("///////////////////////////////");
            TFMG.LOGGER.debug("Connector Count "+be.getConnectedWires().size());
            TFMG.LOGGER.debug("Wire Count "+be.connections.size());
            TFMG.LOGGER.debug("Member Count "+be.getOrCreateElectricNetwork().members.size());
            TFMG.LOGGER.debug("Id "+"X "+BlockPos.of(be.getData().electricalNetworkId).getX()+" Y "+BlockPos.of(be.getData().electricalNetworkId).getY()+" Z "+BlockPos.of(be.getData().electricalNetworkId).getZ());
            TFMG.LOGGER.debug("///////////////////////////////");
            return InteractionResult.SUCCESS;
        }
        if(level.getBlockEntity(pos) instanceof IElectric be){

            be.onPlaced();


            if(context.getPlayer().isShiftKeyDown()) {
                TFMG.LOGGER.debug("///////////////////////////////");
                TFMG.LOGGER.debug("Group Resistance "+be.getData().group.resistance);
                TFMG.LOGGER.debug("Resistance "+be.resistance());
                TFMG.LOGGER.debug("Voltage Supply "+be.getData().voltageSupply);
                TFMG.LOGGER.debug("Higherst Current "+be.getData().highestCurrent);
                TFMG.LOGGER.debug("Group "+be.getData().group.id);
                TFMG.LOGGER.debug("///////////////////////////////");
            }else {

                TFMGUtils.debugMessage(level, "Power "+be.getNetworkPowerUsage());
                TFMGUtils.debugMessage(level, "Voltage "+be.getData().getVoltage());

            };
        }
        if(level.getBlockEntity(pos) instanceof CokeOvenBlockEntity be){
            be.controller = be.getBlockPos();
        }
        if(level.getBlockEntity(pos) instanceof BlastStoveBlockEntity be){
            be.notifyMultiUpdated();
        }
        if(level.getBlockEntity(pos) instanceof VatBlockEntity be){
                be.evaluate();

        }
        if(level.getBlockEntity(pos) instanceof FluidTankBlockEntity be){
            TFMGUtils.blowUpTank(be, 5);
        }
        if(level.getBlockEntity(pos) instanceof RegularEngineBlockEntity be){

        }
        if(level.getBlockEntity(pos) instanceof AbstractEngineBlockEntity be){
            be.connect();
        }
        if(level.getBlockEntity(pos) instanceof CastingBasinBlockEntity be){
            be.findRecipe();
        }
        if(level.getBlockEntity(pos) instanceof SurfaceScannerBlockEntity be){
            be.findDeposits();
        }
        if(level.getBlockEntity(pos) instanceof PumpjackBaseBlockEntity be){
            if(TFMG.DEPOSITS.depositData !=null) {
            if(context.getPlayer().isShiftKeyDown()){
                TFMG.DEPOSITS.depositData.removeData();
                return InteractionResult.SUCCESS;
            } else TFMG.DEPOSITS.depositData.removeEmptyDeposits();


            }
        }
        return InteractionResult.SUCCESS;
    }
}
