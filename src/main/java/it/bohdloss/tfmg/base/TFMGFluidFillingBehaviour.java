package it.bohdloss.tfmg.base;

import com.simibubi.create.content.fluids.transfer.FluidFillingBehaviour;
import com.simibubi.create.content.fluids.transfer.FluidManipulationBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import it.bohdloss.tfmg.mixin.fluid_handling.FluidManipulationBehaviourAccessor;
import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.BBHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.LevelTicks;

import java.util.*;

// Taken and adapted from Create's FluidFillingBehaviour
public class TFMGFluidFillingBehaviour extends FluidManipulationBehaviour {

    static final int searchedPerTick = 1024;
    static final int validationTimerMin = 160;
    public static final BehaviourType<FluidFillingBehaviour> TYPE = new BehaviourType<>();

    PriorityQueue<BlockPosEntry> queue;

    List<BlockPosEntry> infinityCheckFrontier;
    Set<BlockPos> infinityCheckVisited;

    public TFMGFluidFillingBehaviour(SmartBlockEntity be) {
        super(be);
        queue = new ObjectHeapPriorityQueue<>((p, p2) -> -comparePositions(p, p2));
        self().setRevalidateIn(1);
        infinityCheckFrontier = new ArrayList<>();
        infinityCheckVisited = new HashSet<>();
    }

    protected FluidManipulationBehaviourAccessor self() {
        return (FluidManipulationBehaviourAccessor) this;
    }

    @Override
    public void tick() {
        super.tick();
        var self = self();
        if (!infinityCheckFrontier.isEmpty() && self.getRootPos() != null) {
            Fluid fluid = getWorld().getFluidState(self.getRootPos())
                    .getType();
            if (fluid != Fluids.EMPTY)
                continueValidation(fluid);
        }

        int revalidateIn = self.getRevalidateIn();
        if (revalidateIn > 0) {
            revalidateIn--;
            self.setRevalidateIn(revalidateIn);
        }

    }

    protected void continueValidation(Fluid fluid) {
        var self = self();
        try {
            search(fluid, infinityCheckFrontier, infinityCheckVisited,
                    (p, d) -> infinityCheckFrontier.add(new BlockPosEntry(p, d)), true);
        } catch (ChunkNotLoadedException e) {
            infinityCheckFrontier.clear();
            infinityCheckVisited.clear();
            setLongValidationTimer();
            return;
        }

        int maxBlocks = maxBlocks();

        if (infinityCheckVisited.size() > maxBlocks && maxBlocks != -1 && !fillInfinite()) {
            if (!self.getInfinite()) {
                reset();
                self.setInfinite(true);
                blockEntity.sendData();
            }
            infinityCheckFrontier.clear();
            setLongValidationTimer();
            return;
        }

        if (!infinityCheckFrontier.isEmpty())
            return;
        if (self.getInfinite()) {
            reset();
            return;
        }

        infinityCheckVisited.clear();
    }

    public boolean tryDeposit(Fluid fluid, BlockPos root, boolean simulate) {
        var self = self();
        if (!Objects.equals(root, self.getRootPos())) {
            reset();
            self.setRootPos(root);
            queue.enqueue(new BlockPosEntry(root, 0));
            self.setAffectedArea(BoundingBox.fromCorners(self.getRootPos(), self.getRootPos()));
            return false;
        }

        if (counterpartActed) {
            counterpartActed = false;
            softReset(root);
            return false;
        }

        if (self.getAffectedArea() == null)
            self.setAffectedArea(BoundingBox.fromCorners(root, root));

        if (self.getRevalidateIn() == 0) {
            self.getVisited().clear();
            infinityCheckFrontier.clear();
            infinityCheckVisited.clear();
            infinityCheckFrontier.add(new BlockPosEntry(root, 0));
            setValidationTimer();
            softReset(root);
        }

        Level world = getWorld();
        int maxRange = maxRange();
        int maxRangeSq = maxRange * maxRange;
        int maxBlocks = maxBlocks();
        boolean evaporate = world.dimensionType()
                .ultraWarm() && FluidHelper.isTag(fluid, FluidTags.WATER);
        boolean canPlaceSources = AllConfigs.server().fluids.fluidFillPlaceFluidSourceBlocks.get();

        if ((!fillInfinite() && self.getInfinite()) || evaporate || !canPlaceSources) {
            FluidState fluidState = world.getFluidState(self.getRootPos());
            boolean equivalentTo = fluidState.getType()
                    .isSame(fluid);
            if (!equivalentTo && !evaporate && canPlaceSources)
                return false;
            if (simulate)
                return true;
            playEffect(world, root, fluid, false);
            if (evaporate) {
                int i = root.getX();
                int j = root.getY();
                int k = root.getZ();
                world.playSound(null, i, j, k, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F,
                        2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
            } else if (!canPlaceSources)
                blockEntity.award(AllAdvancements.HOSE_PULLEY);
            return true;
        }

        boolean success = false;
        for (int i = 0; !success && !queue.isEmpty() && i < searchedPerTick; i++) {
            BlockPosEntry entry = queue.first();
            BlockPos currentPos = entry.pos();

            if (self.getVisited().contains(currentPos)) {
                queue.dequeue();
                continue;
            }

            if (!simulate)
                self.getVisited().add(currentPos);

            if (self.getVisited().size() >= maxBlocks && maxBlocks != -1) {
                self.setInfinite(true);
                if (!fillInfinite()) {
                    self.getVisited().clear();
                    queue.clear();
                    return false;
                }
            }

            SpaceType spaceType = getAtPos(world, currentPos, fluid);
            if (spaceType == SpaceType.BLOCKING)
                continue;
            if (spaceType == SpaceType.FILLABLE) {
                success = true;
                if (!simulate) {
                    playEffect(world, currentPos, fluid, false);

                    BlockState blockState = world.getBlockState(currentPos);
                    if (canFluidLog(blockState, fluid)) {
                        if (!blockEntity.isVirtual())
                            world.setBlock(currentPos,
                                    updatePostWaterlogging(doFluidLog(blockState, fluid)),
                                    2 | 16);
                    } else {
                        replaceBlock(world, currentPos, blockState);
                        if (!blockEntity.isVirtual())
                            world.setBlock(currentPos, FluidHelper.convertToStill(fluid)
                                    .defaultFluidState()
                                    .createLegacyBlock(), 2 | 16);
                    }

                    LevelTickAccess<Fluid> pendingFluidTicks = world.getFluidTicks();
                    if (pendingFluidTicks instanceof LevelTicks<Fluid> serverTickList) {
                        serverTickList.clearArea(new BoundingBox(currentPos));
                    }

                    self.setAffectedArea(BBHelper.encapsulate(self.getAffectedArea(), currentPos));
                }
            }

            if (simulate && success)
                return true;

            self.getVisited().add(currentPos);
            queue.dequeue();

            for (Direction side : Iterate.directions) {
                if (side == Direction.UP)
                    continue;

                BlockPos offsetPos = currentPos.relative(side);
                if (self.getVisited().contains(offsetPos))
                    continue;
                if (offsetPos.distSqr(self.getRootPos()) > maxRangeSq)
                    continue;

                SpaceType nextSpaceType = getAtPos(world, offsetPos, fluid);
                if (nextSpaceType != SpaceType.BLOCKING)
                    queue.enqueue(new BlockPosEntry(offsetPos, entry.distance() + 1));
            }
        }

        if (!simulate && success)
            blockEntity.award(AllAdvancements.HOSE_PULLEY);
        return success;
    }

    protected void softReset(BlockPos root) {
        var self = self();
        self.getVisited().clear();
        queue.clear();
        queue.enqueue(new BlockPosEntry(root, 0));
        self.setInfinite(false);
        setValidationTimer();
        blockEntity.sendData();
    }

    protected enum SpaceType {
        FILLABLE, FILLED, BLOCKING
    }

    protected SpaceType getAtPos(Level world, BlockPos pos, Fluid toFill) {
        BlockState blockState = world.getBlockState(pos);
        FluidState fluidState = blockState.getFluidState();

        if (canFluidLog(blockState))
            return canFluidLog(blockState, toFill)
                    ? isFluidLogged(blockState, toFill) ? SpaceType.FILLED : SpaceType.FILLABLE
                    : SpaceType.BLOCKING;

        if (blockState.getBlock() instanceof LiquidBlock)
            return blockState.getValue(LiquidBlock.LEVEL) == 0
                    ? toFill.isSame(fluidState.getType()) ? SpaceType.FILLED : SpaceType.BLOCKING
                    : SpaceType.FILLABLE;

        if (fluidState.getType() != Fluids.EMPTY
                && blockState.getCollisionShape(getWorld(), pos, CollisionContext.empty())
                .isEmpty())
            return toFill.isSame(fluidState.getType()) ? SpaceType.FILLED : SpaceType.BLOCKING;

        return canBeReplacedByFluid(world, pos, blockState) ? SpaceType.FILLABLE : SpaceType.BLOCKING;
    }

    protected void replaceBlock(Level world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropResources(state, world, pos, blockEntity);
    }

    // From FlowingFluidBlock#isBlocked
    protected boolean canBeReplacedByFluid(BlockGetter world, BlockPos pos, BlockState pState) {
        Block block = pState.getBlock();
        if (!(block instanceof DoorBlock) && !pState.is(BlockTags.ALL_SIGNS) && !pState.is(Blocks.LADDER)
                && !pState.is(Blocks.SUGAR_CANE) && !pState.is(Blocks.BUBBLE_COLUMN)) {
            if (!pState.is(Blocks.NETHER_PORTAL) && !pState.is(Blocks.END_PORTAL) && !pState.is(Blocks.END_GATEWAY)
                    && !pState.is(Blocks.STRUCTURE_VOID)) {
                return !pState.blocksMotion();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected BlockState updatePostWaterlogging(BlockState state) {
        if (state.hasProperty(BlockStateProperties.LIT))
            state = state.setValue(BlockStateProperties.LIT, false);
        return state;
    }

    protected boolean canFluidLog(BlockState blockState, Fluid fluid) {
        if(!fluid.isSame(Fluids.WATER)) {
            return false;
        }
        return canFluidLog(blockState) && fluid.isSame(Fluids.WATER);
    }

    protected boolean canFluidLog(BlockState blockState) {
        return blockState.hasProperty(BlockStateProperties.WATERLOGGED);
    }

    protected BlockState doFluidLog(BlockState blockState, Fluid fluid) {
        return blockState.setValue(BlockStateProperties.WATERLOGGED, true);
    }

    protected boolean isFluidLogged(BlockState blockState, Fluid fluid) {
        return blockState.getValue(BlockStateProperties.WATERLOGGED);
    }

    @Override
    public void reset() {
        super.reset();
        queue.clear();
        infinityCheckFrontier.clear();
        infinityCheckVisited.clear();
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

}