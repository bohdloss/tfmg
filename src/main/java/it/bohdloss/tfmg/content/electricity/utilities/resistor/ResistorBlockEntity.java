package it.bohdloss.tfmg.content.electricity.utilities.resistor;

import it.bohdloss.tfmg.content.electricity.base.CurrentCalculation;
import it.bohdloss.tfmg.content.electricity.base.ElectricBlockEntity;
import it.bohdloss.tfmg.content.electricity.base.ElectricData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ResistorBlockEntity extends ElectricBlockEntity {
    public int resistance = 0;

    public ResistorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected ElectricData instantiateElectric() {
        return new ElectricData(this) {
            @Override
            public boolean hasConnectorTowards(Direction direction) {
                return getBlockState().getValue(ResistorBlock.FACING).equals(direction.getOpposite());
            }

            @Override
            public CurrentCalculation getResistance() {
                return CurrentCalculation.resistance(resistance);
            }
        };
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        tag.putInt("Resistance", resistance);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        resistance = tag.getInt("Resistance");
    }
}
