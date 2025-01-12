package com.drmangotea.tfmg.content.engines;



import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.content.machinery.metallurgy.coke_oven.CokeOvenBlock;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

import static com.drmangotea.tfmg.content.engines.EngineBlock.ENGINE_STATE;
import static com.drmangotea.tfmg.content.machinery.metallurgy.coke_oven.CokeOvenBlock.CONTROLLER_TYPE;


public class EngineGenerator extends SpecialBlockStateGen {
    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return horizontalAngle(state.getValue(EngineBlock.HORIZONTAL_FACING).getOpposite());
    }


    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                BlockState state) {

        String path = "block/" +ctx.getName()+"/block_"
                + state.getValue(ENGINE_STATE).getSerializedName()
                ;

        return prov.models()
                .getExistingFile(TFMG.asResource(path));

    }
}
