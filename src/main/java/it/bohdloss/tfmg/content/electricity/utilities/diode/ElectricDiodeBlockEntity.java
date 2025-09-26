package it.bohdloss.tfmg.content.electricity.utilities.diode;

import it.bohdloss.tfmg.content.electricity.base.ElectricBlockEntity;
import it.bohdloss.tfmg.content.electricity.base.ElectricData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

import static it.bohdloss.tfmg.content.electricity.lights.LightBulbBlock.FACING;

public class ElectricDiodeBlockEntity extends ElectricBlockEntity {
    public ElectricDiodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected ElectricData instantiateElectric() {
        return new ElectricData(this) {
            @Override
            public boolean hasConnectorTowards(Direction direction) {
                return direction.getAxis() == getBlockState().getValue(FACING).getAxis();
            }

            @Override
            public void getOutputConnections(Set<BlockPos> outputs) {
                outputs.add(getBlockPos().relative(getBlockState().getValue(FACING)));
            }

            @Override
            public float getInputOutputVoltageMultiplier() {
                return 1;
            }

            @Override
            public float getOutputInputVoltageMultiplier() {
                return 0;
            }
        };
    }
}
