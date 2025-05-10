package com.drmangotea.tfmg.content.electricity.debug;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.base.TFMGUtils;
import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.drmangotea.tfmg.content.electricity.connection.cables.CableConnection;
import com.drmangotea.tfmg.content.electricity.connection.cables.CableConnectorBlockEntity;
import com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.engine_controller.EngineControllerBlockEntity;
import com.drmangotea.tfmg.content.engines.types.AbstractSmallEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.types.large_engine.LargeEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.types.regular_engine.RegularEngineBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.blast_stove.BlastStoveBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.casting_basin.CastingBasinBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.coke_oven.CokeOvenBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.concrete_hose.ConcreteHoseBlockEntity;
import com.drmangotea.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.base.FluidReservoir;
import com.drmangotea.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.base.PumpjackBaseBlockEntity;
import com.drmangotea.tfmg.content.machinery.oil_processing.surface_scanner.SurfaceScannerBlockEntity;
import com.drmangotea.tfmg.content.machinery.vat.base.VatBlockEntity;
import com.drmangotea.tfmg.registry.TFMGFluids;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;

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
        if (level.getBlockEntity(pos) instanceof VatBlockEntity be) {
            if (!level.isClientSide)
                return InteractionResult.SUCCESS;
            TFMG.LOGGER.debug("////////////////////////////////////////");
            for (int i = 0; i < 8; i++) {
                TFMG.LOGGER.debug("fluid " + i + ": " + be.fluidLevel[i].getValue());
            }
            TFMG.LOGGER.debug("////////////////////////////////////////");
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof EngineControllerBlockEntity be) {

            if (context.getPlayer().isCrouching()) {
                context.getPlayer().getPersistentData().remove("IsUsingEngineController");

            } else {
                TFMG.LOGGER.debug(be.shift.name());
                TFMGUtils.debugMessage(level, "ENGINE "+(be.engine!=null));
            }
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof LargeEngineBlockEntity be) {

            be.airTank.fill(new FluidStack(TFMGFluids.AIR.getSource(),200), IFluidHandler.FluidAction.EXECUTE);

            return InteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(pos) instanceof ConcreteHoseBlockEntity be) {

            be.filler.reset();

            return InteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(pos) instanceof CableConnectorBlockEntity be) {
            if (context.getPlayer().isShiftKeyDown()) {
                TFMG.LOGGER.debug("///////////////////////////////");
                for (CableConnection connection : be.connections) {
                    TFMG.LOGGER.debug("Primary Pos " + connection.blockPos1.getX() + " " + connection.blockPos1.getY() + " " + connection.blockPos1.getZ());
                }
                TFMG.LOGGER.debug("///////////////////////////////");
                return InteractionResult.SUCCESS;
            }
            TFMG.LOGGER.debug("///////////////////////////////");
            TFMG.LOGGER.debug("Connector Count " + be.getConnectedWires().size());
            TFMG.LOGGER.debug("Wire Count " + be.connections.size());
            TFMG.LOGGER.debug("Member Count " + be.getOrCreateElectricNetwork().members.size());
            TFMG.LOGGER.debug("Id " + "X " + BlockPos.of(be.getData().electricalNetworkId).getX() + " Y " + BlockPos.of(be.getData().electricalNetworkId).getY() + " Z " + BlockPos.of(be.getData().electricalNetworkId).getZ());
            TFMG.LOGGER.debug("///////////////////////////////");
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof IElectric be) {

           // be.onPlaced();


            if (context.getPlayer().isShiftKeyDown()) {
                TFMG.LOGGER.debug("///////////////////////////////");
                TFMG.LOGGER.debug("Group Resistance " + be.getData().group.resistance);
                TFMG.LOGGER.debug("Resistance " + be.resistance());
                TFMG.LOGGER.debug("Voltage Supply " + be.getData().voltageSupply);
                TFMG.LOGGER.debug("Higherst Current " + be.getData().highestCurrent);
                TFMG.LOGGER.debug("Group " + be.getData().group.id);
                TFMG.LOGGER.debug("///////////////////////////////");
            } else {
                be.updateNextTick();
                TFMGUtils.debugMessage(level, "Power " + be.getNetworkPowerUsage());
                TFMGUtils.debugMessage(level, "Voltage " + be.getData().getVoltage());

            }
            ;
        }
        if (level.getBlockEntity(pos) instanceof CokeOvenBlockEntity be) {
            be.controller = be.getBlockPos();
        }
        if (level.getBlockEntity(pos) instanceof BlastStoveBlockEntity be) {
            be.notifyMultiUpdated();
        }
        if (level.getBlockEntity(pos) instanceof VatBlockEntity be) {
            be.evaluate();

        }
        if (level.getBlockEntity(pos) instanceof FluidTankBlockEntity be) {
            TFMGUtils.blowUpTank(be, 5);
        }
        if (level.getBlockEntity(pos) instanceof MechanicalMixerBlockEntity be) {
            TFMG.LOGGER.debug("Reservoir Count " + TFMG.DEPOSITS.list.size());
            if (context.getPlayer().isCrouching()) {
                TFMG.DEPOSITS.list = new ArrayList<>();
                TFMG.DEPOSITS.markDirty();
            } else {
                // TFMG.DEPOSITS.list.add(new FluidReservoir(758441445444L));
                // TFMG.DEPOSITS.markDirty();
                if (!TFMG.DEPOSITS.list.isEmpty()) {
                    FluidReservoir reservoir = TFMG.DEPOSITS.list.get(0);
                    TFMG.LOGGER.debug("////////////////////");
                    for (long deposit : reservoir.deposits)
                        TFMG.LOGGER.debug("Deposit " + deposit);
                    TFMG.LOGGER.debug("Size " + reservoir.oilReserves);
                    TFMG.LOGGER.debug("ID " + reservoir.id);
                    TFMG.LOGGER.debug("////////////////////");
                }
            }
        }

        if (level.getBlockEntity(pos) instanceof AbstractSmallEngineBlockEntity be) {
            //be.getControllerBE().highestSignal=0;
            //be.getControllerBE().rpm = 0;
            //be.getControllerBE().engineController = null;
            //be.getControllerBE().updateGeneratedRotation();
            TFMGUtils.debugMessage(level, "RPM "+be.getControllerBE().rpm);
            TFMGUtils.debugMessage(level, "SIGNAL "+be.getControllerBE().signal);
            TFMGUtils.debugMessage(level, "HSIGNAL "+be.getControllerBE().highestSignal);
            TFMGUtils.debugMessage(level, "RATE "+be.getControllerBE().fuelInjectionRate);
        }
        if (level.getBlockEntity(pos) instanceof CastingBasinBlockEntity be) {
            be.findRecipe();
        }
        if (level.getBlockEntity(pos) instanceof SurfaceScannerBlockEntity be) {
            be.findDeposits();
        }
        if (level.getBlockEntity(pos) instanceof PumpjackBaseBlockEntity be) {
            if (!context.getPlayer().isCrouching()) {
                be.process();
            } else {
                if (context.getPlayer().getServer() != null)
                    TFMG.DEPOSITS.levelLoaded(context.getPlayer().level());
            }
        }
        return InteractionResult.SUCCESS;
    }
}
