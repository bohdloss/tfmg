package com.drmangotea.tfmg.content.electricity.generators.large_generator;


import com.drmangotea.tfmg.base.TFMGShapes;
import com.drmangotea.tfmg.content.electricity.base.ConnectNeightborsPacket;
import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.drmangotea.tfmg.registry.TFMGPackets;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PacketDistributor;

public class RotorBlock extends RotatedPillarKineticBlock implements IBE<RotorBlockEntity> {
    public RotorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }
    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == getRotationAxis(state);
    }
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return TFMGShapes.ROTOR.get(Direction.get(Direction.AxisDirection.POSITIVE,pState.getValue(AXIS)));
    }
    @Override
    public void onPlace(BlockState pState, Level level, BlockPos pos, BlockState pOldState, boolean pIsMoving) {
        withBlockEntityDo(level, pos, IElectric::onPlaced);
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
    }


    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public Class<RotorBlockEntity> getBlockEntityClass() {
        return RotorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RotorBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.ROTOR.get();
    }
}
