package it.bohdloss.tfmg.content.electricity;

import com.simibubi.create.api.registry.SimpleRegistry;
import net.minecraft.world.level.block.Block;

import java.util.function.DoubleSupplier;

public class BlockResistanceValues {
    public static final SimpleRegistry<Block, DoubleSupplier> RESISTANCES = SimpleRegistry.create();
    public static final SimpleRegistry<Block, DoubleSupplier> GENERATOR_RESISTANCES = SimpleRegistry.create();

    public static double getResistance(Block block) {
        DoubleSupplier supplier = RESISTANCES.get(block);
        return supplier == null ? 0 : supplier.getAsDouble();
    }

    public static double getGeneratorResistance(Block block) {
        DoubleSupplier supplier = GENERATOR_RESISTANCES.get(block);
        return supplier == null ? 0 : supplier.getAsDouble();
    }
}
