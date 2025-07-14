package it.bohdloss.tfmg.content.machinery.misc.flarestack;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.base.TFMGFluidBehavior;
import it.bohdloss.tfmg.content.machinery.misc.exhaust.ExhaustBlock;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGTags;
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
public class FlarestackBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    protected TFMGFluidBehavior tank;
    public int smokeTimer;
    public boolean produceSmoke;

    public FlarestackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = new TFMGFluidBehavior(TFMGFluidBehavior.TYPE, "Fluid", this, 2500)
                .withValidator(fluidStack ->
                        fluidStack.getFluid().is(TFMGTags.TFMGFluidTags.FLAMMABLE.tag) ||
                        fluidStack.getFluid().is(TFMGTags.TFMGFluidTags.FUEL.tag))
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
                TFMGBlockEntities.FLARESTACK.get(),
                (it, ctx) -> it.tank.getCapability()
        );
    }

    @Override
    public void tick() {
        super.tick();

        // Client code
        if(produceSmoke) {
            makeParticles(level, this.getBlockPos());
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
        if(level != null) {
            if (produceSmoke) {
                level.setBlock(getBlockPos(), this.getBlockState()
                        .setValue(FlarestackBlock.LIT, true), 2);
            } else {
                level.setBlock(getBlockPos(), this.getBlockState()
                        .setValue(FlarestackBlock.LIT, false), 2);
            }
        }
        setSmoke(smokeTimer != 0);
        if(tank.getHandler().getFluidAmount() > 1000) {
            tank.getHandler().drain(100, IFluidHandler.FluidAction.EXECUTE);
        } else {
            tank.getHandler().drain(30, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    protected void setSmoke(boolean produceSmoke) {
        if(this.produceSmoke != produceSmoke) {
            this.produceSmoke = produceSmoke;
            notifyUpdate();
        }
    }

    public static void makeParticles(Level level, BlockPos pos) {
        var random = level.getRandom();
        int shouldSpawnSmoke = random.nextInt(7);
        if(shouldSpawnSmoke==0) {
            level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, pos.getX()  + random.nextFloat(), pos.getY() + 1, pos.getZ()  +random.nextFloat(), 0.0D, 0.08D, 0.0D);
            level.addParticle(ParticleTypes.FLAME, pos.getX() + random.nextFloat(), pos.getY() + 1, pos.getZ()  +random.nextFloat(), random.nextDouble() * 0.28 - 0.14D, 0.14D, random.nextDouble() * 0.28 - 0.14D);
            level.addParticle(ParticleTypes.FLAME, pos.getX() + random.nextFloat(), pos.getY() + 1, pos.getZ()  +random.nextFloat(), random.nextDouble() * 0.28 - 0.14D, 0.14D, random.nextDouble() * 0.28 - 0.14D);
            level.addParticle(ParticleTypes.FLAME, pos.getX() + random.nextFloat(), pos.getY() + 1, pos.getZ()  +random.nextFloat(), random.nextDouble() * 0.28 - 0.14D, 0.14D, random.nextDouble() * 0.28 - 0.14D);
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
