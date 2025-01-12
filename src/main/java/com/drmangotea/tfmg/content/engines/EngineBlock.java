package com.drmangotea.tfmg.content.engines;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import static com.drmangotea.tfmg.content.engines.EngineBlock.EngineState.NORMAL;
import static com.drmangotea.tfmg.content.engines.EngineBlock.EngineState.SHAFT;

public class EngineBlock extends HorizontalKineticBlock {

    public static final EnumProperty<EngineState> ENGINE_STATE = EnumProperty.create("engine_state", EngineState.class);

    public EngineBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(ENGINE_STATE, EngineState.NORMAL));
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (level.getBlockEntity(pos) instanceof AbstractEngineBlockEntity be)
            if (state.getValue(ENGINE_STATE) == SHAFT) {
                be.playRemovalSound();
                be.dropItem(AllBlocks.SHAFT.asStack());
                level.setBlock(pos, state.setValue(ENGINE_STATE, NORMAL), 2);
                be.connectNextTick = true;
                be.setChanged();
                be.sendData();
                return InteractionResult.SUCCESS;
            }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {

        if (level.getBlockEntity(pos) instanceof AbstractEngineBlockEntity be) {
            if (be.insertItem(player.getItemInHand(hand), player.isShiftKeyDown()))
                return InteractionResult.SUCCESS;
        }
        return super.use(blockState, level, pos, player, hand, blockHitResult);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return state.getValue(ENGINE_STATE) == EngineState.SHAFT && face == state.getValue(HORIZONTAL_FACING);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ENGINE_STATE);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getAxis();
    }

    enum EngineState implements StringRepresentable {

        NORMAL("normal"),
        SHAFT("front"),
        BACK("back");
        private final String name;

        EngineState(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
