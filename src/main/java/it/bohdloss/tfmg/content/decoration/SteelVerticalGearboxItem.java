package it.bohdloss.tfmg.content.decoration;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.gearbox.VerticalGearboxItem;
import it.bohdloss.tfmg.registry.TFMGBlocks;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SteelVerticalGearboxItem extends BlockItem {
    public SteelVerticalGearboxItem(Properties builder) {
        super(TFMGBlocks.STEEL_GEARBOX.get(), builder);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "item.create.vertical_gearbox";
    }

    @Override
    public void registerBlocks(@NotNull Map<Block, Item> map, @NotNull Item item) {}

    @Override
    protected boolean updateCustomBlockEntityTag(@NotNull BlockPos pos, @NotNull Level world, Player player, @NotNull ItemStack stack, @NotNull BlockState state) {
        Direction.Axis prefferedAxis = null;
        for (Direction side : Iterate.horizontalDirections) {
            BlockState blockState = world.getBlockState(pos.relative(side));
            if (blockState.getBlock() instanceof IRotate) {
                if (((IRotate) blockState.getBlock()).hasShaftTowards(world, pos.relative(side), blockState,
                        side.getOpposite()))
                    if (prefferedAxis != null && prefferedAxis != side.getAxis()) {
                        prefferedAxis = null;
                        break;
                    } else {
                        prefferedAxis = side.getAxis();
                    }
            }
        }

        Direction.Axis axis = prefferedAxis == null ? player.getDirection()
                .getClockWise()
                .getAxis() : prefferedAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.AXIS, axis));
        return super.updateCustomBlockEntityTag(pos, world, player, stack, state);
    }
}
