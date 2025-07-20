package it.bohdloss.tfmg.content.machinery.metallurgy.blast_furnace;

import com.simibubi.create.Create;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.DebugStuff;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.base.*;
import it.bohdloss.tfmg.config.TFMGConfigs;
import it.bohdloss.tfmg.content.machinery.metallurgy.blast_furnace.util.BlastFurnaceValidator;
import it.bohdloss.tfmg.recipes.IndustrialBlastingRecipe;
import it.bohdloss.tfmg.registry.*;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import java.util.List;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

@EventBusSubscriber
public class BlastFurnaceOutputBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    // Items
    public TFMGItemBehavior inventory;
    public TFMGMagicInventoryBehavior fuel;
    public TFMGMagicInventoryBehavior flux;
    protected CombinedInvWrapper allInvs;

    // Fluids
    protected TFMGFluidBehavior steel;
    protected TFMGFluidBehavior slag;
    protected CombinedTankWrapper allFluids;

    // Recipe stuff
    protected TFMGRecipeBehavior<TFMGRecipeWrapper, IndustrialBlastingRecipe> recipeExecutor;
    protected TFMGRecipeWrapper input;

    public int fuelConsumeTimer = 0;

    // Don't save, prefer recalculating
    public BlockPos tuyerePos;
    public LerpedFloat coalCokeHeight = LerpedFloat.linear();
    boolean isReinforced = false;

    public BlastFurnaceOutputBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        inventory = new TFMGItemBehavior(TFMGItemBehavior.TYPE, "Inventory", this, 1)
                .allowExtraction(true) // In case the wrong item is accidentally introduced
                .allowInsertion(false)
                .withStackSize(64)
                .withCallback(this::onIOChange);
        fuel = new TFMGMagicInventoryBehavior(TFMGMagicInventoryBehavior.TYPE, "Fuel", this, 5)
                .allowExtraction(false)
                .allowInsertion(false)
                .withStackSize(64)
                .withValidator((slot, itemStack) -> itemStack.is(TFMGTags.TFMGItemTags.BLAST_FURNACE_FUEL.tag))
                .withCallback(this::onIOChange);
        flux = new TFMGMagicInventoryBehavior(TFMGMagicInventoryBehavior.SECONDARY_TYPE, "Flux", this, 5)
                .allowExtraction(false)
                .allowInsertion(false)
                .withStackSize(64)
                .withValidator((slot, itemStack) -> itemStack.is(TFMGTags.TFMGItemTags.FLUX.tag))
                .withCallback(this::onIOChange);
        steel = new TFMGFluidBehavior(TFMGFluidBehavior.TYPE, "Steel", this, 4000)
                .allowExtraction(true)
                .allowInsertion(false)
                .syncCapacity(false)
                .withCallback(this::onIOChange);
        slag = new TFMGFluidBehavior(TFMGFluidBehavior.SECONDARY_TYPE, "Slag", this, 4000)
                .allowExtraction(true)
                .allowInsertion(false)
                .syncCapacity(false)
                .withCallback(this::onIOChange);

        input = new TFMGRecipeWrapper(inventory.getHandler(), null);
        allInvs = new CombinedInvWrapper(inventory.getCapability(), fuel.getCapability(), flux.getCapability());
        allFluids = new CombinedTankWrapper(steel.getCapability(), slag.getCapability());
        recipeExecutor = new TFMGRecipeBehavior<TFMGRecipeWrapper, IndustrialBlastingRecipe>(this, TFMGRecipeTypes.INDUSTRIAL_BLASTING.getType())
                .withInput(() -> input)
                .withDurationModifier(this::calculateProcessingTime)
                .withAdditionalIngredientCheck(this::hasIngredients)
                .withAdditionalInputConsumption(this::consumeAdditionally)
                .withCheckFreeSpace(this::checkFreeSpace)
                .withResultsDo(this::acceptResults)
                .withCallback(this::notifyUpdate);

        behaviours.add(inventory);
        behaviours.add(fuel);
        behaviours.add(flux);
        behaviours.add(steel);
        behaviours.add(slag);
        behaviours.add(recipeExecutor);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TFMGBlockEntities.BLAST_FURNACE_OUTPUT.get(),
                (be, ctx) -> be.allInvs
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                TFMGBlockEntities.BLAST_FURNACE_OUTPUT.get(),
                (be, ctx) -> be.allFluids
        );
    }

    protected void onIOChange() {
        recipeExecutor.updateRecipe();
        notifyUpdate();
    }

    private int[] findCleanRatio(IndustrialBlastingRecipe recipe) {
        // Get all values from config
        int ticksPerFuel = TFMGConfigs.common().machines.blastFurnaceFuelConsumption.get();
        int actualTicks = calculateProcessingTime(recipe.getProcessingDuration());

        // Calculate the exact decimal ratio
        float exactRatio = (float)ticksPerFuel / actualTicks;

        // Find the smallest integer ratio that approximates this
        int maxIterations = 1000; // Prevents infinite loops
        float tolerance = 0.0001f; // How close we need to be

        int a = 1, b = 1;
        float currentError = Math.abs(exactRatio - ((float)a/b));

        // Farey sequence approximation
        for (int i = 0; i < maxIterations && currentError > tolerance; i++) {
            if ((float)a/b < exactRatio) {
                a++;
            } else {
                b++;
            }
            currentError = Math.abs(exactRatio - ((float)a/b));
        }

        // Check if recipe needs flux
        boolean needsFlux = recipe.needsFlux();

        // Return as [fuel, ore, flux]
        return new int[]{b, a, needsFlux ? a : 0};
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        CreateLang.translate("goggles.blast_furnace.stats", inventory.getHandler().getStackInSlot(0).getCount())
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 1);

        CreateLang.translate("goggles.blast_furnace.height", getSize())
                .forGoggles(tooltip, 1);
        CreateLang.translate("goggles.blast_furnace.fuel_amount", getTotalFuel())
                .forGoggles(tooltip, 1);

        int recipeDuration = recipeExecutor.getRecipeDuration();
        if (recipeExecutor.timer != -1 && recipeDuration != -1) {
            CreateLang.translate("goggles.blast_furnace.timer", (recipeExecutor.timer / 20) + " / " + (recipeDuration / 20))
                    .style(ChatFormatting.GOLD)
                    .forGoggles(tooltip, 1);
        }


        if (isReinforced) {
            CreateLang.translate("goggles.blast_furnace.reinforced")
                    .style(ChatFormatting.GREEN)
                    .forGoggles(tooltip);
        }

        // Dynamic batch ratio calculation for current input
        if (!inventory.getHandler().getStackInSlot(0).isEmpty()) {
            RecipeHolder<IndustrialBlastingRecipe> recipeOpt = recipeExecutor.getRecipe();

            if (recipeOpt != null) {
                IndustrialBlastingRecipe recipe = recipeOpt.value();
                int[] batch = findCleanRatio(recipe); // Uses the corrected method

                CreateLang.translate("goggles.blast_furnace.batch_header")
                        .style(ChatFormatting.GRAY)
                        .forGoggles(tooltip);

                CreateLang.translate("goggles.blast_furnace.batch_ratio",
                                batch[0], batch[1], batch[2])
                        .style(ChatFormatting.AQUA)
                        .forGoggles(tooltip);

                if (batch[1] > 64) {
                    CreateLang.translate("goggles.blast_furnace.batch_warning")
                            .style(ChatFormatting.YELLOW)
                            .forGoggles(tooltip);
                }
            }
        }

        TFMGUtils.createFluidTooltip(this, tooltip, null);
        TFMGUtils.createItemTooltip(this, tooltip, null);

        return true;

    }

    @Override
    public void tick() {
        super.tick();

        if(level.isClientSide) {
            coalCokeHeight.chase(Math.min(getTotalFuel() + inventory.getHandler().getStackInSlot(0).getCount(), 24), 0.1f, LerpedFloat.Chaser.EXP);
            coalCokeHeight.tickChaser();
            if(fuelConsumeTimer > 0) {
                makeParticles();
            }
            return;
        }

        if(getSize() < 3) {
            return;
        }

        // In order not to waste fuel, don't try to consume any if there's no active recipe
        if(fuelConsumeTimer == 0 && recipeExecutor.getRecipe() != null) {
            // Try to pull more fuel from the fuel inventory
            ItemStack fuelStack = fuel.getManagedHandler().extractItem(0, 1, false);
            if(!fuelStack.isEmpty()) {
                fuelConsumeTimer = TFMGConfigs.common().machines.blastFurnaceFuelConsumption.get();
            }

            // If we don't reset the fuel timer, the recipe executor will detect it because of hasIngredients and cancel
            // the recipe
        } else {
            fuelConsumeTimer = Math.max(0, fuelConsumeTimer - 1);
        }

        if(fuelConsumeTimer > 0 && recipeExecutor.timer > 0) {
            hurtEntities();
        }

        recipeExecutor.update();
    }

    protected int calculateProcessingTime(int baseDuration) {
        int base = baseDuration * 20;
        double timeModifier = TFMGConfigs.common().machines.blastFurnaceMaxHeight.get() /
                (((double) base / 2) * TFMGConfigs.common().machines.blastFurnaceHeightSpeedModifier.get());

        int actualTicks = (int)(base - (getSize() / timeModifier));
        return isReinforced ? actualTicks / 2 : actualTicks;
    }

    protected boolean hasIngredients(TFMGRecipeWrapper input, IndustrialBlastingRecipe recipe) {
        // We check for fuel, flux and hot air
        if(recipe.needsFlux()) {
            ItemStack fluxStack = flux.getManagedHandler().extractItem(0, 1, true);
            if(fluxStack.isEmpty()) {
                return false;
            }
        }

        if(recipe.hotAirUsage() > 0) {
            // getSize() is called inside the tick function pretty much before anything else, so this is safe
            if(!(level.getBlockEntity(tuyerePos) instanceof BlastFurnaceHatchBlockEntity be)) {
                return false;
            }
            FluidStack hotAirStack = be.tank.getHandler().drain(recipe.hotAirUsage(), IFluidHandler.FluidAction.SIMULATE);
            if(hotAirStack.isEmpty() || !hotAirStack.getFluid().isSame(TFMGFluids.HOT_AIR.getSource()) || hotAirStack.getAmount() < recipe.hotAirUsage()) {
                return false;
            }
        }

        return fuelConsumeTimer != 0;
    }

    protected boolean checkFreeSpace(List<ItemStack> items, List<FluidStack> fluids) {
        FluidStack primary = fluids.getFirst();
        FluidStack secondary = fluids.size() < 2 ? null : fluids.get(1);
        // Explicitly don't check if tertiary results don't fit.
        // If you aren't catching the gas and it flies away, thats kinda your problem.

        boolean fits = steel.getHandler().getSpace() >= primary.getAmount() &&
                (steel.getHandler().isEmpty() || steel.getHandler().getFluid().getFluid().isSame(primary.getFluid()));

        if(secondary != null) {
            fits &= slag.getHandler().getSpace() >= secondary.getAmount() &&
                    (slag.getHandler().isEmpty() || slag.getHandler().getFluid().getFluid().isSame(secondary.getFluid()));
        }

        return fits;
    }

    protected void consumeAdditionally(TFMGRecipeWrapper input, IndustrialBlastingRecipe recipe) {
        // Consume flux
        if(recipe.needsFlux()) {
            flux.getManagedHandler().extractItem(0, 1, false);
        }

        // Consume hot air
        if(recipe.hotAirUsage() > 0) {
            // This is safe because this condition has already been validated in hasIngredients earlier
            BlastFurnaceHatchBlockEntity hatch = (BlastFurnaceHatchBlockEntity) level.getBlockEntity(tuyerePos);
            hatch.tank.getHandler().drain(recipe.hotAirUsage(), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    protected void acceptResults(List<ItemStack> items, List<FluidStack> fluids) {
        FluidStack primary = fluids.getFirst();
        FluidStack secondary = fluids.size() < 2 ? null : fluids.get(1);
        FluidStack tertiary = fluids.size() < 3 ? null : fluids.get(2);

        steel.getHandler().fill(primary, IFluidHandler.FluidAction.EXECUTE);
        if(secondary != null) {
            slag.getHandler().fill(secondary, IFluidHandler.FluidAction.EXECUTE);
        }

        // Furnace gas
        if(tertiary != null) {
            if (level.getBlockEntity(getChargeHatch()) instanceof BlastFurnaceHatchBlockEntity chargeHatchBE) {
                chargeHatchBE.tank.getHandler().fill(tertiary, IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    public int getTotalFuel() {
        int totalFuel = 0;
        for(int i = 0; i < fuel.getHandler().getSlots(); i++) {
            totalFuel += fuel.getHandler().getStackInSlot(i).getCount();
        }
        return totalFuel;
    }

    public int getSize() {
        if (this.isRemoved()) {  // Critical check
            return 0;  // Skip validation if block entity is destroyed
        }
        // Create validator and validate the furnace structure
        BlastFurnaceValidator validator = new BlastFurnaceValidator(getBlockPos(), level);
        BlastFurnaceValidator.ValidationResult result = validator.validateFurnace();

        // Update entity state
        this.isReinforced = result.isReinforced();
        this.tuyerePos = validator.getTuyerePos();

        return result.height();
    }

    public BlockPos getChargeHatch() {
        return getBlockPos().relative(getBlockState().getValue(FACING).getOpposite()).above(getSize());
    }

    public void makeParticles() {
        RandomSource random = level.getRandom();
        Direction direction = getBlockState().getValue(FACING).getOpposite();
        BlockPos pos = getBlockPos().above().relative(direction);

        int shouldSpawnSmoke = random.nextInt(7);
        if (shouldSpawnSmoke == 0) {
            level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, pos.getX() + random.nextFloat() * 0.6f + 0.2, pos.getY() + 1, pos.getZ() + random.nextFloat() * 0.6f + 0.2, 0.0D, 0.08D, 0.0D);
        }
    }

    public void hurtEntities() {
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(this.getBlockPos().relative(getBlockState().getValue(FACING).getOpposite()).above()));

        for (LivingEntity entity : entities) {
            if (!entity.fireImmune()) {
                entity.setRemainingFireTicks(15 * 20);
                if (entity.hurt(TFMGDamageSources.blastFurnace(level), 4.0F)) {
                    entity.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + entity.getRandom().nextFloat() * 0.4F);
                }

            }
        }
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if(level.isClientSide || getSize() < 3) {
            return;
        }

        collectItems();
    }

    protected void collectItems() {
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(this.getBlockPos().relative(getBlockState().getValue(FACING).getOpposite()).above()));

        if (items.isEmpty()) {
            return;
        }

        for (ItemEntity itemEntity : items) {
            ItemStack itemStack = itemEntity.getItem().copy();
            if (itemStack.isEmpty()) {
                continue;
            }

            if (itemStack.is(TFMGTags.TFMGItemTags.BLAST_FURNACE_FUEL.tag)) {
                itemStack = fuel.getManagedHandler().insertItem(0, itemStack, false);
                itemEntity.setItem(itemStack.copy());
                continue;
            }

            if (itemStack.is(TFMGTags.TFMGItemTags.FLUX.tag)) {
                itemStack = flux.getManagedHandler().insertItem(0, itemStack, false);
                itemEntity.setItem(itemStack.copy());
                continue;
            }

            itemStack = inventory.getHandler().insertItem(0, itemStack, false);
            itemEntity.setItem(itemStack.copy());
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        getSize();
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        tag.putInt("FuelConsumeTimer", fuelConsumeTimer);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        fuelConsumeTimer = tag.getInt("FuelConsumeTimer");
        if(hasLevel()) {
            getSize();
        }
    }
}
