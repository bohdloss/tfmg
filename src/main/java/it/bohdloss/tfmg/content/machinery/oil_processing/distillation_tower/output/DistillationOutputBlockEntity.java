package it.bohdloss.tfmg.content.machinery.oil_processing.distillation_tower.output;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.base.TFMGFluidBehavior;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGIcons;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.List;

@EventBusSubscriber
public class DistillationOutputBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public TFMGFluidBehavior fluid;
    public ScrollOptionBehaviour<DistillationOutputMode> mode;


    public DistillationOutputBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        fluid = new TFMGFluidBehavior(TFMGFluidBehavior.TYPE, "Fluid", this, 8000)
                .allowExtraction(true)
                .allowInsertion(false)
                .syncCapacity(false)
                .withCallback(this::notifyUpdate);
        mode = new ScrollOptionBehaviour<>(
                DistillationOutputMode.class,
                CreateLang.translateDirect("distillation_output.when_tank_is_full"),
                this,
                new DistillationOutputValueBox()
        );

        behaviours.add(fluid);
        behaviours.add(mode);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                TFMGBlockEntities.STEEL_DISTILLATION_OUTPUT.get(),
                (be, ctx) -> be.fluid.getCapability()
        );
    }

    public static class DistillationOutputValueBox extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 16.05);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis().isHorizontal();
        }
    }

    public enum DistillationOutputMode implements INamedIconOptions {
        KEEP_FLUID(TFMGIcons.DISTILLATION_OUTPUT_ICON_DO_NOT_VOID),
        VOID_WHEN_FULL(TFMGIcons.DISTILLATION_OUTPUT_ICON_VOID);

        final AllIcons icon;

        DistillationOutputMode(AllIcons icon){
            this.icon = icon;
        }

        @Override
        public AllIcons getIcon() {
            return icon;
        }

        @Override
        public String getTranslationKey() {
            return "distillation_output.mode."+ CreateLang.asId(name());
        }
    }
}
