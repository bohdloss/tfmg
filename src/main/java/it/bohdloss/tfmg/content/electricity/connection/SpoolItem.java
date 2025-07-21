package it.bohdloss.tfmg.content.electricity.connection;

import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.base.IWind;
import it.bohdloss.tfmg.registry.TFMGDataComponents;
import it.bohdloss.tfmg.registry.TFMGItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpoolItem extends Item implements IWind {
    public final int barColor;

    public SpoolItem(Properties properties, int barColor) {
        super(properties);
        this.barColor = barColor;
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        if(this != TFMGItems.EMPTY_SPOOL.get()) {
            setWindings(stack, SpoolAmount.MAX.amount());
        }
        return stack;
    }

    @Override
    public void onCraftedBy(@NotNull ItemStack stack, @NotNull Level level, @NotNull Player player) {
        setWindings(stack, SpoolAmount.MAX.amount());
        super.onCraftedBy(stack, level, player);

    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        WireSelection selection = stack.get(TFMGDataComponents.WIRE_SELECTION);
        if (player.isCrouching() && selection != null) {
//            if (level.getBlockEntity(selection.pos()) instanceof CableConnectorBlockEntity be) {
//                be.player = null;
//            }

            stack.remove(TFMGDataComponents.WIRE_SELECTION);
            if (level.isClientSide)
                player.displayClientMessage(CreateLang.translateDirect("wires.removed_data")
                        .withStyle(ChatFormatting.YELLOW), true);
            return InteractionResultHolder.success(stack);

        }

        return super.use(level, player, hand);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(CreateLang.translateDirect("tooltip.coils", stack.getOrDefault(TFMGDataComponents.SPOOL_AMOUNT, SpoolAmount.DEFAULT).amount())
                .withStyle(ChatFormatting.GREEN)
        );
        WireSelection selection = stack.get(TFMGDataComponents.WIRE_SELECTION);
        if(selection != null) {
            BlockPos pos = selection.pos();
            tooltipComponents.add(CreateLang.text("" + pos.getX() + " " + pos.getY() + " " + pos.getZ()).component()
                    .withStyle(ChatFormatting.AQUA)
            );
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        if (this == TFMGItems.EMPTY_SPOOL.get()) {
            return InteractionResult.PASS;
        }

//        if (level.getBlockEntity(pos) instanceof CableConnectorBlockEntity be) {
//            WireSelection selection = stack.get(TFMGDataComponents.WIRE_SELECTION);
//            if (selection != null) {
//                BlockPos posToConnect = BlockPos.of(stack.getOrCreateTag().getLong("Position"));
//                if (posToConnect.equals(pos)) {
//                    stack.getOrCreateTag().putLong("Position", 0);
//                    if (level.isClientSide)
//                        player.displayClientMessage(CreateLang.translateDirect("wires.cant_connect_itself")
//                                .withStyle(ChatFormatting.YELLOW), true);
//                    be.player = null;
//                    be.sendData();
//                    be.setChanged();
//                    return InteractionResult.SUCCESS;
//                }
//
//                if (level.getBlockEntity(posToConnect) instanceof CableConnectorBlockEntity otherBE) {
//                    //CableConnectorBlockEntity connectedBe1 = pos.asLong()>posToConnect.asLong() ? otherBE : be;
//                    //CableConnectorBlockEntity connectedBe2= pos.asLong()>posToConnect.asLong() ? be : otherBE;
//                    CableType cableType = TFMGUtils.getCableType(cableTypeKey);
////
//                    CableConnection connection1 = new CableConnection(be.getCablePosition(), otherBE.getCablePosition(), otherBE.getBlockPos(), cableType, true);
//                    CableConnection connection2 = new CableConnection(otherBE.getCablePosition(), be.getCablePosition(), be.getBlockPos(), cableType, false);
//
//                    float wireCost = (connection1.getLength() / 8);
//
//
//                    if (stack.getOrCreateTag().getInt("Amount") < wireCost * 125) {
//                        return InteractionResult.PASS;
//                    }
//                    if (be.connections.contains(connection1) || otherBE.connections.contains(connection1)) {
//                        if (level.isClientSide)
//                            player.displayClientMessage(CreateLang.translateDirect("wires.connection_already_created")
//                                    .withStyle(ChatFormatting.YELLOW), true);
//                        be.player = null;
//                        be.sendData();
//                        be.setChanged();
//                        return InteractionResult.SUCCESS;
//                    }
//                    //  if(!level.isClientSide) {
//                    be.connections.add(connection1);
//                    otherBE.connections.add(connection2);
//                    if (!level.isClientSide)
//                        TFMGPackets.getChannel().send(PacketDistributor.ALL.noArg(), new ElectricPlacementPacket(BlockPos.of(be.getPos())));
//                    be.onPlaced();
//                    if(!level.isClientSide)
//                        TFMGPackets.getChannel().send(PacketDistributor.ALL.noArg(), new ElectricPlacementPacket(BlockPos.of(otherBE.getPos())));
//                    otherBE.onPlaced();
//                    //   otherBE.onPlaced();
//                    //}
//
//                    //  connectedBe1.wiresUpdated();
//                    stack.getOrCreateTag().putInt("Amount", (int) (stack.getOrCreateTag().getInt("Amount") - (wireCost * 125)));
//                    be.player = null;
//                    otherBE.player = null;
//                    be.setChanged();
//                    otherBE.setChanged();
//                    be.sendData();
//                    otherBE.sendData();
//                    stack.getOrCreateTag().remove("Position");
//                    stack.getOrCreateTag().remove("XPos");
//                    stack.getOrCreateTag().remove("YPos");
//                    stack.getOrCreateTag().remove("ZPos");
//                }
//                //
//                be.player = null;
////
//                return InteractionResult.SUCCESS;
//            } else {
//                stack.getOrCreateTag().putLong("Position", be.getBlockPos().asLong());
//                stack.getOrCreateTag().putDouble("XPos", be.getCablePosition().x());
//                stack.getOrCreateTag().putDouble("YPos", be.getCablePosition().y());
//                stack.getOrCreateTag().putDouble("ZPos", be.getCablePosition().z());
//                be.player = player;
//                be.color = barColor;
//                be.sendData();
//                be.setChanged();
//                return InteractionResult.SUCCESS;
//            }
//        }
////
////
//        if (level.getBlockEntity(pos) instanceof WindingMachineBlockEntity be) {
//            ItemStack oldSpool = ItemStack.EMPTY;
//            if (!be.spool.isEmpty()) {
//                oldSpool = be.spool;
//            }
//            be.spool = context.getItemInHand();
//            context.getPlayer().setItemInHand(context.getHand(), oldSpool);
//            be.sendData();
//            be.setChanged();
////
//            return InteractionResult.SUCCESS;
//        }
        return InteractionResult.PASS;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slot, isSelected);

        if (getWindings(stack) == 0 &&
                entity instanceof Player player &&
                !stack.is(TFMGItems.EMPTY_SPOOL.get()))
        {
            ItemStack copy = TFMGItems.EMPTY_SPOOL.asStack();
            copy.setCount(stack.getCount());
            player.getInventory().setItem(slot, copy);
        }
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return this != TFMGItems.EMPTY_SPOOL.get() && getWindings(stack) != getMaxWindings(stack);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return barColor;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        float value = (float) getWindings(stack);
        float max = (float) getMaxWindings(stack);
        return (int) (13f * (value / max));
    }

    @Override
    public int getWindings(ItemStack itemStack) {
        return itemStack.getOrDefault(TFMGDataComponents.SPOOL_AMOUNT, SpoolAmount.DEFAULT).amount();
    }

    @Override
    public void setWindings(ItemStack itemStack, int windings) {
        itemStack.set(TFMGDataComponents.SPOOL_AMOUNT, new SpoolAmount(windings));
    }

    @Override
    public int getMaxWindings(ItemStack itemStack) {
        return SpoolAmount.MAX.amount();
    }

    @Override
    public int getRenderedColor(ItemStack itemStack) {
        return getWindings(itemStack) == 0 ? 0x61472F : barColor;
    }
}
