package it.bohdloss.tfmg.content.machinery.misc.smokestack;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

import static it.bohdloss.tfmg.content.machinery.misc.smokestack.SmokestackBlock.TOP;

@EventBusSubscriber
public class SmokestackBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    protected SmartFluidTankBehaviour tank;
    protected int smokeTimer;

    public SmokestackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = SmartFluidTankBehaviour.single(this, 8000).allowInsertion().forbidExtraction();
        tank.getPrimaryHandler().setValidator(fluidStack -> {
            return fluidStack.getFluid().isSame(TFMGFluids.CARBON_DIOXIDE.getSource());
        });
        behaviours.add(tank);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return TFMGUtils.createFluidTooltip(this, tooltip);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                TFMGBlockEntities.SMOKESTACK.get(),
                (it, ctx) -> it.tank.getCapability()
        );
    }

    public static void makeParticles(Level level, BlockPos pos) {
        var random = level.getRandom();
        int shouldSpawnSmoke = random.nextInt(7);
        if (shouldSpawnSmoke == 0) {
            level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, pos.getX() + random.nextFloat(), pos.getY() + 1, pos.getZ() + random.nextFloat(), 0.0D, 0.08D, 0.0D);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (level.getBlockEntity(getBlockPos().above()) instanceof SmokestackBlockEntity be) {
            int transferAmount = Math.min(
                    tank.getPrimaryHandler().getFluidAmount(),
                    be.tank.getPrimaryHandler().getSpace()
            );

            tank.getPrimaryHandler().drain(transferAmount, IFluidHandler.FluidAction.EXECUTE);
            be.tank.getPrimaryHandler().fill(new FluidStack(TFMGFluids.CARBON_DIOXIDE.get(), transferAmount), IFluidHandler.FluidAction.EXECUTE);
        } else {
            if (smokeTimer > 0) {
                makeParticles(level, getBlockPos());
                smokeTimer--;
            }

            if (tank.isEmpty()) {
                return;
            }

            if (getBlockState().getValue(TOP)) {
                tank.getPrimaryHandler().drain(tank.getPrimaryHandler().getSpace() < 1000 ? 50 : 10, IFluidHandler.FluidAction.EXECUTE);
                smokeTimer = 40;
            }
        }
    }
}
