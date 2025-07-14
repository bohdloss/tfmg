package it.bohdloss.tfmg.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.datagen.recipes.TFMGProcessingRecipeGen;
import it.bohdloss.tfmg.datagen.recipes.TFMGRecipeProvider;
import it.bohdloss.tfmg.datagen.recipes.values.TFMGStandardRecipeGen;
import it.bohdloss.tfmg.registry.TFMGGeneratedEntriesProvider;
import it.bohdloss.tfmg.registry.TFMGRegistrateTags;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static it.bohdloss.tfmg.TFMG.REGISTRATE;

@EventBusSubscriber
public class TFMGDatagen {
    protected static final List<TFMGRecipeProvider> RECIPE_GENERATORS = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void gatherDataHighestPriority(GatherDataEvent event) {
        if(!event.getMods().contains(TFMG.MOD_ID)) {
            return;
        }
        addExtraRegistrateData();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void gatherDataLowestPriority(GatherDataEvent event) {
        if(!event.getMods().contains(TFMG.MOD_ID)) {
            return;
        }

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        TFMGGeneratedEntriesProvider generatedEntriesProvider = new TFMGGeneratedEntriesProvider(output, lookupProvider);
        lookupProvider = generatedEntriesProvider.getRegistryProvider();

        generator.addProvider(event.includeServer(), generatedEntriesProvider);

        //  generator.addProvider(true, new DamageTypeTagGen(output, lookupProvider, existingFileHelper));

//            RECIPE_GENERATORS.add(new IndustrialBlastingRecipeGen(output));
//            RECIPE_GENERATORS.add(new CastingRecipeGen(output));
//            RECIPE_GENERATORS.add(new VatRecipeGen(output));
        generator.addProvider(event.includeServer(), new TFMGStandardRecipeGen(output, lookupProvider));
//            RECIPE_GENERATORS.add(new TFMGMechanicalCraftingRecipeGen(output));
        //generator.addProvider(event.includeServer(), new TFMGMechanicalCraftingRecipeGen(output));
//            generator.addProvider(true, new TFMGSequencedAssemblyRecipeGen(output));

//            generator.addProvider(true, new DataProvider() {
//
//                @Override
//                public @NotNull String getName() {
//                    return "TFMG's Recipes";
//                }
//
//                @Override
//                public @NotNull CompletableFuture<?> run(@NotNull CachedOutput dc) {
//                    return CompletableFuture.allOf(RECIPE_GENERATORS.stream()
//                            .map(gen -> gen.run(dc))
//                            .toArray(CompletableFuture[]::new));
//                }
//            });

        //generator.addProvider(true, new IndustrialBlastingRecipeGen(output));
        //generator.addProvider(true, new CastingRecipeGen(output));
        //generator.addProvider(true, new VatRecipeGen(output));
        //generator.addProvider(true, new TFMGStandardRecipeGen(output));
        //generator.addProvider(true, new TFMGSequencedAssemblyRecipeGen(output));
        if (event.includeServer()) {
            TFMGProcessingRecipeGen.registerAll(generator, output, lookupProvider);
        }

    }

    private static void addExtraRegistrateData() {
        TFMGRegistrateTags.addGenerators();

        REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            BiConsumer<String, String> langConsumer = provider::add;

            provideDefaultLang("interface", langConsumer);
            provideDefaultLang("tooltips", langConsumer);

            providePonderLang(langConsumer);
        });
    }

    private static void providePonderLang(BiConsumer<String, String> consumer) {
//        PonderIndex.addPlugin(new TFMGPonderPlugin());

        PonderIndex.getLangAccess().provideLang(TFMG.MOD_ID, consumer);
    }

    private static void provideDefaultLang(String fileName, BiConsumer<String, String> consumer) {
        String path = "assets/tfmg/lang/default/" + fileName + ".json";
        JsonElement jsonElement = FilesHelper.loadJsonResource(path);
        if (jsonElement == null) {
            throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();
            consumer.accept(key, value);
        }
    }
}
