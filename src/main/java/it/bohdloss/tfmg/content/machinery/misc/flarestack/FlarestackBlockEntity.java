package it.bohdloss.tfmg.content.machinery.misc.flarestack;

import com.simibubi.create.Create;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGTags;
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
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

@EventBusSubscriber
public class FlarestackBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    protected SmartFluidTankBehaviour tank;
    public boolean spawnsSmoke;
    public int smokeTimer;

    public FlarestackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = SmartFluidTankBehaviour.single(this, 2500).allowInsertion().forbidExtraction();
        tank.getPrimaryHandler().setValidator(fluidStack -> {
            return fluidStack.getFluid().is(TFMGTags.TFMGFluidTags.FLAMMABLE.tag) ||
                    fluidStack.getFluid().is(TFMGTags.TFMGFluidTags.FUEL.tag);
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
                TFMGBlockEntities.FLARESTACK.get(),
                (it, ctx) -> it.tank.getCapability()
        );
    }

    @Override
    public void tick() {
        super.tick();

        if(smokeTimer != 0) {
            spawnsSmoke = true;
            smokeTimer--;
        } else {
            spawnsSmoke = false;
        }

        if(level != null) {
            if (spawnsSmoke) {
                level.setBlock(getBlockPos(), this.getBlockState()
                        .setValue(FlarestackBlock.LIT, true), 2);
                makeParticles(level, this.getBlockPos());
            } else {
                level.setBlock(getBlockPos(), this.getBlockState()
                        .setValue(FlarestackBlock.LIT, false), 2);
            }
        }

        if(!tank.getPrimaryHandler().isEmpty()) {
            smokeTimer = 100;
            spawnsSmoke = true;

            if(tank.getPrimaryHandler().getFluidAmount() > 1000) {
                tank.getPrimaryHandler().drain(100, IFluidHandler.FluidAction.EXECUTE);
            } else {
                tank.getPrimaryHandler().drain(30, IFluidHandler.FluidAction.EXECUTE);
            }
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
}
