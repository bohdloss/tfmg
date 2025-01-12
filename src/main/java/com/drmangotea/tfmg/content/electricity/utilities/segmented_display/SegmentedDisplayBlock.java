package com.drmangotea.tfmg.content.electricity.utilities.segmented_display;


import com.drmangotea.tfmg.base.TFMGHorizontalDirectionalBlock;
import com.drmangotea.tfmg.content.electricity.base.ConnectNeightborsPacket;
import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.drmangotea.tfmg.registry.TFMGPackets;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.BiConsumer;

public class SegmentedDisplayBlock extends TFMGHorizontalDirectionalBlock implements IBE<SegmentedDisplayBlockEntity> {




    public SegmentedDisplayBlock(Properties properties) {
        super(properties);

    }

    @Override
    public void onPlace(BlockState pState, Level level, BlockPos pos, BlockState pOldState, boolean pIsMoving) {
        withBlockEntityDo(level,pos, IElectric::onPlaced);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
    }

    public static void walkParts(LevelAccessor world, BlockPos start, BiConsumer<BlockPos, Integer> callback) {
        BlockState state = world.getBlockState(start);
        if (!(state.getBlock() instanceof SegmentedDisplayBlock))
            return;

        BlockPos currentPos = start;
        Direction left = state.getValue(FACING)
                .getClockWise();


        Direction right = left.getOpposite();

        while (true) {
            BlockPos nextPos = currentPos.relative(left);
            if (!areDisplaysEqual(world.getBlockState(nextPos), state))
                break;
            currentPos = nextPos;
        }

        int index = 0;

        while (true) {
            final int rowPosition = index;
            callback.accept(currentPos, rowPosition);
            BlockPos nextPos = currentPos.relative(right);
            if (!areDisplaysEqual(world.getBlockState(nextPos), state))
                break;
            currentPos = nextPos;
            index++;
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult ray) {
        ItemStack heldItem = player.getItemInHand(hand);
        SegmentedDisplayBlockEntity segmentedDisplay = getBlockEntity(world, pos);
        if (heldItem.isEmpty()) {

            segmentedDisplay.clearCustomText();
            return InteractionResult.SUCCESS;
        }

        boolean display =
                heldItem.getItem() == Items.NAME_TAG && heldItem.hasCustomHoverName() || AllBlocks.CLIPBOARD.isIn(heldItem);

        DyeColor dye = DyeColor.getColor(heldItem);

        if (dye != null) {
            walkParts(world, pos, (currentPos, rowPosition) -> {
                world.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                withBlockEntityDo(world, currentPos, be -> be.setColor(dye));
            });
            return InteractionResult.SUCCESS;
        }

        if(!display)
            return InteractionResult.PASS;
        CompoundTag tag = heldItem.getTagElement("display");
        String tagElement = tag != null && tag.contains("Name", Tag.TAG_STRING) ? tag.getString("Name") : null;
        String tagUsed = tagElement;
        walkParts(world, pos, (currentPos, rowPosition) -> {
            if (display)
                withBlockEntityDo(world, currentPos, be -> be.displayCustomText(tagUsed, rowPosition));

        });
        return InteractionResult.SUCCESS;
    }

    public static boolean areDisplaysEqual(BlockState blockState, BlockState otherState) {
        if (!(blockState.getBlock() instanceof SegmentedDisplayBlock))
            return false;
        return otherState.getBlock() instanceof SegmentedDisplayBlock;
    }

    @Override
    public Class<SegmentedDisplayBlockEntity> getBlockEntityClass() {
        return SegmentedDisplayBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SegmentedDisplayBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.SEGMENTED_DISPLAY.get();
    }





}
