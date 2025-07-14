package it.bohdloss.tfmg.content.machinery.misc.air_intake;

import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.base.AbstractKineticMultiblock;
import it.bohdloss.tfmg.base.TFMGFluidBehavior;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGFluids;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

import static it.bohdloss.tfmg.content.machinery.misc.air_intake.AirIntakeBlock.INVISIBLE;

@EventBusSubscriber
public class AirIntakeBlockEntity extends AbstractKineticMultiblock {
    public boolean hasShaft = true;

    protected static final int CAPACITY_MULTIPLIER = 8000;

    public float angle = 0;
    public LerpedFloat visual_angle = LerpedFloat.angular();

    protected TFMGFluidBehavior tank;

    public AirIntakeBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        tank = new TFMGFluidBehavior(TFMGFluidBehavior.TYPE, "Fluid", this, CAPACITY_MULTIPLIER)
                .withValidator(fluidStack -> fluidStack.getFluid().isSame(TFMGFluids.AIR.getSource()))
                .allowInsertion(false)
                .allowExtraction(true)
                .withCallback(this::notifyUpdate);
        behaviours.add(tank);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                TFMGBlockEntities.AIR_INTAKE.get(),
                (it, ctx) -> ((AirIntakeBlockEntity) it.getControllerBE()).tank.getCapability()
        );
    }

    protected void generateAir() {
        if(level.isClientSide || speed == 0f) {
            return;
        }

        AirIntakeBlockEntity be = (AirIntakeBlockEntity) getControllerBE();

        if(be != null) {
            float speed = getSpeed();
            float howFull = ((float) be.tank.getHandler().getFluidAmount()) / ((float) be.tank.getHandler().getCapacity());
            float loss = 100f * Math.max(howFull, 0.1f);
            float production = Math.abs(speed) / 40f;

            float total = production - (speed == 0 ? loss : 0);

            if (total > 0) {
                be.tank.getHandler().fill(new FluidStack(TFMGFluids.AIR.get(), (int) total), IFluidHandler.FluidAction.EXECUTE);
            } else if (total < 0) {
                be.tank.getHandler().drain((int) -total, IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    public void tick() {
        super.tick();

        if(level.isClientSide) {
            return;
        }

        generateAir();

        AirIntakeBlockEntity controller = (AirIntakeBlockEntity) getControllerBE();
        if(controller != null) {
            int width = controller.getWidth();
            if (width == 3) {
                visual_angle.chase(angle, 0.1f, LerpedFloat.Chaser.EXP);
                visual_angle.tickChaser();
            }
            angle += speed / 2;
            angle %= 360;

            boolean shouldBeInvisible = width != 1;
            if (this.getBlockState().getValue(INVISIBLE) != shouldBeInvisible) {
                getLevel().setBlock(getBlockPos(), getBlockState().setValue(INVISIBLE, shouldBeInvisible), 2);
            }
        }
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(this.getBlockPos()).inflate(3);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        addStressImpactStats(tooltip, calculateStressApplied());
        return TFMGUtils.createFluidTooltip(this, tooltip, Direction.DOWN);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        hasShaft = compound.getBoolean("hasShaft");
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putBoolean("hasShaft", hasShaft);
    }

    // AbstractKineticMultiblock

    @Override
    protected BlockState rotateBlockToMatch(BlockState current, BlockState controller) {
        return controller;
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return getBlockState().getValue(DirectionalKineticBlock.FACING).getAxis();
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        Direction.Axis axis = getBlockState().getValue(DirectionalKineticBlock.FACING).getAxis();
        if(longAxis == axis) {
            return 1;
        } else {
            return getMaxWidth();
        }
    }

    @Override
    public int getMaxWidth() {
        return 3;
    }

    @Override
    public boolean hasTank() {
        return true;
    }

    @Override
    public int getTankSize(int tank) {
        return width * width;
    }

    @Override
    public void setTankSize(int tank, int blocks) {
        this.tank.getHandler().setCapacity(CAPACITY_MULTIPLIER * blocks);
    }

    @Override
    public IFluidTank getTank(int tank) {
        return this.tank.getHandler();
    }

    @Override
    public FluidStack getFluid(int tank) {
        return this.tank.getHandler().getFluid();
    }

    @Override
    public boolean hasInventory() {
        return false;
    }
}
