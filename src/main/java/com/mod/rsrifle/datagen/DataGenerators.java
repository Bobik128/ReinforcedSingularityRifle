package com.mod.rsrifle.datagen;

import com.mod.rsrifle.datagen.recipe.ModRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class DataGenerators {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(DataGenerators::gatherData);
    }

    private static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(
                event.includeServer(),
                new ModRecipeProvider(packOutput, lookupProvider)
        );

        /*
         * Your mod currently has no blocks, so this is optional.
         * Enable if you want block loot datagen to run.
         */
        // generator.addProvider(
        //         event.includeServer(),
        //         ModLootTableProvider.create(packOutput, lookupProvider)
        // );

        generator.addProvider(
                event.includeClient(),
                new ModBlockStateProvider(packOutput, existingFileHelper)
        );

        generator.addProvider(
                event.includeClient(),
                new ModItemModelProvider(packOutput, existingFileHelper)
        );

        ModBlockTagGenerator blockTagGenerator = generator.addProvider(
                event.includeServer(),
                new ModBlockTagGenerator(packOutput, lookupProvider, existingFileHelper)
        );

        generator.addProvider(
                event.includeServer(),
                new ModItemTagGenerator(
                        packOutput,
                        lookupProvider,
                        blockTagGenerator.contentsGetter(),
                        existingFileHelper
                )
        );

        generator.addProvider(
                event.includeServer(),
                new ModGlobalLootModifiersProvider(packOutput, lookupProvider)
        );

        generator.addProvider(
                event.includeServer(),
                new ModPoiTypeTagsProvider(packOutput, lookupProvider, existingFileHelper)
        );
    }
}