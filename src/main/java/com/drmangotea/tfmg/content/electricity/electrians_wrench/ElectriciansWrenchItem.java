package com.drmangotea.tfmg.content.electricity.electrians_wrench;

import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryWandScreen;
import com.simibubi.create.foundation.gui.ScreenOpener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

public class ElectriciansWrenchItem extends Item {
    public ElectriciansWrenchItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack itemStack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (level.isClientSide) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    openWandGUI(itemStack);
                });
                player.getCooldowns()
                        .addCooldown(this, 5);
            }
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if(!context.getPlayer().isShiftKeyDown())
            if(level.getBlockEntity(pos) instanceof IElectric be){
                be.getData().group.id = context.getItemInHand().getOrCreateTag().getInt("Number");
                be.updateNextTick();
            }


        return super.useOn(context);
    }

    @OnlyIn(Dist.CLIENT)
    private void openWandGUI(ItemStack itemStack) {
        ScreenOpener.open(new ElectriciansWrenchScreen(itemStack));
    }
}
