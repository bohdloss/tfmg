package com.drmangotea.tfmg.content.electricity.measurement;


import com.drmangotea.tfmg.base.TFMGHorizontalDirectionalBlock;
import com.drmangotea.tfmg.base.TFMGShapes;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class AmmeterBlock extends TFMGHorizontalDirectionalBlock implements IBE<AmmeterBlockEntity>, IWrenchable {

    public AmmeterBlock(Properties p_54120_) {
        super(p_54120_);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter worldIn, BlockPos pos, CollisionContext context) {

        return TFMGShapes.VOLTMETER.get(pState.getValue(FACING));
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();


        return onBlockEntityUse(level, pos, be -> {

            be.range = be.getRange() + 100;

            if(be.getRange() > 2000)
                be.range = 100;

            return InteractionResult.SUCCESS;


        });
    }

    @Override
    public Class<AmmeterBlockEntity> getBlockEntityClass() {
        return AmmeterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AmmeterBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.AMMETER.get();
    }
}
