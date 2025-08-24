package it.bohdloss.tfmg.content.electricity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.bohdloss.tfmg.DebugStuff;
import it.bohdloss.tfmg.content.electricity.base.ElectricData;
import it.bohdloss.tfmg.content.electricity.base.IElectric;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
    public final Set<BlockPos> sources = new HashSet<>();

    // Compiled
    public float totalUsage;
    public float totalProduction;

    protected static ElectricalNetwork fromCodec(Long id, List<Member> members, Float totalUsage, Float totalProduction) {
        ElectricalNetwork self = new ElectricalNetwork(id);
        for(Member member : members) {
            self.members.put(member.pos, member);
            if(member.isSource()) {
                self.sources.add(member.pos);
            }
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
            if(member.isSource()) {
                sources.add(member.pos);
            }
            ElectricData memberData = getElectric(member.pos);
            if(memberData != null) {
                memberData.network = id;
                memberData.connectNextTick = false;
                memberData.syncNextTick = true;
            }
        }
        network.members.clear();
        network.sources.clear();
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
        sources.remove(pos);
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
            if(member.isSource()) {
                newNetwork.sources.add(member.pos);
            }
            ElectricData memberData = getElectric(member.pos);
            if(memberData != null) {
                memberData.network = newNetwork.id;
                memberData.connectNextTick = false;
                memberData.syncNextTick = true;
            }
        }
        sources.removeIf(pos -> !members.get(pos).marked);
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

        // Sync data from the component itself
        float generatedVoltage = Math.max(0, component.getGeneratedVoltage());
        float resistance = Math.max(0, component.getResistance());
        float generatorResistance = Math.max(0, component.getGeneratorResistance());

        boolean dirty = generatedVoltage != member.generatedVoltage ||
                resistance != member.resistance ||
                generatorResistance != member.generatorResistance;

        member.generatedVoltage = generatedVoltage;
        member.resistance = resistance;
        member.generatorResistance = generatorResistance;

        if(member.isSource()) {
            sources.add(member.pos);
        } else {
            sources.remove(member.pos);
        }

        // If anything changed since we last checked, we must advance the simulation
        if(dirty) {
            owner.setDirty();
            step();
        }

        // Provide component with updated data
        component.frequency = member.frequency;
        component.voltage = member.voltage;
        component.totalNetworkUsage = totalUsage;
        component.totalNetworkProduction = totalProduction;
        component.lastAmpsConsumed = member.ampsConsumed;
        component.lastAmpsProvided = member.ampsProvided;
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
        // Dummy very stupid implementation: find source with the highest voltage and use that.
        // Then sum up the max amperage of all the sources combined and use that.

        // This doesn't account for components such as diodes or components that might change the voltage along the way
        float totalAmps = 0;
        float highestVoltage = 0;
        for(BlockPos sourcePos : sources) {
            Member source = members.get(sourcePos);
            source.marked = true;

            if(source.generatedVoltage > highestVoltage) {
                highestVoltage = source.generatedVoltage;
            }

            float generatedAmps = source.calcGeneratedAmps();
            source.ampsProvided = generatedAmps;
            totalAmps += generatedAmps;
        }
        totalProduction = totalAmps;

        // Calculate total consumption for all components and apply voltage
        totalAmps = 0;

        for(Member member : members.values()) {
            member.frequency = 0;
            member.voltage = highestVoltage;
            float consumedAmps = member.calcConsumedAmps();
            member.ampsConsumed = consumedAmps;
            if(!member.isSource()) {
                member.ampsProvided = 0;
            }
            totalAmps += consumedAmps;
        }
        totalUsage = totalAmps;

        // FIXME auto update on ElectricData tick instead of this bs
        for(Member member : members.values()) {
            ElectricData data = getElectric(member.pos);
            if(data != null) {
                syncComponent(data);
                data.syncNextTick = false;
                data.notifyUpdate();
            }
        }

        owner.setDirty();
    }

    public static class Member {
        public static final Codec<Member> CODEC = RecordCodecBuilder.create(
                inst -> inst.group(
                        BlockPos.CODEC.fieldOf("Pos").forGetter(x -> x.pos),
                        Codec.list(BlockPos.CODEC).fieldOf("Connections").forGetter(x -> x.connections),
                        Codec.FLOAT.fieldOf("GeneratedVoltage").forGetter(x -> x.generatedVoltage),
                        Codec.FLOAT.fieldOf("Resistance").forGetter(x -> x.resistance),
                        Codec.FLOAT.fieldOf("GeneratorResistance").forGetter(x -> x.generatorResistance),
                        Codec.FLOAT.fieldOf("Frequency").forGetter(x -> x.frequency),
                        Codec.FLOAT.fieldOf("Voltage").forGetter(x -> x.voltage),
                        Codec.FLOAT.fieldOf("AmpsConsumed").forGetter(x -> x.ampsConsumed),
                        Codec.FLOAT.fieldOf("AmpsProvided").forGetter(x -> x.ampsProvided)
                ).apply(inst, Member::fromCodec)
        );

        public final BlockPos pos;
        public List<BlockPos> connections = new ArrayList<>();

        public boolean marked;

        // Data from the component
        public float generatedVoltage;
        public float resistance;
        public float generatorResistance;

        // Compiled
        public float frequency;
        public float voltage;
        public float ampsConsumed;
        public float ampsProvided;

        private static Member fromCodec(BlockPos pos, List<BlockPos> connections, Float generatedVoltage, Float resistance, Float generatorResistance, Float frequency, Float voltage, Float ampsConsumed, Float ampsProvided) {
            Member self = new Member(pos);
            self.connections.addAll(connections);
            self.generatedVoltage = generatedVoltage;
            self.resistance = resistance;
            self.generatorResistance = generatorResistance;
            self.frequency = frequency;
            self.voltage = voltage;
            self.ampsConsumed = ampsConsumed;
            self.ampsProvided = ampsProvided;
            return self;
        }

        public boolean isSource() {
            return generatedVoltage != 0 && generatorResistance > 0;
        }

        public float calcGeneratedAmps() {
            float amps = generatedVoltage / generatorResistance;
            return Float.isFinite(amps) ? amps : 0;
        }

        public float calcConsumedAmps() {
            float amps = voltage / resistance;
            return Float.isFinite(amps) ? amps : 0;
        }

        public Member(BlockPos pos) {
            this.pos = pos;
        }
    }
}
