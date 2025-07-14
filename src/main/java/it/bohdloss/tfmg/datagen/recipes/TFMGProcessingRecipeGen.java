package it.bohdloss.tfmg.datagen.recipes;

import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.datagen.recipes.values.tfmg.CokingRecipeGen;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class TFMGProcessingRecipeGen extends TFMGRecipeProvider {
    protected static final List<TFMGProcessingRecipeGen> GENERATORS = new ArrayList<>();

    public static void registerAll(DataGenerator gen, PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {

        GENERATORS.add(new CokingRecipeGen(output, registries));
//        GENERATORS.add(new DistillationRecipeGen(output));
//        GENERATORS.add(new WindingRecipeGen(output)); TODO
//        GENERATORS.add(new PolarizingRecipeGen(output));
//        GENERATORS.add(new HotBlastRecipeGen(output));
//        GENERATORS.add(new TFMGItemApplicationRecipeGen(output));
//        GENERATORS.add(new TFMGFillingRecipeGen(output));
//        GENERATORS.add(new TFMGMixingRecipeGen(output));
//        GENERATORS.add(new TFMGCompactingRecipeGen(output));
//        GENERATORS.add(new TFMGPressingRecipeGen(output));
//        GENERATORS.add(new TFMGCrushingRecipeGen(output));
//        GENERATORS.add(new TFMGDeployingRecipeGen(output));

        gen.addProvider(true, new DataProvider() {

            @Override
            public @NotNull String getName() {
                return "TFMG's Processing Recipes";
            }

            @Override
            public @NotNull CompletableFuture<?> run(@NotNull CachedOutput dc) {
                return CompletableFuture.allOf(GENERATORS.stream()
                        .map(gen -> gen.run(dc))
                        .toArray(CompletableFuture[]::new));
            }
        });
    }

    public TFMGProcessingRecipeGen(PackOutput generator, CompletableFuture<HolderLookup.Provider> registries) {
        super(generator, registries);
    }

    public <T extends StandardProcessingRecipe<?>> GeneratedRecipe createSingleIngredient(
            String namespace,
            Supplier<ItemLike> singleIngredient,
            UnaryOperator<StandardProcessingRecipe.Builder<T>> transform
    ) {
        StandardProcessingRecipe.Serializer<T> serializer = getSerializer();
        GeneratedRecipe generatedRecipe = c -> {
            ItemLike itemLike = singleIngredient.get();
            transform
                    .apply(new StandardProcessingRecipe.Builder<>(serializer.factory(),
                            ResourceLocation.fromNamespaceAndPath(namespace, RegisteredObjectsHelper.getKeyOrThrow(itemLike.asItem())
                                    .getPath())).withItemIngredients(Ingredient.of(itemLike)))
                    .build(c);
        };
        all.add(generatedRecipe);
        return generatedRecipe;
    }

    public  <T extends StandardProcessingRecipe<?>> GeneratedRecipe createSingleIngredient(
            Supplier<ItemLike> singleIngredient,
            UnaryOperator<StandardProcessingRecipe.Builder<T>> transform
    ) {
        return createSingleIngredient(TFMG.MOD_ID, singleIngredient, transform);
    }

    protected <T extends StandardProcessingRecipe<?>> GeneratedRecipe createWithDeferredId(
            Supplier<ResourceLocation> name,
            UnaryOperator<StandardProcessingRecipe.Builder<T>> transform
    ) {
        StandardProcessingRecipe.Serializer<T> serializer = getSerializer();
        GeneratedRecipe generatedRecipe =
                c -> transform.apply(new StandardProcessingRecipe.Builder<>(serializer.factory(), name.get()))
                        .build(c);
        all.add(generatedRecipe);
        return generatedRecipe;
    }


    public <T extends StandardProcessingRecipe<?>> GeneratedRecipe createSingleIngredient(
            ResourceLocation name,
            UnaryOperator<StandardProcessingRecipe.Builder<T>> transform
    ) {
        return createWithDeferredId(() -> name, transform);
    }


    public <T extends StandardProcessingRecipe<?>> GeneratedRecipe createSingleIngredient(
            String name,
            UnaryOperator<StandardProcessingRecipe.Builder<T>> transform
    ) {
        return createSingleIngredient(TFMG.asResource(name), transform);
    }

    protected abstract IRecipeTypeInfo getRecipeType();

    protected <T extends StandardProcessingRecipe<?>> StandardProcessingRecipe.Serializer<T> getSerializer() {
        return getRecipeType().getSerializer();
    }

    protected Supplier<ResourceLocation> idWithSuffix(Supplier<ItemLike> item, String suffix) {
        return () -> {
            ResourceLocation registryName = RegisteredObjectsHelper.getKeyOrThrow(item.get()
                    .asItem());
            return TFMG.asResource(registryName.getPath() + suffix);
        };
    }
}
