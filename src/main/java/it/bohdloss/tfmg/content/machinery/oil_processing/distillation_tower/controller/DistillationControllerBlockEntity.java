package it.bohdloss.tfmg.content.machinery.oil_processing.distillation_tower.controller;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.base.TFMGFluidBehavior;
import it.bohdloss.tfmg.base.TFMGRecipeBehavior;
import it.bohdloss.tfmg.content.decoration.tanks.steel.DistillationData;
import it.bohdloss.tfmg.content.decoration.tanks.steel.SteelTankBlockEntity;
import it.bohdloss.tfmg.content.machinery.oil_processing.distillation_tower.output.DistillationOutputBlockEntity;
import it.bohdloss.tfmg.recipes.DistillationRecipe;
import it.bohdloss.tfmg.recipes.DistillationRecipeInput;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import it.bohdloss.tfmg.registry.TFMGTags;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

import static it.bohdloss.tfmg.recipes.DistillationRecipe.MAX_OUTPUTS;

@EventBusSubscriber
public class DistillationControllerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public TFMGFluidBehavior fluid;
    protected DistillationRecipeInput input;
    protected TFMGRecipeBehavior<DistillationRecipeInput, DistillationRecipe> recipeExecutor;

    // Temporary - not to be serialized
    protected SteelTankBlockEntity tower;
    protected List<BlockPos> outputs = new ArrayList<>(6);

    // Rendering!
    protected LerpedFloat gaugeAngle = LerpedFloat.angular();
    protected int outputLength;

    public DistillationControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        fluid = new TFMGFluidBehavior(TFMGFluidBehavior.TYPE, "Fluid", this, 8000)
                .withValidator(fluidStack -> tower != null && scanFluidValid(tower, fluidStack.getFluid()))
                .allowExtraction(true)
                .allowInsertion(true)
                .syncCapacity(false)
                .withCallback(this::onIOChange);
        input = new DistillationRecipeInput(null, fluid.getHandler(), () -> this.outputs.size());

        recipeExecutor = new TFMGRecipeBehavior<DistillationRecipeInput, DistillationRecipe>(this, TFMGRecipeTypes.DISTILLATION.getType())
                .withInput(() -> input)
                .withDurationModifier(this::recipeDuration)
                .withAdditionalIngredientCheck(this::hasIngredients)
                .withCheckFreeSpace(this::checkFreeSpace)
                .withResultsDo(this::acceptResults)
                .withCallback(this::notifyUpdate);

        behaviours.add(fluid);
        behaviours.add(recipeExecutor);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                TFMGBlockEntities.STEEL_DISTILLATION_CONTROLLER.get(),
                (be, ctx) -> be.fluid.getCapability()
        );
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        SteelTankBlockEntity tower = getTower();
        if (tower != null) {
            CreateLang.translate("goggles.distillation_tower.status")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip, 1);

            if (tower.distillationData.isActuallyActive()) {
                CreateLang.translate("goggles.distillation_tower.level", tower.distillationData.activeHeat)
                        .style(ChatFormatting.GOLD)
                        .forGoggles(tooltip, 1);
            } else {
                CreateLang.translate("goggles.distillation_tower.level", tower.distillationData.activeHeat)
                        .style(ChatFormatting.RED)
                        .forGoggles(tooltip, 1);
            }
            if (!outputs.isEmpty()) {
                CreateLang.translate("goggles.distillation_tower.found_outputs", outputLength)
                        .style(ChatFormatting.GOLD)
                        .forGoggles(tooltip, 1);
            } else {
                CreateLang.translate("goggles.distillation_tower.found_outputs", outputLength)
                        .style(ChatFormatting.RED)
                        .forGoggles(tooltip, 1);
            }
        } else {
            CreateLang.translate("goggles.distillation_tower.tank_not_found")
                    .style(ChatFormatting.RED)
                    .forGoggles(tooltip, 1);
        }
        TFMGUtils.createFluidTooltip(this, tooltip, null);

        return true;
    }

    protected void onIOChange() {
        recipeExecutor.updateRecipe();
        notifyUpdate();
    }

    protected int recipeDuration(int zero) {
        DistillationData distillationData = getTower().distillationData; // tower might be null at this point
        if(zero == 0) {
            return DistillationData.MAX_HEAT - distillationData.activeHeat;
        } else {
            return (int) ((float) zero / ((float) distillationData.activeHeat / 2f));
        }
    }

    protected boolean hasIngredients(DistillationRecipeInput input, DistillationRecipe recipe) {
        return tower.distillationData.isActuallyActive();
    }

    protected boolean checkFreeSpace(List<ItemStack> items, List<FluidStack> fluids) {
        for(int i = 0; i < outputs.size(); i++) {
            DistillationOutputBlockEntity be = (DistillationOutputBlockEntity) level.getBlockEntity(outputs.get(i)); // This will not fail

            if(be.mode.get() == DistillationOutputBlockEntity.DistillationOutputMode.KEEP_FLUID &&
                fluids.get(i).getAmount() > be.fluid.getHandler().getSpace()) {
                return false;
            }
        }
        return true;
    }

    protected void acceptResults(List<ItemStack> items, List<FluidStack> fluids) {
        for(int i = 0; i < outputs.size(); i++) {
            DistillationOutputBlockEntity be = (DistillationOutputBlockEntity) level.getBlockEntity(outputs.get(i));

            be.fluid.getHandler().fill(fluids.get(i), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if(level.isClientSide) {
            gaugeAngle.chase(180 * ((float) fluid.getHandler().getFluidAmount() / fluid.getHandler().getCapacity()), 0.2f, LerpedFloat.Chaser.EXP);
            gaugeAngle.tickChaser();
            return;
        }

        tower = getTower();
        if(tower == null) {
            return;
        }

        if(!tower.getDistillationData().isActive()) {
            return;
        }

        // Update output column
        boolean changed = discoverOutputs(outputs);
        if(changed) {
            outputLength = outputs.size();
            notifyUpdate();
        }
        if(outputs.isEmpty()) {
            return;
        }
        if(changed || (recipeExecutor.getRecipe() != null && recipeExecutor.getRecipe().value().getFluidResults().size() != outputs.size())) {
            recipeExecutor.updateRecipe();
        }

        recipeExecutor.update();
    }

    public static boolean scanFluidValid(SteelTankBlockEntity tank, Fluid fluid) {
        for(BlockPos controller : tank.distillationData.attachedControllers) {
            if(tank.getLevel().getBlockEntity(controller) instanceof DistillationControllerBlockEntity be) {
                if(!be.fluid.getHandler().isEmpty() && !be.fluid.getHandler().getFluid().getFluid().isSame(fluid)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected SteelTankBlockEntity getTower() {
        BlockPos tankPos = getBlockPos().relative(getBlockState().getValue(DistillationControllerBlock.FACING).getOpposite());
        if(level.getBlockEntity(tankPos) instanceof SteelTankBlockEntity be) {
            SteelTankBlockEntity controller = (SteelTankBlockEntity) be.getControllerBE();
            if(controller != null && controller.getBlockPos().getY() == tankPos.getY()) {
                return controller;
            }
        }
        return null;
    }

    protected boolean discoverOutputs(List<BlockPos> outputs) {
        int prevLength = outputs.size();
        outputs.clear();
        BlockPos checkedPos = this.getBlockPos().above();
        int checkHeight = MAX_OUTPUTS /*Outputs*/ + (MAX_OUTPUTS - 1) /*Pipes*/;

        for (int i = 0; i < checkHeight; i++) {
            if (i % 2 == 0) {
                if (level.getBlockEntity(checkedPos) instanceof DistillationOutputBlockEntity be) {
                    outputs.add(checkedPos);
                } else {
                    break;
                }
            } else {
                if (!(level.getBlockState(checkedPos).is(TFMGTags.TFMGBlockTags.INDUSTRIAL_PIPE.tag))) {
                    break;
                }
            }
            checkedPos = checkedPos.above();
        }
        return outputs.size() != prevLength;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        if(clientPacket) {
            tag.putInt("OutputLength", outputLength);
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        if(clientPacket) {
            outputLength = tag.getInt("OutputLength");
        }
    }
}
