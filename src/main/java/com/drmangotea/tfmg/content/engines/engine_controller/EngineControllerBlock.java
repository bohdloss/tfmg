package com.drmangotea.tfmg.content.engines.engine_controller;

import com.drmangotea.tfmg.base.TFMGHorizontalDirectionalBlock;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.redstone.link.controller.LecternControllerBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class EngineControllerBlock extends TFMGHorizontalDirectionalBlock implements IBE<EngineControllerBlockEntity>, IWrenchable {
    public EngineControllerBlock(Properties p_54120_) {
        super(p_54120_);
    }
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        if (!player.isShiftKeyDown() && EngineControllerBlockEntity.playerInRange(player, world, pos)) {
            if (!world.isClientSide)
                withBlockEntityDo(world, pos, be -> be.tryStartUsing(player));
            return InteractionResult.SUCCESS;
        }


        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return onBlockEntityUse(context.getLevel(), context.getClickedPos(), be -> be.use(context.getPlayer()));
    }

    @Override
    public Class<EngineControllerBlockEntity> getBlockEntityClass() {
        return EngineControllerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends EngineControllerBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.ENGINE_CONTROLLER.get();
    }
}
