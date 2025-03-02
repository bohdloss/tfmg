package com.drmangotea.tfmg.content.engines.engine_controller;

import com.drmangotea.tfmg.TFMG;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerClientHandler;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.DistExecutor;

import java.util.List;
import java.util.UUID;


public class EngineControllerBlockEntity extends SmartBlockEntity {

    private UUID user;
    private UUID prevUser;
    private boolean deactivatedThisTick;

    public EngineControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ItemStack getController() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        if (user != null)
            compound.putUUID("User", user);
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::tryToggleActive);
            prevUser = user;
        }

        if (!level.isClientSide) {
            deactivatedThisTick = false;

            if (!(level instanceof ServerLevel))
                return;
            if (user == null)
                return;

            Entity entity = ((ServerLevel) level).getEntity(user);
            if (!(entity instanceof Player)) {
                stopUsing(null);
                return;
            }

            Player player = (Player) entity;
            if (!playerInRange(player, level, worldPosition) || !playerIsUsingEngineController(player))
                stopUsing(player);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tryToggleActive() {
        if (user == null && Minecraft.getInstance().player.getUUID().equals(prevUser)) {
            EngineControllerClientHandler.deactivateInLectern();
        } else if (prevUser == null && Minecraft.getInstance().player.getUUID().equals(user)) {
            EngineControllerClientHandler.activateInLectern(worldPosition);
        }
    }

    public void tryStopUsing(Player player) {
        TFMG.LOGGER.debug("Try Stop Using");
        if (isUsedBy(player))
            stopUsing(player);
    }

    public static boolean playerIsUsingEngineController(Player player) {
        return player.getPersistentData().contains("IsUsingEngineController");
    }

    public void tryStartUsing(Player player) {


        TFMG.LOGGER.debug("TRY START USING");
        if (!playerInRange(player, level, worldPosition))
            TFMG.LOGGER.debug("Not in Range");
        if (playerIsUsingEngineController(player))
            TFMG.LOGGER.debug("Already Using");
        if (deactivatedThisTick)
            TFMG.LOGGER.debug("Deactivated");
        if (hasUser())
            TFMG.LOGGER.debug("Already has user");
        if (!deactivatedThisTick && !hasUser() && !playerIsUsingEngineController(player) && playerInRange(player, level, worldPosition)) {
            TFMG.LOGGER.debug("START USING");
            startUsing(player);
        }
    }

    private void startUsing(Player player) {

        user = player.getUUID();
        player.getPersistentData().putBoolean("IsUsingEngineController", true);
        sendData();
    }

    private void stopUsing(Player player) {
        TFMG.LOGGER.debug("STOP USING");
        user = null;
        if (player != null)
            player.getPersistentData().remove("IsUsingEngineController");
        deactivatedThisTick = true;
        sendData();
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        user = compound.hasUUID("User") ? compound.getUUID("User") : null;
    }

    public boolean isUsedBy(Player player) {
        return hasUser() && user.equals(player.getUUID());
    }

    public boolean hasUser() {
        return user != null;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public static boolean playerInRange(Player player, Level world, BlockPos pos) {
        double reach = 0.4 * player.getAttributeValue(ForgeMod.BLOCK_REACH.get());
        return player.distanceToSqr(Vec3.atCenterOf(pos)) < reach * reach;
    }
}
