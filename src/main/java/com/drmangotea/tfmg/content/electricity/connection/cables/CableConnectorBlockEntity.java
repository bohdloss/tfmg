package com.drmangotea.tfmg.content.electricity.connection.cables;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.content.electricity.base.ElectricNetworkManager;
import com.drmangotea.tfmg.registry.TFMGPackets;
import com.simibubi.create.content.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class CableConnectorBlockEntity extends SmartBlockEntity implements IHaveHoveringInformation {

    //player held cable rendering
    public Player player;
    public int color = 0x000000;
    public LerpedFloat wireMovementX = LerpedFloat.linear();
    public LerpedFloat wireMovementY = LerpedFloat.linear();
    public LerpedFloat wireMovementZ = LerpedFloat.linear();
    //

    public List<CableConnection> connections = new ArrayList<>();
    public long id;

    public CableConnectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        wireMovementX.setValue(pos.getX());
        wireMovementY.setValue(pos.getY());
        wireMovementZ.setValue(pos.getZ());
        id = getBlockPos().asLong();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}
    @Override
    public void remove() {
        super.remove();
        removeConnections(false);
    }

    public void removeConnections(boolean client) {
        if (!level.isClientSide()) {
            TFMGPackets.getChannel().send(PacketDistributor.ALL.noArg(), new CablePacket(this.getBlockPos()));
        } else if(!client)
            return;

        for (CableConnection connection : connections) {
            ItemEntity itemToDrop = new ItemEntity(level, getBlockPos().getX() + 0.5f, getBlockPos().getY() + 0.5f, getBlockPos().getZ() + 0.5f, new ItemStack(connection.type.wire.get(), (int) (connection.getLength())));
            if (itemToDrop.getItem().getCount() > 0) {
                level.addFreshEntity(itemToDrop);
            }
            BlockPos pos = connection.blockPos1 == getBlockPos() ? connection.blockPos2 : connection.blockPos1;
            if (level.getBlockEntity(pos) instanceof CableConnectorBlockEntity be) {

                if(be.getBlockPos() == getBlockPos())
                    continue;
                be.connections.removeIf(c->c.blockPos1==getBlockPos()||c.blockPos2==getBlockPos());
                be.setChanged();
                be.sendData();
                setChanged();
                sendData();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (player != null)
            managePlayerWire();
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);

        compound.putInt("ConnectionCount", connections.size());

        for (int i = 0; i < connections.size(); i++) {
            CableConnection connection = connections.get(i);
            compound.put("Connection" + i, connection.saveConnection());
            //
        }

    }

    @Override
    public void lazyTick() {
        super.lazyTick();

    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        connections = new ArrayList<>();
        for (int i = 0; i < compound.getInt("ConnectionCount"); i++) {

            connections.add(CableConnection.loadConnection(compound.getCompound("Connection" + i)));
        }

    }

    public void managePlayerWire() {
        wireMovementX.chase(player.getX() - .5, 0.7, LerpedFloat.Chaser.EXP);
        wireMovementY.chase(player.getY() + (player.isCrouching() ? 0.6 : 0.9), 0.3, LerpedFloat.Chaser.EXP);
        wireMovementZ.chase(player.getZ() - .5, 0.7, LerpedFloat.Chaser.EXP);
        wireMovementX.tickChaser();
        wireMovementY.tickChaser();
        wireMovementZ.tickChaser();
    }

    public CablePos getCablePosition() {
        return new CablePos(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos()).inflate(30);
    }

}
