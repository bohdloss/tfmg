package it.bohdloss.tfmg.content.decoration;

import com.simibubi.create.content.kinetics.gearbox.GearboxBlock;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class SteelGearboxBlock extends GearboxBlock {
    public SteelGearboxBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        if (state.getValue(AXIS).isVertical())
            return super.getCloneItemStack(state, target, level, pos, player);
        return new ItemStack(TFMGItems.STEEL_VERTICAL_GEARBOX.get());
    }

    @Override
    public BlockEntityType<? extends GearboxBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.GEARBOX.get();
    }
}
