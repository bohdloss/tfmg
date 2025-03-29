package com.drmangotea.tfmg.content.machinery.misc.winding_machine;

import com.drmangotea.tfmg.base.TFMGUtils;
import com.drmangotea.tfmg.content.electricity.connection.cables.CableConnection;
import com.drmangotea.tfmg.content.electricity.connection.cables.CableConnectorBlockEntity;
import com.drmangotea.tfmg.content.electricity.connection.cables.CablePos;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

import static com.simibubi.create.foundation.utility.Debug.debugMessage;

public class SpoolItem extends Item {

    public final PartialModel model;
    public final int barColor;
    public final CableConnection.CableType type;

    public SpoolItem(Properties properties, PartialModel model, int barColor, CableConnection.CableType type) {
        super(properties);
        this.model = model;
        this.barColor = barColor;
        this.type = type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isCrouching() && stack.getOrCreateTag().getLong("Position") != 0) {
            if (level.getBlockEntity(BlockPos.of(stack.getOrCreateTag().getLong("Position"))) instanceof CableConnectorBlockEntity be)
                be.player = null;
            stack.getOrCreateTag().putLong("Position", 0);
            stack.getOrCreateTag().remove("Position");
            stack.getOrCreateTag().remove("XPos");
            stack.getOrCreateTag().remove("YPos");
            stack.getOrCreateTag().remove("ZPos");
            if (level.isClientSide)
                player.displayClientMessage(CreateLang.translateDirect("wires.removed_data")
                        .withStyle(ChatFormatting.YELLOW), true);
            return InteractionResultHolder.success(stack);

        }

        return super.use(level, player, hand);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(CreateLang.translateDirect("tooltip.coils", stack.getOrCreateTag().getInt("Amount"))
                .withStyle(ChatFormatting.GREEN)
        );
        BlockPos pos = BlockPos.of(stack.getOrCreateTag().getLong("Position"));
        if(pos.asLong()!=0)
            tooltip.add(CreateLang.text("" + pos.getX() + " " + pos.getY() + " " + pos.getZ()).component()
                    .withStyle(ChatFormatting.AQUA)
            );
        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if(level.isClientSide)
            return InteractionResult.SUCCESS;

        if(type == CableConnection.CableType.NONE)
            return InteractionResult.PASS;

         if(level.getBlockEntity(pos) instanceof CableConnectorBlockEntity be){
             if(stack.getOrCreateTag().getLong("Position")!=0){
                 BlockPos posToConnect = BlockPos.of(stack.getOrCreateTag().getLong("Position"));
                 if(posToConnect == pos){
                     stack.getOrCreateTag().putLong("Position",0);
                     if (level.isClientSide)
                         player.displayClientMessage(CreateLang.translateDirect("wires.cant_connect_itself")
                                 .withStyle(ChatFormatting.YELLOW), true);
                     be.player = null;
                     be.sendData();
                     be.setChanged();
                     return InteractionResult.SUCCESS;
                 }

                 if(level.getBlockEntity(posToConnect) instanceof CableConnectorBlockEntity otherBE) {
                     //CableConnectorBlockEntity connectedBe1 = pos.asLong()>posToConnect.asLong() ? otherBE : be;
                     //CableConnectorBlockEntity connectedBe2= pos.asLong()>posToConnect.asLong() ? be : otherBE;
//
                     CableConnection connection1 = new CableConnection(be.getCablePosition(), otherBE.getCablePosition(), otherBE.getBlockPos(),type,true);
                     CableConnection connection2 = new CableConnection(otherBE.getCablePosition(), be.getCablePosition(), be.getBlockPos(),type,false);
                     if(be.connections.contains(connection1)||otherBE.connections.contains(connection1)){
                         if (level.isClientSide)
                             player.displayClientMessage(CreateLang.translateDirect("wires.connection_already_created")
                                     .withStyle(ChatFormatting.YELLOW), true);
                         be.player = null;
                         be.sendData();
                         be.setChanged();
                         return InteractionResult.SUCCESS;
                     }
                     if(!level.isClientSide) {
                         be.connections.add(connection1);
                         otherBE.connections.add(connection2);
                         be.onPlaced();
                     }

                   //  connectedBe1.wiresUpdated();

                     be.player = null;
                     otherBE.player = null;
                     be.setChanged();
                     otherBE.setChanged();
                     be.sendData();
                     otherBE.sendData();
                     stack.getOrCreateTag().remove("Position");
                     stack.getOrCreateTag().remove("XPos");
                     stack.getOrCreateTag().remove("YPos");
                     stack.getOrCreateTag().remove("ZPos");
                 }
                 //
                 be.player = null;
//
                 return InteractionResult.SUCCESS;
             }else {
                 stack.getOrCreateTag().putLong("Position", be.getBlockPos().asLong());
                 stack.getOrCreateTag().putDouble("XPos", be.getCablePosition().x());
                 stack.getOrCreateTag().putDouble("YPos", be.getCablePosition().y());
                 stack.getOrCreateTag().putDouble("ZPos", be.getCablePosition().z());
                 be.player = player;
                 be.color = barColor;
                 be.sendData();
                 be.setChanged();
                 return InteractionResult.SUCCESS;
             }
         }
//
//
         if(level.getBlockEntity(pos) instanceof WindingMachineBlockEntity be){
             ItemStack oldSpool = ItemStack.EMPTY;
             if(!be.spool.isEmpty()){
                 oldSpool = be.spool;
             }
             be.spool = context.getItemInHand();
             context.getPlayer().setItemInHand(context.getHand(), oldSpool);
             be.sendData();
             be.setChanged();
//
             return InteractionResult.SUCCESS;
         }
         return InteractionResult.PASS;
         }
        public void removeOtherConnections(Player player, ItemStack stack){
            for(int i =0;i<player.getInventory().getContainerSize();i++){
                ItemStack inventoryStack = player.getInventory().getItem(i);
//

//
            }
        }



    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean p_41408_) {
        super.inventoryTick(stack, level, entity, slot, p_41408_);

        if(stack.getOrCreateTag().getInt("Amount")==0&& entity instanceof Player player&&!stack.is(TFMGItems.EMPTY_SPOOL.get())){
            player.getInventory().setItem(slot, TFMGItems.EMPTY_SPOOL.asStack());
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return model != null;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return barColor;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return (int) (13f*((float)stack.getOrCreateTag().getInt("Amount")/1000));
    }

}
