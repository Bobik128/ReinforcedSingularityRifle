package com.mod.rsrifle.recipe;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RSRifleRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(
                    BuiltInRegistries.RECIPE_SERIALIZER,
                    ReinforcedSingularityRifle.MODID
            );

    public static final DeferredHolder<
            RecipeSerializer<?>,
            SimpleCraftingRecipeSerializer<DyeRifleRecipe>
            > DYE_RIFLE =
            RECIPE_SERIALIZERS.register(
                    "dye_rifle",
                    () -> new SimpleCraftingRecipeSerializer<>(DyeRifleRecipe::new)
            );

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }
}