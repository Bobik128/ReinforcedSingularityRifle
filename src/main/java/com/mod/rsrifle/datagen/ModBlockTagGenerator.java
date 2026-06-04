package com.mod.rsrifle.datagen;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {

    public ModBlockTagGenerator(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(
                output,
                lookupProvider,
                ReinforcedSingularityRifle.MODID,
                existingFileHelper
        );
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        /*
        Example later:

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(RSRifleBlocks.SOME_BLOCK.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(RSRifleBlocks.SOME_BLOCK.get());
        */
    }
}