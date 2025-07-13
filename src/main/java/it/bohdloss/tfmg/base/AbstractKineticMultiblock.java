package it.bohdloss.tfmg.base;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;

import java.util.function.Consumer;

public abstract class AbstractKineticMultiblock extends KineticBlockEntity implements IMultiBlockEntityContainer.Fluid, IMultiBlockEntityContainer.Inventory {
    protected BlockPos controller;
    protected BlockPos lastKnownPos;
    protected boolean updateConnectivity;
    protected int width;
    protected int height;

    public AbstractKineticMultiblock(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        updateConnectivity = false;
        width = 1;
        height = 1;
    }
    protected abstract BlockState rotateBlockToMatch(BlockState current, BlockState controller);

    @Override
    public void tick() {
        super.tick();

        if(!level.isClientSide && height == 0) {
            removeController(true);
            setTankSize(0, 1);
            notifyUpdate();
        }

        if (lastKnownPos == null)
            lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition) && worldPosition != null) {
            removeController(true);
            lastKnownPos = worldPosition;
            return;
        }

        if (updateConnectivity) {
            updateConnectivity();
        }
    }

    public void updateConnectivity() {
        updateConnectivity = false;
        if (level.isClientSide) {
            return;
        }
        if (!isController()) {
            return;
        }
        ConnectivityHandler.formMulti(this);
    }

    public void withAllBlocksDo(Consumer<BlockPos> consumer) {
        if(height == 0) {
            consumer.accept(lastKnownPos);
            return;
        }
        AbstractKineticMultiblock controller = getControllerBE();
        if(controller == null) {
            consumer.accept(lastKnownPos);
            return;
        }
        if(controller.height == 0) {
            consumer.accept(controller.lastKnownPos);
            return;
        }
        BlockPos controllerPos = controller.lastKnownPos;
        int minX = controllerPos.getX();
        int minY = controllerPos.getY();
        int minZ = controllerPos.getZ();

        Direction.Axis axis = controller.getMainConnectionAxis();

        int maxX = minX + (axis == Direction.Axis.X ? controller.height : controller.width);
        int maxY = minY + (axis == Direction.Axis.Y ? controller.height : controller.width);
        int maxZ = minZ + (axis == Direction.Axis.Z ? controller.height : controller.width);

        BlockPos.MutableBlockPos checkingPos = new BlockPos.MutableBlockPos(minX, minY, minZ);
        for(int x = minX; x < maxX; x++) {
            for(int y = minY; y < maxY; y++) {
                for(int z = minZ; z < maxZ; z++) {
                    checkingPos.set(x, y, z);
                    consumer.accept(checkingPos);
                }
            }
        }
    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractKineticMultiblock getControllerBE() {
        if (isController() || level == null) {
            return this;
        }
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if(blockEntity != null) {
            if(getClass().isAssignableFrom(blockEntity.getClass())) {
                return (AbstractKineticMultiblock) blockEntity;
            }
        }
        return this;
    }

    @Override
    public boolean isController() {
        return controller == null || worldPosition.getX() == controller.getX()
                && worldPosition.getY() == controller.getY() && worldPosition.getZ() == controller.getZ();
    }

    @Override
    public void setController(BlockPos pos) {
        if (level.isClientSide && !isVirtual()) {
            return;
        }
        if (pos.equals(this.controller)) {
            return;
        }
        this.controller = pos;

        BlockState current = getBlockState();
        BlockState controller = level.getBlockState(pos);
        BlockState target = rotateBlockToMatch(current, controller);
        if(!target.equals(current)) {
            level.setBlock(worldPosition, target, 2);
        }

        notifyUpdate();
    }

    @Override
    public void removeController(boolean keepContents) {
        if (level.isClientSide) {
            return;
        }
        updateConnectivity = true;
        controller = null;
        width = 1;
        height = 1;

        notifyUpdate();
    }

    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    @Override
    public void notifyMultiUpdated() {
        notifyUpdate();
    }

    @Override
    public abstract Direction.Axis getMainConnectionAxis();

    @Override
    public abstract int getMaxLength(Direction.Axis longAxis, int width);

    @Override
    public abstract int getMaxWidth();

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public abstract boolean hasTank();

    @Override
    public abstract int getTankSize(int tank);

    @Override
    public abstract void setTankSize(int tank, int blocks);

    @Override
    public abstract IFluidTank getTank(int tank);

    @Override
    public abstract FluidStack getFluid(int tank);

    @Override
    public abstract boolean hasInventory();

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (updateConnectivity) {
            compound.putBoolean("Uninitialized", true);
        }
        if (lastKnownPos != null)
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));
        if (!isController())
            compound.put("Controller", NbtUtils.writeBlockPos(controller));
        if (isController()) {
            compound.putInt("Width", width);
            compound.putInt("Height", height);
        }
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        updateConnectivity = compound.contains("Uninitialized");

        lastKnownPos = null;
        if (compound.contains("LastKnownPos")) {
            lastKnownPos = NBTHelper.readBlockPos(compound, "LastKnownPos");
        }

        controller = null;
        if (compound.contains("Controller")) {
            controller = NBTHelper.readBlockPos(compound, "Controller");
        }

        if (isController()) {
            width = compound.getInt("Width");
            height = compound.getInt("Height");
        }
    }
}
