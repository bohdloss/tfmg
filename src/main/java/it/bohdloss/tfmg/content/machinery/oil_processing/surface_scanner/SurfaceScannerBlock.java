package it.bohdloss.tfmg.content.machinery.oil_processing.surface_scanner;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class SurfaceScannerBlock extends Block implements IBE<SurfaceScannerBlockEntity>, IWrenchable {
    public SurfaceScannerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter level, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return TFMGShapes.SLAB;
    }

    @Override
    public Class<SurfaceScannerBlockEntity> getBlockEntityClass() {
        return SurfaceScannerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SurfaceScannerBlockEntity> getBlockEntityType() {
        return TFMGBlockEntities.SURFACE_SCANNER.get();
    }
}
