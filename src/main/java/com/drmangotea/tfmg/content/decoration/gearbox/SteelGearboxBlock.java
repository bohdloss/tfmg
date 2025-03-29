package com.drmangotea.tfmg.content.decoration.gearbox;


import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;

public class SteelGearboxBlock extends RotatedPillarKineticBlock implements IBE<GearboxBlockEntity> {

	public SteelGearboxBlock(Properties properties) {
		super(properties);
	}
	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.PUSH_ONLY;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos,
			Player player) {
		if (state.getValue(AXIS).isVertical())
			return super.getCloneItemStack(state, target, world, pos, player);

		return new ItemStack(TFMGItems.STEEL_VERTICAL_GEARBOX.get());
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(AXIS, Axis.Y);
	}

	// IRotate:

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() != state.getValue(AXIS);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(AXIS);
	}

	@Override
	public Class<GearboxBlockEntity> getBlockEntityClass() {
		return GearboxBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends GearboxBlockEntity> getBlockEntityType() {
		return TFMGBlockEntities.STEEL_GEARBOX.get();
	}
}
