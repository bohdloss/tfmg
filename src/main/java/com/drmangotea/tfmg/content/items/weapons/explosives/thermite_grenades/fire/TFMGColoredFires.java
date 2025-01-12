package com.drmangotea.tfmg.content.items.weapons.explosives.thermite_grenades.fire;

import com.drmangotea.tfmg.TFMG;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * aint making blockstates with registrate for these
 */
public class TFMGColoredFires {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TFMG.MOD_ID);


    public static final RegistryObject<Block> GREEN_FIRE = BLOCKS.register("green_fire",
            () -> new GreenFireBlock(BlockBehaviour.Properties.copy(Blocks.FIRE)
            .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .lightLevel(s -> 15)
    ));
    public static final RegistryObject<Block> BLUE_FIRE = BLOCKS.register("blue_fire",
            () -> new BlueFireBlock(BlockBehaviour.Properties.copy(Blocks.FIRE)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .lightLevel(s -> 15)
            ));
    public static final RegistryObject<Block> LITHIUM_FIRE = BLOCKS.register("lithium_fire",
            () -> new LithiumFireBlock(BlockBehaviour.Properties.copy(Blocks.FIRE)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .lightLevel(s -> 15)
            ));


    public static void register (IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
