package it.bohdloss.tfmg.content.electricity.connection.diagonal;

import it.bohdloss.tfmg.content.electricity.base.ElectricBlockEntity;
import it.bohdloss.tfmg.content.electricity.base.ElectricData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static it.bohdloss.tfmg.content.electricity.connection.diagonal.DiagonalCableBlock.FACING_PRIMARY;
import static it.bohdloss.tfmg.content.electricity.connection.diagonal.DiagonalCableBlock.FACING_SECONDARY;

public class DiagonalCableBlockEntity extends ElectricBlockEntity {
    public DiagonalCableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected ElectricData instantiateElectric() {
        return new ElectricData(this) {
            @Override
            public boolean hasConnectorTowards(Direction direction) {
                BlockState state = getBlockState();
                return direction == state.getValue(FACING_PRIMARY) || direction == state.getValue(FACING_SECONDARY);
            }
        };
    }
}
