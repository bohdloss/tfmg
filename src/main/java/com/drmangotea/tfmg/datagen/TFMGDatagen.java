package com.drmangotea.tfmg.datagen;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.base.TFMGRegistrateTags;
import com.drmangotea.tfmg.datagen.recipes.TFMGProcessingRecipeGen;
import com.drmangotea.tfmg.datagen.recipes.values.CastingRecipeGen;
import com.drmangotea.tfmg.datagen.recipes.values.TFMGStandardRecipeGen;
import com.drmangotea.tfmg.datagen.recipes.values.IndustrialBlastingRecipeGen;
import com.drmangotea.tfmg.datagen.recipes.values.VatRecipeGen;
import com.drmangotea.tfmg.ponder.TFMGPonderIndex;
import com.drmangotea.tfmg.ponder.TFMGPonderTag;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import com.simibubi.create.infrastructure.ponder.GeneralText;
import com.simibubi.create.infrastructure.ponder.PonderIndex;
import com.simibubi.create.infrastructure.ponder.SharedText;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static com.drmangotea.tfmg.TFMG.REGISTRATE;

public class TFMGDatagen {

    public static void gatherData(GatherDataEvent event) {
        addExtraRegistrateData();
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        if (event.includeServer()) {
            TFMGGeneratedEntriesProvider generatedEntriesProvider = new TFMGGeneratedEntriesProvider(output, lookupProvider);
            //lookupProvider = generatedEntriesProvider.getRegistryProvider();
            generator.addProvider(true, generatedEntriesProvider);

            //generator.addProvider(true, new DamageTypeTagGen(output, lookupProvider, existingFileHelper));
            generator.addProvider(true, new IndustrialBlastingRecipeGen(output));
            generator.addProvider(true, new CastingRecipeGen(output));
            generator.addProvider(true, new VatRecipeGen(output));
            generator.addProvider(true, new TFMGStandardRecipeGen(output));
            TFMGProcessingRecipeGen.registerAll(generator, output);
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
        TFMGPonderIndex.registerTags();
        TFMGPonderIndex.register();
        PonderLocalization.generateSceneLang();

        PonderLocalization.provideLang(TFMG.MOD_ID, consumer);
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
