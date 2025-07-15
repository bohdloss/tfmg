package it.bohdloss.tfmg.content.decoration.tanks.aluminum;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import it.bohdloss.tfmg.content.decoration.tanks.IRefreshCapability;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber
public class AluminumTankBlockEntity extends FluidTankBlockEntity {
    public AluminumTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                TFMGBlockEntities.ALUMINUM_FLUID_TANK.get(),
                (be, context) -> {
                    if (be.fluidCapability == null)
                        ((IRefreshCapability) be).tfmg$refreshCapability();
                    return be.fluidCapability;
                }
        );
    }
}
