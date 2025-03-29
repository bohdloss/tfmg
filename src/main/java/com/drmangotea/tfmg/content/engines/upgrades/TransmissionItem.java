package com.drmangotea.tfmg.content.engines.upgrades;

import com.drmangotea.tfmg.content.engines.engine_controller.EngineControllerBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class TransmissionItem extends Item {
    public TransmissionItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if(player.isCrouching()){

            stack.getOrCreateTag().remove("Position");

            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if(level.getBlockEntity(pos) instanceof EngineControllerBlockEntity be&&!context.getPlayer().isCrouching()){
            context.getItemInHand().getOrCreateTag().putLong("Position",be.getBlockPos().asLong());
        }
        return InteractionResult.PASS;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        BlockPos pos = BlockPos.of(stack.getOrCreateTag().getLong("Position"));
        if(pos.asLong()!=0)
            tooltip.add(CreateLang.text("" + pos.getX() + " " + pos.getY() + " " + pos.getZ()).component()
                    .withStyle(ChatFormatting.AQUA)
            );
        super.appendHoverText(stack, world, tooltip, flag);
    }
}
