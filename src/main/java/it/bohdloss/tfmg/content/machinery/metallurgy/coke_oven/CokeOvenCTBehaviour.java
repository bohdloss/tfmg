package it.bohdloss.tfmg.content.machinery.metallurgy.coke_oven;

import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import it.bohdloss.tfmg.registry.TFMGBlocks;
import it.bohdloss.tfmg.registry.TFMGSpriteShifts;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class CokeOvenCTBehaviour extends ConnectedTextureBehaviour.Base {
    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        if (direction == Direction.UP) {
            return TFMGSpriteShifts.COKE_OVEN_TOP;
        }
        if (direction == Direction.DOWN) {
            return TFMGSpriteShifts.COKE_OVEN_BOTTOM;
        }
        return TFMGSpriteShifts.COKE_OVEN_SIDE;
    }

    @Override
    protected Direction getUpDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
        if(face.getAxis().isVertical()) {
            return state.getValue(FACING).getOpposite();
        }

        return Direction.UP;
    }

    @Override
    protected Direction getRightDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
        return state.getValue(FACING).getClockWise();
    }

    public boolean buildContextForOccludedDirections() {
        return super.buildContextForOccludedDirections();
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos,
                              BlockPos otherPos, Direction face) {
        if(other.is(TFMGBlocks.COKE_OVEN.get())){
            if(other.getValue(FACING)==state.getValue(FACING)) {
                return super.connectsTo(state,other,reader,pos,otherPos,face);
            }

        }
        return false;
    }

}
