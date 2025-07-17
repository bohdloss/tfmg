package it.bohdloss.tfmg.content.machinery.misc.exhaust;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import it.bohdloss.tfmg.DebugStuff;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.base.TFMGFluidBehavior;
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
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

@EventBusSubscriber
public class ExhaustBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    protected TFMGFluidBehavior tank;
    public int smokeTimer;
    public boolean produceSmoke;

    public ExhaustBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = new TFMGFluidBehavior(TFMGFluidBehavior.TYPE, "Fluid", this, 1000)
                .withValidator(fluidStack -> fluidStack.getFluid().isSame(TFMGFluids.CARBON_DIOXIDE.getSource()))
                .allowExtraction(false)
                .allowInsertion(true)
                .syncCapacity(false)
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
                TFMGBlockEntities.EXHAUST.get(),
                (it, ctx) -> it.tank.getCapability()
        );
    }

    public static void makeParticles(Level level, BlockPos pos, Direction direction) {
        var random = level.getRandom();
        int shouldSpawnSmoke = random.nextInt(7);
        if(shouldSpawnSmoke == 0) {
            switch (direction) {
                case DOWN -> level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, pos.getX() + random.nextFloat(), pos.getY(), pos.getZ() + random.nextFloat(), 0.0D, 0.08D, 0.0D);
                case UP -> level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, pos.getX() + random.nextFloat(), pos.getY() + 1, pos.getZ() + random.nextFloat(), 0.0D, 0.08D, 0.0D);
                case NORTH -> level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ(), 0.0D, 0.08D, 0.0D);
                case SOUTH -> level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ() + 1, 0.0D, 0.08D, 0.0D);
                case WEST -> level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, pos.getX(), pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(), 0.0D, 0.08D, 0.0D);
                case EAST -> level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, pos.getX() + 1, pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(), 0.0D, 0.08D, 0.0D);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        // Client code
        if(produceSmoke) {
            Direction direction = this.getBlockState().getValue(ExhaustBlock.FACING);
            makeParticles(level, this.getBlockPos(), direction);
        }
        if(getLevel().isClientSide()) {
            return;
        }

        // Server code
        if(smokeTimer != 0) {
            smokeTimer--;
        }
        if(!tank.getHandler().isEmpty()) {
            smokeTimer = 100;
        }
        setSmoke(smokeTimer != 0);
        tank.getHandler().drain(100, IFluidHandler.FluidAction.EXECUTE);
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
