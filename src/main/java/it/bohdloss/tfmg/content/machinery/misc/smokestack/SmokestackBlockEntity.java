package it.bohdloss.tfmg.content.machinery.misc.smokestack;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.base.TFMGFluidBehavior;
import it.bohdloss.tfmg.content.machinery.misc.exhaust.ExhaustBlock;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
    protected TFMGFluidBehavior tank;
    protected int smokeTimer;
    protected boolean produceSmoke;

    public SmokestackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = new TFMGFluidBehavior(TFMGFluidBehavior.TYPE, "Fluid", this, 8000)
                .withValidator(fluidStack -> fluidStack.getFluid().isSame(TFMGFluids.CARBON_DIOXIDE.getSource()))
                .allowExtraction(false)
                .allowInsertion(true)
                .withCallback(this::notifyUpdate);
        behaviours.add(tank);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return TFMGUtils.createFluidTooltip(this, tooltip, Direction.DOWN);
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

        // Client code
        if(produceSmoke) {
            makeParticles(level, getBlockPos());
        }
        if(getLevel().isClientSide()) {
            return;
        }

        // Server code
        if (level.getBlockEntity(getBlockPos().above()) instanceof SmokestackBlockEntity be) {
            int transferAmount = Math.min(
                    tank.getHandler().getFluidAmount(),
                    be.tank.getHandler().getSpace()
            );

            tank.getHandler().drain(transferAmount, IFluidHandler.FluidAction.EXECUTE);
            be.tank.getHandler().fill(new FluidStack(TFMGFluids.CARBON_DIOXIDE.get(), transferAmount), IFluidHandler.FluidAction.EXECUTE);
        } else {
            if (smokeTimer != 0) {
                smokeTimer--;
            }
            if (!tank.getHandler().isEmpty()) {
                if (getBlockState().getValue(TOP)) {
                    tank.getHandler().drain(tank.getHandler().getSpace() < 1000 ? 50 : 10, IFluidHandler.FluidAction.EXECUTE);
                    smokeTimer = 40;
                }
            }
        }
        setSmoke(smokeTimer != 0);
    }

    protected void setSmoke(boolean produceSmoke) {
        if(this.produceSmoke != produceSmoke) {
            this.produceSmoke = produceSmoke;
            notifyUpdate();
        }
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        tag.putBoolean("produceSmoke", produceSmoke);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        produceSmoke = tag.getBoolean("produceSmoke");
    }
}
