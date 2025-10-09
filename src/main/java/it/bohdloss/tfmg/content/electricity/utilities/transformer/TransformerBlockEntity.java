package it.bohdloss.tfmg.content.electricity.utilities.transformer;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import it.bohdloss.tfmg.DebugStuff;
import it.bohdloss.tfmg.base.IWindable;
import it.bohdloss.tfmg.base.TFMGItemBehavior;
import it.bohdloss.tfmg.content.electricity.base.ElectricBlockEntity;
import it.bohdloss.tfmg.content.electricity.base.ElectricData;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import java.util.List;
import java.util.Set;

@EventBusSubscriber
public class TransformerBlockEntity extends ElectricBlockEntity {
    public TFMGItemBehavior primaryCoil;
    public TFMGItemBehavior secondaryCoil;
    protected CombinedInvWrapper allCaps;

    public TransformerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        primaryCoil = new TFMGItemBehavior(TFMGItemBehavior.TYPE, "Primary", this, 1)
                .withValidator((slot, item) -> item.has(TFMGDataComponents.COIL_TURNS))
                .withStackSize(1)
                .allowInsertion(true)
                .allowExtraction(true)
                .withCallback(this::onIOUpdate);
        secondaryCoil = new TFMGItemBehavior(TFMGItemBehavior.SECONDARY_TYPE, "Secondary", this, 1)
                .withValidator((slot, item) -> item.has(TFMGDataComponents.COIL_TURNS))
                .withStackSize(1)
                .allowInsertion(true)
                .allowExtraction(true)
                .withCallback(this::onIOUpdate);
        allCaps = new CombinedInvWrapper(primaryCoil.getCapability(), secondaryCoil.getCapability());

        behaviours.add(primaryCoil);
        behaviours.add(secondaryCoil);
    }

    protected void onIOUpdate() {
        getElectricData().syncNextTick = true;
        notifyUpdate();
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TFMGBlockEntities.TRANSFORMER.get(),
                (be, ctx) -> {
                    if(ctx == null) {
                        return be.allCaps;
                    }
                    return null;
                }
        );
    }

    protected final float getCoilRatio() {
        if(primaryCoil.firstItem().isEmpty() ||
                secondaryCoil.firstItem().isEmpty() ||
                !primaryCoil.firstItem().has(TFMGDataComponents.COIL_TURNS) ||
                !secondaryCoil.firstItem().has(TFMGDataComponents.COIL_TURNS)) {
            return 0;
        }

        int first = primaryCoil.firstItem().get(TFMGDataComponents.COIL_TURNS).amount();
        int second = secondaryCoil.firstItem().get(TFMGDataComponents.COIL_TURNS).amount();

        if(first < 50 || second < 50) {
            return 0;
        }

        float ratio = (float) second / (float) first;
        return ratio;
    }

    @Override
    protected ElectricData instantiateElectric() {
        return new ElectricData(this) {
            @Override
            public boolean hasConnectorTowards(Direction direction) {
                return direction.getAxis() == getBlockState().getValue(TransformerBlock.FACING).getClockWise().getAxis();
            }

            @Override
            public void getOutputConnections(Set<BlockPos> outputs) {
                Direction out = getBlockState().getValue(TransformerBlock.FACING).getCounterClockWise();
                outputs.add(getBlockPos().relative(out));
            }

            @Override
            public float getOutputInputVoltageMultiplier() {
                float ratio = getCoilRatio();
                return ratio == 0 ? 0 : 1f / ratio;
            }

            @Override
            public float getInputOutputVoltageMultiplier() {
                return getCoilRatio();
            }
        };
    }
}
