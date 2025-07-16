package it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.base;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.base.TFMGFluidBehavior;
import it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.hammer.PumpjackBlockEntity;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.List;

@EventBusSubscriber
public class PumpjackBaseBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public TFMGFluidBehavior tank;
    public PumpjackBlockEntity controllerHammer;
    public boolean isRunning = false;
    int depositCheckTimer = 0;
    public int miningRate = 0;

    public PumpjackBaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = new TFMGFluidBehavior(TFMGFluidBehavior.TYPE, "Fluid", this, 8000)
                .allowExtraction(true)
                .allowInsertion(false)
                .withCallback(this::notifyUpdate);

        behaviours.add(tank);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.translate("goggles.pumpjack_info")
                .forGoggles(tooltip);
//        if (deposit == null) {
//            CreateLang.translate("goggles.zero")
//                    .style(ChatFormatting.DARK_RED) TODO
//                    .forGoggles(tooltip, 1);
//        }

        TFMGUtils.createFluidTooltip(this, tooltip, Direction.DOWN);
        return true;
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                TFMGBlockEntities.PUMPJACK_BASE.get(),
                (be, ctx) -> be.tank.getCapability()
        );
    }

    public void setControllerHammer(PumpjackBlockEntity controllerHammer) {
        this.controllerHammer = controllerHammer;
    }
}
