package com.mod.rsrifle.recipe;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RSRifleRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ReinforcedSingularityRifle.MODID);

    public static final RegistryObject<SimpleCraftingRecipeSerializer<DyeRifleRecipe>> DYE_RIFLE =
            RECIPE_SERIALIZERS.register("dye_rifle",
                    () -> new SimpleCraftingRecipeSerializer<>(DyeRifleRecipe::new));

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }
}
