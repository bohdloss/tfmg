package it.bohdloss.tfmg.content.decoration.flywheels;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGPartialModels;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class TFMGFlywheelBlock extends RotatedPillarKineticBlock implements IBE<TFMGFlywheelBlockEntity> {
    public final PartialModel model;

    public static TFMGFlywheelBlock steel(Properties properties){
        return new TFMGFlywheelBlock(properties, TFMGPartialModels.STEEL_FLYWHEEL);
    }
    public static TFMGFlywheelBlock aluminum(Properties properties){
        return new TFMGFlywheelBlock(properties, TFMGPartialModels.ALUMINUM_FLYWHEEL);
    }
    public static TFMGFlywheelBlock cast_iron(Properties properties){
        return new TFMGFlywheelBlock(properties, TFMGPartialModels.CAST_IRON_FLYWHEEL);
    }
    public static TFMGFlywheelBlock lead(Properties properties){
        return new TFMGFlywheelBlock(properties, TFMGPartialModels.LEAD_FLYWHEEL);
    }
    public static TFMGFlywheelBlock nickel(Properties properties){
        return new TFMGFlywheelBlock(properties, TFMGPartialModels.NICKEL_FLYWHEEL);
    }

    public TFMGFlywheelBlock(Properties properties, PartialModel model) {
        super(properties);
        this.model = model;
    }

    @Override
    public Class<TFMGFlywheelBlockEntity> getBlockEntityClass() {
        return TFMGFlywheelBlockEntity.class;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel,
                                        @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return AllShapes.LARGE_GEAR.get(pState.getValue(AXIS));
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntityType<? extends TFMGFlywheelBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.TFMG_FLYWHEEL.get();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == getRotationAxis(state);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public float getParticleTargetRadius() {
        return 2f;
    }

    @Override
    public float getParticleInitialRadius() {
        return 1.75f;
    }
}
