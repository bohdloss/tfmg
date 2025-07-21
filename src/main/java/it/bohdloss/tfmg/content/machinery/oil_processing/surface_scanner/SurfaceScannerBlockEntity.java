package it.bohdloss.tfmg.content.machinery.oil_processing.surface_scanner;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.DebugStuff;
import it.bohdloss.tfmg.config.TFMGConfigs;
import it.bohdloss.tfmg.content.machinery.misc.machine_input.MachineInputPowered;
import it.bohdloss.tfmg.registry.TFMGTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SurfaceScannerBlockEntity extends MachineInputPowered implements IHaveGoggleInformation {
    public Boolean[][] grid = new Boolean[5][5];

    public SurfaceScannerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(20);
    }

    public void findDeposits() {
        if(!level.isClientSide) {
            return;
        }

        for(int x = 0; x < 5; x++){
            for(int z = 0; z < 5; z++){
                grid[x][z] = hasOil(new BlockPos(getBlockPos().getX() + (x - 2) * 16, TFMGConfigs.common().machines.surfaceScannerScanDepth.get(), getBlockPos().getZ() + (z - 2) * 16));
            }
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.translate("goggles.surface_scanner.header")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        if(Math.abs(getMachineInputSpeed()) >= 64) {
            int depositsFound = 0;
            for(Boolean[] row : grid){
                for(Boolean light : row){
                    if(light != null && light) {
                        depositsFound++;
                    }
                }
            }

            if(depositsFound > 0) {
                CreateLang.number(depositsFound)
                        .add(CreateLang.translate("goggles.surface_scanner.deposits_found"))
                        .style(ChatFormatting.GREEN)
                        .forGoggles(tooltip);
            } else {
                CreateLang.translate("goggles.surface_scanner.no_deposit")
                        .style(ChatFormatting.RED)
                        .forGoggles(tooltip);
            }

        } else {
            CreateLang.translate("goggles.surface_scanner.no_rotation")
                    .style(ChatFormatting.DARK_RED)
                    .forGoggles(tooltip);
        }

        return true;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();

        if(Math.abs(getMachineInputSpeed()) >= 64) {
            findDeposits();
        } else {
            grid = new Boolean[5][5];
        }
    }

    public boolean hasOil(BlockPos pos){
        ChunkAccess chunk = level.getChunk(pos);
        AABB checkedArea = new AABB(chunk.getPos().getMiddleBlockPosition(TFMGConfigs.common().machines.surfaceScannerScanDepth.get()).north().west());
        checkedArea = checkedArea.inflate(7,0,7);
        for(BlockState state : chunk.getBlockStates(checkedArea).toList()){
            if(state.is(TFMGTags.TFMGBlockTags.SURFACE_SCANNER_FINDABLE.tag)) {
                return true;
            }
        }

        return false;
    }
}
