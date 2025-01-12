package com.drmangotea.tfmg.content.electricity.debug;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.base.TFMGUtils;
import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.drmangotea.tfmg.content.electricity.generators.GeneratorBlockEntity;
import com.drmangotea.tfmg.content.electricity.utilities.diode.ElectricDiodeBlockEntity;
import com.drmangotea.tfmg.content.engines.AbstractEngineBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.blast_stove.BlastStoveBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.casting_basin.CastingBasinBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.coke_oven.CokeOvenBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.vat_machines.base.VatBlockEntity;
import com.drmangotea.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.base.PumpjackBaseBlockEntity;
import com.drmangotea.tfmg.content.machinery.oil_processing.surface_scanner.SurfaceScannerBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

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

        if(level.getBlockEntity(pos) instanceof GeneratorBlockEntity be){
            be.updateStress();
            return InteractionResult.SUCCESS;
        }

        if(level.getBlockEntity(pos) instanceof ElectricDiodeBlockEntity be){
            be.updateInFront();
            return InteractionResult.SUCCESS;
        }
        if(level.getBlockEntity(pos) instanceof IElectric be){
            if(context.getPlayer().isShiftKeyDown()) {
               // for (IElectric member : be.getOrCreateElectricNetwork().members) {
               //     level.setBlock(BlockPos.of(member.getPos()).above(), Blocks.GOLD_BLOCK.defaultBlockState(), 3);
               // }
            }else be.updateNextTick();
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
            be.loadDepositData();
            if(be.depositData !=null) {
            if(context.getPlayer().isShiftKeyDown()){
                be.depositData.removeData();
                return InteractionResult.SUCCESS;
            }


                be.depositData.addDeposit(level, be.getBlockPos().below().asLong());
            }
        }
        return InteractionResult.SUCCESS;
    }
}
