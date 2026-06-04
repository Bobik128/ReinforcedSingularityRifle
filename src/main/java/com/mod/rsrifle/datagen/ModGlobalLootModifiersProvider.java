package com.mod.rsrifle.datagen;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {

    public ModGlobalLootModifiersProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries
    ) {
        super(output, registries, ReinforcedSingularityRifle.MODID);
    }

    @Override
    protected void start() {
        /*
        Example later:

        add("pine_cone_from_grass", new AddItemModifier(
                new LootItemCondition[]{
                        LootItemBlockStatePropertyCondition
                                .hasBlockStateProperties(Blocks.GRASS)
                                .build(),
                        LootItemRandomChanceCondition
                                .randomChance(0.35f)
                                .build()
                },
                RSRifleItems.PINE_CONE.get()
        ));
        */
    }
}