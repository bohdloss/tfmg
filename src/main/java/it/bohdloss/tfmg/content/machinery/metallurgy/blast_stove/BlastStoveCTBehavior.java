package it.bohdloss.tfmg.content.machinery.metallurgy.blast_stove;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import com.simibubi.create.foundation.block.connected.HorizontalCTBehaviour;
import it.bohdloss.tfmg.DebugStuff;
import it.bohdloss.tfmg.registry.TFMGSpriteShifts;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlastStoveCTBehavior extends HorizontalCTBehaviour {
    public BlastStoveCTBehavior(CTSpriteShiftEntry layerShift, CTSpriteShiftEntry topShift) {
        super(layerShift, topShift);
    }

    public boolean buildContextForOccludedDirections() {
        return true;
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos,
                              Direction face) {
        return state.getBlock() == other.getBlock() && ConnectivityHandler.isConnected(reader, pos, otherPos);
    }

    @Override
    public @Nullable CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        if (direction == Direction.UP) {
            return TFMGSpriteShifts.BLAST_STOVE_TOP;
        }
        return TFMGSpriteShifts.BLAST_STOVE_SIDE;
    }
}
