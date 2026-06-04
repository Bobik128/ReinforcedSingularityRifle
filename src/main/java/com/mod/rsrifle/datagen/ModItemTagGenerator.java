package com.mod.rsrifle.datagen;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagBuilder;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {

    public ModItemTagGenerator(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagLookup<Block>> blockTags,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(
                output,
                lookupProvider,
                blockTags,
                ReinforcedSingularityRifle.MODID,
                existingFileHelper
        );
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        /*
        Example later:

        this.tag(ItemTags.TRIMMABLE_ARMOR)
                .add(
                        RSRifleItems.SOME_HELMET.get(),
                        RSRifleItems.SOME_CHESTPLATE.get()
                );

        this.tag(ItemTags.MUSIC_DISCS)
                .add(RSRifleItems.SOME_DISC.get());
        */
    }
}