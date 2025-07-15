package it.bohdloss.tfmg.content.machinery.misc.concrete_hose;

import com.simibubi.create.content.fluids.hosePulley.HosePulleyBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import it.bohdloss.tfmg.content.decoration.concrete.ConcreteloggedBlock;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

@EventBusSubscriber
public class ConcreteHoseBlockEntity extends KineticBlockEntity {
    LerpedFloat offset;
    boolean isMoving;

    public SmartFluidTank internalTank;
    public ConcreteFillingBehavior filler;
    public ConcreteHoseFluidHandler handler;

    public ConcreteHoseBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        offset = LerpedFloat.linear()
                .startWithValue(0);
        isMoving = true;
        internalTank = new SmartFluidTank(1500, this::onTankContentsChanged);
        handler = new ConcreteHoseFluidHandler(internalTank, filler,
                () -> worldPosition.below((int) Math.ceil(offset.getValue())), () -> !this.isMoving);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                TFMGBlockEntities.CONCRETE_HOSE.get(),
                (be, context) -> {
                    if (context == null || HosePulleyBlock.hasPipeTowards(be.level, be.worldPosition, be.getBlockState(), context))
                        return be.handler;
                    return null;
                }
        );
    }

    @Override
    public void sendData() {
        super.sendData();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        filler = new ConcreteFillingBehavior(this);
        behaviours.add(filler);
        super.addBehaviours(behaviours);
        registerAwardables(behaviours, AllAdvancements.HOSE_PULLEY);
    }

    protected void onTankContentsChanged(FluidStack contents) {
        notifyUpdate();
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        isMoving = true;
        if (getSpeed() == 0) {
            offset.forceNextSync();
            offset.setValue(Math.round(offset.getValue()));
            isMoving = false;
        }

        if (isMoving) {
            float newOffset = offset.getValue() + getMovementSpeed();
            if (newOffset < 0) {
                isMoving = false;
            }

            BlockState test = level.getBlockState(worldPosition.below((int) Math.ceil(newOffset)));
            if (!canGoThrough(test)) {
                isMoving = false;
            }
            if (isMoving) {
                filler.reset();
            }
        }

        super.onSpeedChanged(previousSpeed);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().expandTowards(0, -offset.getValue(), 0);
    }

    protected boolean canGoThrough(BlockState state) {
        return state.canBeReplaced() || state.hasProperty(ConcreteloggedBlock.CONCRETELOGGED);
    }

    @Override
    public void tick() {
        super.tick();
        float newOffset = offset.getValue() + getMovementSpeed();
        if (newOffset < 0) {
            newOffset = 0;
            isMoving = false;
        }
        BlockState test = level.getBlockState(worldPosition.below((int) Math.ceil(newOffset)));
        if (!canGoThrough(test)) {
            newOffset = (int) newOffset;
            isMoving = false;
        }
        if (getSpeed() == 0) {
            isMoving = false;
        }

        offset.setValue(newOffset);
        invalidateRenderBoundingBox();
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (level.isClientSide) {
            return;
        }
        if (isMoving) {
            return;
        }

        int ceil = (int) Math.ceil(offset.getValue() + getMovementSpeed());
        BlockState test = level.getBlockState(worldPosition.below(ceil));
        if (getMovementSpeed() > 0 && canGoThrough(test)) {
            isMoving = true;
            filler.reset();
            return;
        }

        sendData();
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (clientPacket) {
            offset.forceNextSync();
        }
        compound.put("Offset", offset.writeNBT());
        compound.put("Tank", internalTank.writeToNBT(registries, new CompoundTag()));
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        offset.readNBT(compound.getCompound("Offset"), clientPacket);
        internalTank.readFromNBT(registries, compound.getCompound("Tank"));
        super.read(compound, registries, clientPacket);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        invalidateCapabilities();
    }

    public float getMovementSpeed() {
        float movementSpeed = convertToLinear(getSpeed());
        if (level.isClientSide) {
            movementSpeed *= ServerSpeedProvider.get();
        }
        return movementSpeed;
    }

    public float getInterpolatedOffset(float pt) {
        return Math.max(offset.getValue(pt), 3 / 16f);
    }
}
