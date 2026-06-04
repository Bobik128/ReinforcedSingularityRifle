package com.mod.rsrifle.recipe;

import com.mod.rsrifle.items.RSRifleItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DyeRifleRecipe extends CustomRecipe {

    public DyeRifleRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        boolean sawBase = false;
        boolean sawDye = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);

            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(RSRifleItems.SINGULARITY_RIFLE.get())) {
                if (sawBase) {
                    return false;
                }

                sawBase = true;
            } else if (stack.is(Tags.Items.DYES)) {
                if (sawDye) {
                    return false;
                }

                sawDye = true;
            } else {
                return false;
            }
        }

        return sawBase && sawDye;
    }

    @Override
    public @NotNull ItemStack assemble(
            @NotNull CraftingInput input,
            HolderLookup.@NotNull Provider registries
    ) {
        ItemStack base = ItemStack.EMPTY;
        ItemStack dye = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);

            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(RSRifleItems.SINGULARITY_RIFLE.get())) {
                base = stack.copy();
                base.setCount(1);
            } else if (stack.is(Tags.Items.DYES)) {
                dye = stack;
            }
        }

        if (base.isEmpty() || dye.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int rgb = rgbFromDye(dye);

        base.update(
                DataComponents.CUSTOM_DATA,
                CustomData.EMPTY,
                customData -> customData.update(tag -> tag.putInt("Color", rgb))
        );

        return base;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RSRifleRecipes.DYE_RIFLE.get();
    }

    private static int rgbFromDye(ItemStack dyeStack) {
        DyeColor dyeColor = DyeColor.getColor(dyeStack);

        if (dyeColor == null) {
            return 0xFFFFFF;
        }

        return VANILLA_RGB.getOrDefault(dyeColor, dyeColor.getTextColor());
    }

    private static final Map<DyeColor, Integer> VANILLA_RGB = Map.ofEntries(
            Map.entry(DyeColor.WHITE, 0xF9FFFE),
            Map.entry(DyeColor.ORANGE, 0xF9801D),
            Map.entry(DyeColor.MAGENTA, 0xC74EBD),
            Map.entry(DyeColor.LIGHT_BLUE, 0x3AB3DA),
            Map.entry(DyeColor.YELLOW, 0xFED83D),
            Map.entry(DyeColor.LIME, 0x80C71F),
            Map.entry(DyeColor.PINK, 0xF38BAA),
            Map.entry(DyeColor.GRAY, 0x474F52),
            Map.entry(DyeColor.LIGHT_GRAY, 0x9D9D97),
            Map.entry(DyeColor.CYAN, 0x169C9C),
            Map.entry(DyeColor.PURPLE, 0x8932B8),
            Map.entry(DyeColor.BLUE, 0x3C44AA),
            Map.entry(DyeColor.BROWN, 0x835432),
            Map.entry(DyeColor.GREEN, 0x5E7C16),
            Map.entry(DyeColor.RED, 0xB02E26),
            Map.entry(DyeColor.BLACK, 0x1D1D21)
    );
}