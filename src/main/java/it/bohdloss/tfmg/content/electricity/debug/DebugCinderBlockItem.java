package it.bohdloss.tfmg.content.electricity.debug;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import it.bohdloss.tfmg.DebugStuff;
import it.bohdloss.tfmg.content.machinery.misc.air_intake.AirIntakeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DebugCinderBlockItem extends Item {
    public DebugCinderBlockItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
//        if (level.getBlockEntity(pos) instanceof IElectric be) {
//            if(level.isClientSide()) {
//                DebugStuff.show("VOLTAGE {}", be.getData().voltage);
//            } else {
//                TFMG.LOGGER.debug("VOLTAGE {}", be.getData().voltage);
//            }
//        }

        if(!level.isClientSide && level.getBlockEntity(pos) instanceof AirIntakeBlockEntity be) {
            DebugStuff.show((level.isClientSide ? "client" : "server") + "; is controller: " + be.isController() + "; width: " + be.getWidth() + " height: " + be.getHeight());
        }
        if(!level.isClientSide && level.getBlockEntity(pos) instanceof FluidTankBlockEntity be) {
            DebugStuff.show((level.isClientSide ? "client" : "server") + "; is controller: " + be.isController() + "; width: " + be.getWidth() + " height: " + be.getHeight());
        }

        return InteractionResult.SUCCESS;
    }
}
