package com.mod.rsrifle.datagen.recipe;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import com.mod.rsrifle.items.RSRifleItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        /*
         * Non-Create fallback recipe.
         *
         * Old Forge:
         * ConditionalRecipe.builder()
         *
         * NeoForge 1.21.1:
         * output.withConditions(...)
         */
        ShapelessRecipeBuilder.shapeless(
                        RecipeCategory.MISC,
                        RSRifleItems.SINGULARITY_BATTERY.get()
                )
                .requires(RSRifleItems.SINGULARITY_BATTERY_EMPTY.get())
                .requires(Items.NETHER_STAR)
                .requires(Items.EXPERIENCE_BOTTLE)
                .unlockedBy(
                        getHasName(RSRifleItems.SINGULARITY_BATTERY_EMPTY.get()),
                        has(RSRifleItems.SINGULARITY_BATTERY_EMPTY.get())
                )
                .save(
                        output.withConditions(not(modLoaded("create"))),
                        ResourceLocation.fromNamespaceAndPath(
                                ReinforcedSingularityRifle.MODID,
                                "singularity_battery"
                        )
                );

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.COMBAT,
                        RSRifleItems.SINGULARITY_RIFLE.get()
                )
                .pattern("NA ")
                .pattern("SES")
                .pattern("Bss")
                .define('S', Items.NETHER_STAR)
                .define('E', Items.DRAGON_EGG)
                .define('N', Items.NETHERITE_INGOT)
                .define('A', Items.AMETHYST_SHARD)
                .define('B', Items.NETHERITE_BLOCK)
                .define('s', RSRifleItems.SINGULARITY_BATTERY_EMPTY.get())
                .unlockedBy(
                        getHasName(Items.DRAGON_EGG),
                        has(Items.DRAGON_EGG)
                )
                .save(output);

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.MISC,
                        RSRifleItems.SINGULARITY_BATTERY_EMPTY.get(),
                        2
                )
                .pattern("IRI")
                .pattern("N N")
                .pattern("ISI")
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE_BLOCK)
                .define('N', Items.NETHERITE_INGOT)
                .define('S', Items.SMOOTH_STONE_SLAB)
                .unlockedBy(
                        getHasName(RSRifleItems.SINGULARITY_RIFLE.get()),
                        has(RSRifleItems.SINGULARITY_RIFLE.get())
                )
                .save(output);
    }

    protected static void oreSmelting(
            RecipeOutput output,
            List<ItemLike> ingredients,
            RecipeCategory category,
            ItemLike result,
            float experience,
            int cookingTime,
            String group
    ) {
        oreCooking(
                output,
                RecipeSerializer.SMELTING_RECIPE,
                SmeltingRecipe::new,
                ingredients,
                category,
                result,
                experience,
                cookingTime,
                group,
                "_from_smelting"
        );
    }

    protected static void oreBlasting(
            RecipeOutput output,
            List<ItemLike> ingredients,
            RecipeCategory category,
            ItemLike result,
            float experience,
            int cookingTime,
            String group
    ) {
        oreCooking(
                output,
                RecipeSerializer.BLASTING_RECIPE,
                BlastingRecipe::new,
                ingredients,
                category,
                result,
                experience,
                cookingTime,
                group,
                "_from_blasting"
        );
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(
            RecipeOutput output,
            RecipeSerializer<T> cookingSerializer,
            AbstractCookingRecipe.Factory<T> factory,
            List<ItemLike> ingredients,
            RecipeCategory category,
            ItemLike result,
            float experience,
            int cookingTime,
            String group,
            String recipeName
    ) {
        for (ItemLike ingredient : ingredients) {
            SimpleCookingRecipeBuilder.generic(
                            Ingredient.of(ingredient),
                            category,
                            result,
                            experience,
                            cookingTime,
                            cookingSerializer,
                            factory
                    )
                    .group(group)
                    .unlockedBy(getHasName(ingredient), has(ingredient))
                    .save(
                            output,
                            ResourceLocation.fromNamespaceAndPath(
                                    ReinforcedSingularityRifle.MODID,
                                    getItemName(result) + recipeName + "_" + getItemName(ingredient)
                            )
                    );
        }
    }
}