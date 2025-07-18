package it.bohdloss.tfmg.base;

import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import it.bohdloss.tfmg.DebugStuff;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TFMGRecipeBehavior<I extends TFMGRecipeInput, T extends StandardProcessingRecipe<I>> extends BlockEntityBehaviour {
    public static final BehaviourType<TFMGRecipeBehavior<?, ?>> TYPE = new BehaviourType<>();

    protected final RecipeType<T> recipeType;
    protected Runnable updateCallback;
    protected Supplier<I> inputSupplier;

    protected Predicate<Pair<List<ItemStack>, List<FluidStack>>> canFitResults;
    protected Consumer<Pair<List<ItemStack>, List<FluidStack>>> acceptResults;
    protected Function<Integer, Integer> durationModifier;

    @Nullable
    protected ResourceLocation currentRecipe;
    protected RecipeHolder<T> recipeCache;
    public int timer = -1;
    protected int recipeDuration = -1;
    protected int laziness = 0;
    protected final NonNullList<Pair<Integer, Integer>> shrinkItems = NonNullList.create();
    protected final NonNullList<Pair<Fluid, Integer>> drainFluids = NonNullList.create();

    public TFMGRecipeBehavior(SmartBlockEntity be, RecipeType<T> recipeType) {
        super(be);
        this.recipeType = recipeType;
        this.updateCallback = () -> {};
        this.inputSupplier = () -> null;
        this.canFitResults = x -> false;
        this.acceptResults = x -> {};
        this.durationModifier = x -> x;
    }

    public TFMGRecipeBehavior<I, T> withCallback(Runnable updateCallback) {
        this.updateCallback = updateCallback == null ? () -> {} : updateCallback;
        return this;
    }

    public TFMGRecipeBehavior<I, T> withInput(Supplier<I> inputSupplier) {
        this.inputSupplier = inputSupplier == null ? () -> null : inputSupplier;
        return this;
    }

    public TFMGRecipeBehavior<I, T> withCheckFreeSpace(Predicate<Pair<List<ItemStack>, List<FluidStack>>> canFitResults) {
        this.canFitResults = canFitResults == null ? x -> false : canFitResults;
        return this;
    }

    public TFMGRecipeBehavior<I, T> withResultsDo(Consumer<Pair<List<ItemStack>, List<FluidStack>>> acceptResults) {
        this.acceptResults = acceptResults == null ? x -> {} : acceptResults;
        return this;
    }

    public TFMGRecipeBehavior<I, T> withDurationModifier(Function<Integer, Integer> durationModifier) {
        this.durationModifier = durationModifier == null ? x -> x : durationModifier;
        return this;
    }

    // We use this instead of tick because the block entity might only want to update the recipe on the controller
    public void update() {
        if(laziness > 0) {
            laziness--;
            return;
        }
        recipeTick();
    }

    public int getRecipeDuration() {
        return recipeDuration;
    }

    public void reset() {
        laziness = 0;
        timer = -1;
        recipeDuration = -1;
        currentRecipe = null;
    }

    public void updateRecipe() {
        Optional<RecipeHolder<T>> recipe = blockEntity.getLevel().getRecipeManager().getRecipeFor(
                recipeType,
                inputSupplier.get(),
                blockEntity.getLevel(),
                recipeCache
        );
        if(recipe.isPresent()) {
            // Recipe went from none to some || or it changed -> reset timer to processing time
            if(currentRecipe == null || !currentRecipe.equals(recipe.get().id())) {
                int processingDuration = recipe.get().value().getProcessingDuration();
                timer = durationModifier.apply(processingDuration);
                recipeDuration = timer;
                laziness = 0;
            }
            currentRecipe = recipe.get().id();
            recipeCache = recipe.get();
        } else {
            // Recipe went from some to none -> we aren't processing anything so set timer to -1
            if(currentRecipe != null) {
                timer = -1;
                recipeDuration = -1;
                laziness = 0;
            }
            currentRecipe = null;
        }
    }

    @SuppressWarnings("unchecked")
    public RecipeHolder<T> getRecipe() {
        if(currentRecipe == null) {
            return null;
        }
        if(recipeCache != null && recipeCache.id().equals(currentRecipe)) {
            return recipeCache;
        }
        Optional<RecipeHolder<?>> recipe = blockEntity.getLevel().getRecipeManager().byKey(currentRecipe);
        if(recipe.isPresent()) {
            recipeCache = (RecipeHolder<T>) recipe.get();
            return recipeCache;
        }

        return null;
    }

    protected void recipeTick() {
        RecipeHolder<T> holder = getRecipe();
        if(holder == null) {
            timer = -1;
            recipeDuration = -1;
            return;
        }
        T theRecipe = holder.value();
        I theInput = inputSupplier.get();

        if(!theInput.hasIngredients(theRecipe, shrinkItems, drainFluids)) {
            laziness = 5; // Get lazy with it
            timer = -1;
            return;
        }

        if(timer == -1) {
            int processingDuration = holder.value().getProcessingDuration();
            timer = durationModifier.apply(processingDuration);
            recipeDuration = timer;
        }

        if(timer == 0) {
            List<ItemStack> itemResults = theRecipe.rollResults();
            itemResults.addFirst(theRecipe.getResultItem(blockEntity.getLevel().registryAccess()));

            NonNullList<FluidStack> fluidResults = theRecipe.getFluidResults();

            Pair<List<ItemStack>, List<FluidStack>> theResultsInQuestion = Pair.of(itemResults, fluidResults);

            // Will these results fit?
            if(!canFitResults.test(theResultsInQuestion)) {
                laziness = 5; // Get lazy with it
                return;
            }

            // Reset the duration
            int processingDuration = holder.value().getProcessingDuration();
            timer = durationModifier.apply(processingDuration);
            recipeDuration = timer;

            // Finally execute the recipe
            theInput.useInputs(shrinkItems, drainFluids);
            acceptResults.accept(theResultsInQuestion);

            updateRecipe();
        } else if(timer != -1) {
            timer = Math.max(0, timer - 1);

            this.updateCallback.run();
        }
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);

        nbt.putInt("Timer", timer);
        nbt.putInt("RecipeDuration", recipeDuration);
        if (currentRecipe != null) {
            ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, currentRecipe).ifSuccess(tag -> {
                nbt.put("CurrentRecipe", tag);
            });
        }
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);

        timer = nbt.getInt("Timer");
        recipeDuration = nbt.getInt("RecipeDuration");
        currentRecipe = null;
        if (nbt.contains("CurrentRecipe")) {
            ResourceLocation.CODEC.decode(NbtOps.INSTANCE, nbt.get("CurrentRecipe")).ifSuccess(res -> {
                currentRecipe = res.getFirst();
            });
        }
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
