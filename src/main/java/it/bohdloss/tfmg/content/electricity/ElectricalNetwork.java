package it.bohdloss.tfmg.content.electricity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.bohdloss.tfmg.content.electricity.base.ElectricData;
import it.bohdloss.tfmg.content.electricity.base.IElectric;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElectricalNetwork {
    public static final Codec<ElectricalNetwork> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.LONG.fieldOf("Id").forGetter(i -> i.id),
                    Codec.list(Member.CODEC).fieldOf("Members").forGetter(i -> i.members.values().stream().toList()),
                    Codec.FLOAT.fieldOf("TotalUsage").forGetter(i -> i.totalUsage),
                    Codec.FLOAT.fieldOf("TotalProduction").forGetter(i -> i.totalProduction)
            ).apply(inst, ElectricalNetwork::fromCodec)
    );

    public ElectricalNetworkManager owner;
    public final long id;
    public final Map<BlockPos, Member> members = new HashMap<>();

    // Compiled
    public float totalUsage;
    public float totalProduction;

    protected static ElectricalNetwork fromCodec(Long id, List<Member> members, Float totalUsage, Float totalProduction) {
        ElectricalNetwork self = new ElectricalNetwork(id);
        for(Member member : members) {
            self.members.put(member.pos, member);
        }
        self.totalUsage = totalUsage;
        self.totalProduction = totalProduction;
        return self;
    }

    public ElectricalNetwork(long id) {
        this.id = id;
    }

    public void absorb(ElectricalNetwork network) {
        if(network.id == this.id) {
            return;
        }
        for(Member member : network.members.values()) {
            members.put(member.pos, member);
            ElectricData memberData = getElectric(member.pos);
            if(memberData != null) {
                memberData.network = id;
                memberData.connectNextTick = false;
                memberData.componentDirty = true;
            }
        }
        network.members.clear();
        owner.networks.remove(network.id);
        owner.setDirty();
    }

    public void addConnection(BlockPos member, BlockPos connection) {
        if(!members.containsKey(member)) {
            throw new IllegalStateException("Trying to *connect* component that is not part of the network");
        }
        members.get(member).connections.add(connection);
        owner.setDirty();
    }

    public void addComponent(ElectricData component) {
        if(members.containsKey(component.getBlockPos())) {
            throw new IllegalStateException("Trying to *add* component that is already part of the network");
        }
        BlockPos pos = component.getBlockPos();
        members.put(pos, new Member(pos));
        owner.setDirty();
    }

    public void removeComponent(BlockPos pos) {
        if(!members.containsKey(pos)) {
            throw new IllegalStateException("Trying to *remove* component that is not part of the network");
        }
        Member toRemove = members.get(pos);

        // Disconnect neighbors
        for(BlockPos connection : toRemove.connections) {
            Member neighbor = members.get(connection);
            neighbor.connections.remove(pos);
        }

        members.remove(pos);
        owner.setDirty();
    }

    public @Nullable ElectricalNetwork splitNetwork(BlockPos startingPos) {
        if(!members.containsKey(startingPos)) {
            throw new IllegalStateException("Trying to *split* network starting from component that is not part of the network");
        }

        markNeighborsRecursive(members.get(startingPos));

        // Move unmarked members to a new network
        ElectricalNetwork newNetwork = null;
        for(Member member : members.values()) {
            if(member.marked) {
                continue;
            }

            if(newNetwork == null) {
                newNetwork = ElectricalNetworkManager.createNewNetwork(owner.level);
            }

            newNetwork.members.put(member.pos, member);
            ElectricData memberData = getElectric(member.pos);
            if(memberData != null) {
                memberData.network = newNetwork.id;
                memberData.connectNextTick = false;
                memberData.componentDirty = true;
            }
        }
        members.values().removeIf(m -> !m.marked);
        unmarkAll();
        owner.setDirty();

        return newNetwork;
    }

    protected void markNeighborsRecursive(Member member) {
        member.marked = true;
        for(BlockPos connection : member.connections) {
            Member neighbor = members.get(connection);
            if(neighbor.marked) {
                continue;
            }

            markNeighborsRecursive(neighbor);
        }
    }

    protected void unmarkAll() {
        for(Member member : members.values()) {
            member.marked = false;
        }
    }

    public void syncComponent(ElectricData component) {
        Member member = members.get(component.getBlockPos());
        if (member == null) {
            throw new IllegalStateException("Trying to *sync* component to network it does not belong to");
        }
        component.network = id;
        component.frequency = member.frequency;
        component.voltage = member.voltage;
        component.totalNetworkUsage = totalUsage;
        component.totalNetworkProduction = totalProduction;
        component.lastAmpsConsumed = member.ampsConsumed;
        component.lastAmpsProvided = member.ampsProvided;

        owner.setDirty();
    }

    public boolean contains(BlockPos pos) {
        return members.containsKey(pos);
    }

    public @Nullable ElectricData getElectric(BlockPos pos) {
        return owner.level != null &&
                owner.level.isLoaded(pos) &&
                owner.level.getBlockEntity(pos) instanceof IElectric be ? be.getElectricData() : null;
    }

    // Advance the simulation
    public void step() {

    }

    public static class Member {
        public static final Codec<Member> CODEC = RecordCodecBuilder.create(
                inst -> inst.group(
                        BlockPos.CODEC.fieldOf("Pos").forGetter(x -> x.pos),
                        Codec.list(BlockPos.CODEC).fieldOf("Connections").forGetter(x -> x.connections),
                        Codec.FLOAT.fieldOf("Frequency").forGetter(x -> x.frequency),
                        Codec.FLOAT.fieldOf("Voltage").forGetter(x -> x.voltage),
                        Codec.FLOAT.fieldOf("AmpsConsumed").forGetter(x -> x.ampsConsumed),
                        Codec.FLOAT.fieldOf("AmpsProvided").forGetter(x -> x.ampsProvided)
                ).apply(inst, Member::fromCodec)
        );

        public final BlockPos pos;
        public List<BlockPos> connections = new ArrayList<>();

        public boolean marked;

        // Compiled
        public float frequency;
        public float voltage;
        public float ampsConsumed;
        public float ampsProvided;

        private static Member fromCodec(BlockPos pos, List<BlockPos> connections, Float frequency, Float voltage, Float ampsConsumed, Float ampsProvided) {
            Member self = new Member(pos);
            self.connections.addAll(connections);
            self.frequency = frequency;
            self.voltage = voltage;
            self.ampsConsumed = ampsConsumed;
            self.ampsProvided = ampsProvided;
            return self;
        }

        public Member(BlockPos pos) {
            this.pos = pos;
        }
    }
}
