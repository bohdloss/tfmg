package it.bohdloss.tfmg.recipes;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import it.bohdloss.tfmg.base.TFMGRecipeWrapper;
import it.bohdloss.tfmg.registry.TFMGRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class IndustrialBlastingRecipe extends ProcessingRecipe<TFMGRecipeWrapper, IndustrialBlastingRecipeParams> {
    private int hotAirUsage;
    private boolean needsFlux;

    public IndustrialBlastingRecipe(IndustrialBlastingRecipeParams params) {
        super(TFMGRecipeTypes.INDUSTRIAL_BLASTING, params);
        this.hotAirUsage = params.hotAirUsage;
        this.needsFlux = params.needsFlux;
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 0;
    }
    @Override
    protected int getMaxFluidOutputCount() {
        return 3;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    public int hotAirUsage() {
        return hotAirUsage;
    }

    public boolean needsFlux() {
        return needsFlux;
    }

    public FluidStack getPrimaryResult(){
        return getFluidResults().get(0);
    }
    public FluidStack getSecondaryResult(){
        return getFluidResults().get(1);
    }
    public FluidStack getGasByproduct(){
        if(getFluidResults().size() == 3) {
            return getFluidResults().get(2);
        } else {
            return FluidStack.EMPTY;
        }
    }

    @Override
    public boolean matches(TFMGRecipeWrapper inv, @NotNull Level worldIn) {
        if (inv.isEmpty()) {
            return false;
        }
        return ingredients.get(0).test(inv.getItem(0));
    }

    @FunctionalInterface
    public interface Factory<R extends IndustrialBlastingRecipe> extends ProcessingRecipe.Factory<IndustrialBlastingRecipeParams, R> {
        R create(IndustrialBlastingRecipeParams params);
    }

    public static class Builder<R extends IndustrialBlastingRecipe> extends ProcessingRecipeBuilder<IndustrialBlastingRecipeParams, R, IndustrialBlastingRecipe.Builder<R>> {
        public Builder(IndustrialBlastingRecipe.Factory<R> factory, ResourceLocation recipeId) {
            super(factory, recipeId);
        }

        @Override
        protected IndustrialBlastingRecipeParams createParams() {
            return new IndustrialBlastingRecipeParams();
        }

        @Override
        public IndustrialBlastingRecipe.Builder<R> self() {
            return this;
        }

        public IndustrialBlastingRecipe.Builder<R> hotAirUsage(int hotAirUsage) {
            params.hotAirUsage = hotAirUsage;
            return this;
        }

        public IndustrialBlastingRecipe.Builder<R> needsFlux(boolean needsFlux) {
            params.needsFlux = needsFlux;
            return this;
        }
    }

    public static class Serializer<R extends IndustrialBlastingRecipe> implements RecipeSerializer<R> {
        private final MapCodec<R> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

        public Serializer(ProcessingRecipe.Factory<IndustrialBlastingRecipeParams, R> factory) {
            this.codec = ProcessingRecipe.codec(factory, IndustrialBlastingRecipeParams.CODEC);
            this.streamCodec = ProcessingRecipe.streamCodec(factory, IndustrialBlastingRecipeParams.STREAM_CODEC);
        }

        @Override
        public @NotNull MapCodec<R> codec() {
            return codec;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
            return streamCodec;
        }

    }
}
