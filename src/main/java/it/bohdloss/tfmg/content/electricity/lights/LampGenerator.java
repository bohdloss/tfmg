package it.bohdloss.tfmg.content.electricity.lights;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import it.bohdloss.tfmg.blocks.WallMountBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class LampGenerator extends SpecialBlockStateGen {
    public LampGenerator() {
    }

    protected int getXRotation(BlockState state) {
        short value;
        switch ((Direction)state.getValue(WallMountBlock.FACING)) {
            case NORTH, EAST, WEST, SOUTH:
                value = 90;
                break;
            case DOWN:
                value = 180;
                break;
            case UP:
                value = 0;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return value;
    }

    protected int getYRotation(BlockState state) {
        short value;
        switch ((Direction)state.getValue(WallMountBlock.FACING)) {
            case NORTH, DOWN, UP:
                value = 0;
                break;
            case SOUTH:
                value = 180;
                break;
            case WEST:
                value = 270;
                break;
            case EAST:
                value = 90;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return value;
    }

    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        return state.getValue(LightBulbBlock.LIGHT) > 0 ? AssetLookup.partialBaseModel(ctx, prov, new String[]{"powered"}) : AssetLookup.partialBaseModel(ctx, prov, new String[0]);
    }
}
