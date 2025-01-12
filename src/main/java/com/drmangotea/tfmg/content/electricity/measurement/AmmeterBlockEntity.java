package com.drmangotea.tfmg.content.electricity.measurement;

import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;


public class AmmeterBlockEntity extends VoltMeterBlockEntity {


    public AmmeterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public int getUnit(IElectric be) {
        return be.getMaxAmps();
    }
    @Override
    @SuppressWarnings("removal")
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        Lang.translate("goggles.ammeter")
                .style(ChatFormatting.DARK_GRAY)
                .forGoggles(tooltip, 1);

        Lang.translate("goggles.ammeter.amps", Math.min(value,range))
                .style(ChatFormatting.AQUA)
                .forGoggles(tooltip, 1);
        Lang.translate("goggles.voltmeter.range", range)
                .style(ChatFormatting.DARK_AQUA)
                .forGoggles(tooltip, 1);


        return true;
    }
}
