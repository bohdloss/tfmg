package it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.hammer;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.config.TFMGConfigs;
import it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.base.PumpjackBaseBlockEntity;
import it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.crank.PumpjackCrankBlockEntity;
import it.bohdloss.tfmg.mixin.contraptions.MechanicalBearingBlockEntityAccessor;
import it.bohdloss.tfmg.registry.TFMGTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;

import java.util.List;

import static net.minecraft.world.level.block.DirectionalBlock.FACING;

public class PumpjackBlockEntity extends MechanicalBearingBlockEntity {
    public BlockPos headPosition = null;
    public BlockPos connectorPosition = null;
    public BlockPos crankPosition = null;
    public BlockPos basePosition = null;

    public boolean connectorAtFront = false;
    public boolean headAtFront = false;

    public int connectorDistance = 0;
    public int headDistance = 0;
    public int crankConnectorDistance = 0;
    public int headBaseDistance = 0;
    public float heightModifier = 0;
    public float crankRadius = 0.7f;

    public PumpjackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(3);
        sequencedAngleLimit = -1;
    }

    @Override
    public boolean isWoodenTop() {
        return false;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(7);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        registerAwardables(behaviours, AllAdvancements.CONTRAPTION_ACTORS);
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (connectorPosition != null) {
            compound.put("Connector", NbtUtils.writeBlockPos(connectorPosition));
        }
        if (headPosition != null) {
            compound.put("Head", NbtUtils.writeBlockPos(headPosition));
        }
        if (crankPosition != null) {
            compound.put("Crank", NbtUtils.writeBlockPos(crankPosition));
        }
        if (basePosition != null) {
            compound.put("Base", NbtUtils.writeBlockPos(basePosition));
        }
        compound.putBoolean("connectorAtFront", connectorAtFront);
        compound.putBoolean("headAtFront", headAtFront);

        if(!clientPacket) {
            compound.putInt("ConnectorDistance", connectorDistance);
            compound.putInt("HeadDistance", headDistance);
            compound.putInt("CrankConnectorDistance", crankConnectorDistance);
            compound.putInt("HeadBaseDistance", headBaseDistance);
            compound.putFloat("HeightModifier", heightModifier);
            compound.putFloat("CrankRadius", crankRadius);
        }

        super.write(compound, registries, clientPacket);
    }

    @Override
    public void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (wasMoved) {
            super.read(compound, registries, clientPacket);
            return;
        }
        connectorPosition = null;
        if(compound.contains("Connector")) {
            connectorPosition = NbtUtils.readBlockPos(compound, "Connector").get();
        }
        headPosition = null;
        if(compound.contains("Head")) {
            headPosition = NbtUtils.readBlockPos(compound, "Head").get();
        }
        crankPosition = null;
        if(compound.contains("Crank")) {
            crankPosition = NbtUtils.readBlockPos(compound, "Crank").get();
        }
        basePosition = null;
        if(compound.contains("Base")) {
            basePosition = NbtUtils.readBlockPos(compound, "Base").get();
        }
        connectorAtFront = compound.getBoolean("connectorAtFront");
        headAtFront = compound.getBoolean("headAtFront");

        if(!clientPacket) {
            connectorDistance = compound.getInt("ConnectorDistance");
            headDistance = compound.getInt("HeadDistance");
            crankConnectorDistance = compound.getInt("CrankConnectorDistance");
            headBaseDistance = compound.getInt("HeadBaseDistance");
            heightModifier = compound.getFloat("HeightModifier");
            crankRadius = compound.getFloat("CrankRadius");
        }

        super.read(compound, registries, clientPacket);
    }

    public void assemble() {
        if(!isComplete()) {
            return;
        }
        if (!(level.getBlockState(worldPosition).getBlock() instanceof BearingBlock)) {
            return;
        }
        Direction direction = getBlockState().getValue(BearingBlock.FACING);
        PumpjackContraption contraption = new PumpjackContraption(direction);
        try {
            if (!contraption.assemble(level, worldPosition)) {
                return;
            }
            lastException = null;
        } catch (AssemblyException e) {
            lastException = e;
            sendData();
            return;
        }
        int q = 1;
        if (direction.getAxis() == Direction.Axis.X)
            q = -1;
        boolean canAssemble = true;
        boolean foundHead = false;
        boolean foundConnector = false;
        BlockPos headLocalPos = headPosition.subtract(getBlockPos().above());
        for (StructureTemplate.StructureBlockInfo block : contraption.getBlocks().values()) {
            if (block.state().is(TFMGTags.TFMGBlockTags.PUMPJACK_HEAD.tag)) {
                foundHead = true;
                if (block.pos().getX() != headLocalPos.getX() ||
                        block.pos().getY() != q * headLocalPos.getY() ||
                        block.pos().getZ() != q * headLocalPos.getZ())
                    canAssemble = false;

            }
        }
        BlockPos connectorLocalPos = connectorPosition.subtract(getBlockPos().above());
        for (StructureTemplate.StructureBlockInfo block : contraption.getBlocks().values()) {
            if (block.state().is(TFMGTags.TFMGBlockTags.PUMPJACK_CONNECTOR.tag)) {
                foundConnector = true;
                if (block.pos().getX() != connectorLocalPos.getX() ||
                        block.pos().getY() != q * connectorLocalPos.getY() ||
                        block.pos().getZ() != q * connectorLocalPos.getZ())
                    canAssemble = false;
            }
        }
        if (!canAssemble || !foundHead || !foundConnector) {
            return;
        }
        contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
        movedContraption = ControlledContraptionEntity.create(level, this, contraption);
        BlockPos anchor = worldPosition.above();
        movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
        movedContraption.setRotationAxis(direction.getClockWise().getAxis());
        level.addFreshEntity(movedContraption);
        AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playOnServer(level, worldPosition);
        if (contraption.containsBlockBreakers())
            award(AllAdvancements.CONTRAPTION_ACTORS);
        running = true;
        angle = 0;
        sendData();
        updateGeneratedRotation();
    }

    @Override
    public void remove() {
        super.remove();
        if(level == null) {
            return;
        }
        // Remove dangling (blockpos) references
        if(basePosition != null && level.getBlockEntity(basePosition) instanceof PumpjackBaseBlockEntity be) {
            be.pumpjackPosition = null;
        }
        if(crankPosition != null && level.getBlockEntity(crankPosition) instanceof PumpjackCrankBlockEntity be) {
            be.pumpjackPosition = null;
        }
    }

    public boolean isHead(BlockPos pos) {
        return level.getBlockState(pos).is(TFMGTags.TFMGBlockTags.PUMPJACK_HEAD.tag);
    }

    public boolean isPart(BlockPos pos) {
        return level.getBlockState(pos).is(TFMGTags.TFMGBlockTags.PUMPJACK_PART.tag);
    }

    public boolean isConnector(BlockPos pos) {
        return level.getBlockState(pos).is(TFMGTags.TFMGBlockTags.PUMPJACK_CONNECTOR.tag);
    }

    @Override
    public void disassemble() {
        if (!running && movedContraption == null) {
            return;
        }
        connectorDistance = 0;
        headDistance = 0;
        angle = 0;
        sequencedAngleLimit = -1;
        if (movedContraption != null) {
            movedContraption.disassemble();
            AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playOnServer(level, worldPosition);
        }
        movedContraption = null;
        running = false;
        updateGeneratedRotation();
        assembleNextTick = false;
        sendData();
    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide) {
            if (!running && updateAssemblyRequirements(false)) {
                // Conditions are right for assembly
                assemble();
            }
            if (running) {
                // The conditions changed in an unfavorable way
                if (!updateWorldRequirements(connectorPosition, headPosition, false)) {
                    disassemble();
                }
            }
            // Fix block shape
            setHolderSize();
        }

        Direction direction = getBlockState().getValue(BearingBlock.FACING);
        if (connectorPosition != null) {
            connectorDistance = Math.abs(getBlockPos().get(direction.getAxis()) - connectorPosition.get(direction.getAxis()));
            if (crankPosition != null && level.getBlockEntity(crankPosition) instanceof PumpjackCrankBlockEntity be) {
                heightModifier = (float) (crankRadius * Math.sin(Math.toRadians(be.angle)));
                crankConnectorDistance = Math.abs(crankPosition.getY() - connectorPosition.getY());
                crankRadius = (float) connectorDistance / 5;
            }
        }
        if (headPosition != null) {
            headDistance = Math.abs(getBlockPos().get(direction.getAxis()) - headPosition.get(direction.getAxis()));
            if (basePosition != null) {
                headBaseDistance = Math.abs(basePosition.getY() - headPosition.getY());
            }
        }

        ((MechanicalBearingBlockEntityAccessor) this).setPrevAngle(angle);
        if (level.isClientSide) {
            clientAngleDiff /= 2;
        }

        if (!level.isClientSide && assembleNextTick) {
            assembleNextTick = false;
            if (running) {
                if (speed == 0 && (movedContraption == null || movedContraption.getContraption().getBlocks().isEmpty())) {
                    if (movedContraption != null) {
                        movedContraption.getContraption().stop(level);
                    }
                    disassemble();
                    return;
                }
            } else {
                if (speed == 0) {
                    return;
                }
                assemble();
            }
        }

        if (!running) {
            return;
        }

        if (!(movedContraption != null && movedContraption.isStalled())) {
            if (crankPosition != null) {
                PumpjackCrankBlockEntity crank = (PumpjackCrankBlockEntity) level.getBlockEntity(crankPosition);
                int x = 1;
                if (connectorAtFront) {
                    x = -1;
                }
                if (direction == Direction.SOUTH || direction == Direction.WEST) {
                    angle = (float) Math.toDegrees(Math.atan(heightModifier * x / connectorDistance));
                } else {
                    angle = (float) Math.toDegrees(Math.atan(-heightModifier * x / connectorDistance));
                }
            }
        }

        applyRotation();
    }

    @Override
    protected void applyRotation() {
        if (movedContraption == null) {
            return;
        }
        movedContraption.setAngle(angle);
        BlockState blockState = getBlockState();
        if (blockState.hasProperty(BlockStateProperties.FACING)) {
            movedContraption.setRotationAxis(blockState.getValue(BlockStateProperties.FACING).getClockWise().getAxis());
        }
    }

    public void setHolderSize() {
        if (isRunning()) {
            return;
        }

        BlockPos pos = getBlockPos();
        BlockState above = level.getBlockState(pos.above());
        BlockState state = getBlockState();

        boolean shouldBeWide = !above.is(TFMGTags.TFMGBlockTags.PUMPJACK_SMALL_PART.tag) && !above.isAir();
        if(state.getValue(PumpjackBlock.WIDE) != shouldBeWide) {
            TFMGUtils.changeProperty(level, pos, PumpjackBlock.WIDE, shouldBeWide);
        }
    }

    public boolean isComplete() {
        return connectorPosition != null &&
                headPosition != null &&
                crankPosition != null &&
                basePosition != null;
    }

    protected boolean updateWorldRequirements(BlockPos connector, BlockPos head, boolean simulate) {
        BlockPos pos = getBlockPos();
        BlockPos crank = findCrank(connector);
        BlockPos base = findBase(head);

        if(crank == null || base == null) {
            return false;
        }
        PumpjackCrankBlockEntity crankBE = (PumpjackCrankBlockEntity) level.getBlockEntity(crank);
        PumpjackBaseBlockEntity baseBE = (PumpjackBaseBlockEntity) level.getBlockEntity(base);

        if(crankBE.pumpjackPosition != null && !crankBE.pumpjackPosition.equals(pos)) {
            return false;
        }
        if(baseBE.pumpjackPosition != null && !baseBE.pumpjackPosition.equals(pos)) {
            return false;
        }

        if(!simulate) {
            crankPosition = crank;
            basePosition = base;
            crankBE.pumpjackPosition = getBlockPos();
            baseBE.pumpjackPosition = getBlockPos();
            crankBE.notifyUpdate();
            baseBE.notifyUpdate();
            notifyUpdate();
        }

        return true;
    }

    protected boolean updateAssemblyRequirements(boolean simulate) {
        BlockPos pos = getBlockPos();
        Direction direction = getBlockState().getValue(FACING);
        BlockPos anchorPos = pos.above();
        BlockPos connector = findConnector(anchorPos);
        BlockPos head = findHead(anchorPos);

        if(connector == null || head == null) {
            return false;
        }
        boolean multiplier = !direction.getAxisDirection().equals(Direction.AxisDirection.NEGATIVE);
        boolean connectorAtFront = connector.get(direction.getAxis()) < anchorPos.get(direction.getAxis()) ^ multiplier;
        boolean headAtFront = head.get(direction.getAxis()) < anchorPos.get(direction.getAxis()) ^ multiplier;

        if(connectorAtFront == headAtFront) {
            return false;
        }

        if(!updateWorldRequirements(connector, head, simulate)) {
            return false;
        }

        if(!simulate) {
            this.connectorAtFront = connectorAtFront;
            this.headAtFront = headAtFront;
            connectorPosition = connector;
            headPosition = head;
            notifyUpdate();
        }

        return true;
    }

    public BlockPos findConnector(BlockPos anchorPos) {
        Direction direction = getBlockState().getValue(FACING);
        int max = maxLength();
        if(!isPart(anchorPos)) {
            return null;
        }
        BlockPos currentPos = anchorPos;
        for(int i = 0; i < max; i++) {
            if (isConnector(currentPos)) {
                return currentPos;
            }
            if(!isPart(currentPos)) {
                break;
            }
            currentPos = currentPos.relative(direction);
        }
        currentPos = anchorPos;
        for(int i = 0; i < max; i++) {
            if (isConnector(currentPos)) {
                return currentPos;
            }
            if(!isPart(currentPos)) {
                break;
            }
            currentPos = currentPos.relative(direction.getOpposite());
        }
        return null;
    }

    public BlockPos findCrank(BlockPos connectorPosition) {
        int max = maxHeight();
        BlockPos currentPos = connectorPosition.below();
        for (int i = 0; i < max; i++) {
            if (level.getBlockEntity(currentPos) instanceof PumpjackCrankBlockEntity) {
                if (level.getBlockState(currentPos).getValue(HorizontalDirectionalBlock.FACING).getAxis() == this.getBlockState().getValue(FACING).getAxis()) {
                    return currentPos;
                }
            }
            currentPos = currentPos.below();
        }
        return null;
    }

    public BlockPos findHead(BlockPos anchorPos) {
        Direction direction = getBlockState().getValue(FACING);
        int max = maxLength();
        if(!isPart(anchorPos)) {
            return null;
        }
        BlockPos currentPos = anchorPos;
        for(int i = 0; i < max; i++) {
            if (isHead(currentPos)) {
                return currentPos;
            }
            if(!isPart(currentPos)) {
                break;
            }
            currentPos = currentPos.relative(direction);
        }
        currentPos = anchorPos;
        for(int i = 0; i < max; i++) {
            if (isHead(currentPos)) {
                return currentPos;
            }
            if(!isPart(currentPos)) {
                break;
            }
            currentPos = currentPos.relative(direction.getOpposite());
        }
        return null;
    }

    public BlockPos findBase(BlockPos headPosition) {
        int max = maxHeight();
        BlockPos currentPos = headPosition.below();
        for (int i = 0; i < max; i++) {
            if (level.getBlockEntity(currentPos) instanceof PumpjackBaseBlockEntity) {
                return currentPos;
            }
            currentPos = currentPos.below();
        }
        return null;
    }

    @Override
    public void attach(ControlledContraptionEntity contraption) {
        BlockState blockState = getBlockState();
        if (!(contraption.getContraption() instanceof PumpjackContraption))
            return;
        if (!blockState.hasProperty(BearingBlock.FACING))
            return;

        this.movedContraption = contraption;
        setChanged();
        BlockPos anchor = worldPosition.above();
        movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
        if (!level.isClientSide) {
            this.running = true;
            sendData();
        }
    }

    protected int maxHeight() {
        return TFMGConfigs.common().machines.pumpjackMaxHeight.get();
    }

    protected int maxLength() {
        return TFMGConfigs.common().machines.pumpjackMaxLength.get();
    }
}
