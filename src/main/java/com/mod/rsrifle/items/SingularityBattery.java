package com.mod.rsrifle.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SingularityBattery extends Item {
    public static final String ENERGY_TAG = "Energy";
    public static final int MAX_ENERGY = 600;

    public SingularityBattery(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static int getEnergy(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);

        if (!customData.contains(ENERGY_TAG)) {
            return MAX_ENERGY;
        }

        return Math.clamp(
                customData.copyTag().getInt(ENERGY_TAG)
                ,
                0,
                MAX_ENERGY);
    }

    public static void setEnergy(ItemStack stack, int value) {
        int clamped = Math.max(0, Math.min(MAX_ENERGY, value));

        stack.update(
                DataComponents.CUSTOM_DATA,
                CustomData.EMPTY,
                customData -> customData.update(tag -> tag.putInt(ENERGY_TAG, clamped))
        );
    }

    public static int addEnergy(ItemStack stack, int delta) {
        int newValue = Math.clamp(getEnergy(stack) + delta,
                0, MAX_ENERGY);

        setEnergy(stack, newValue);

        return newValue;
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        int chargeLevel = getEnergy(stack);

        tooltip.add(
                Component.translatable("battery.charge_level")
                        .append(": ")
                        .withStyle(ChatFormatting.BLUE)
                        .append(
                                Component.literal(chargeLevel + "/" + MAX_ENERGY)
                                        .withStyle(ChatFormatting.WHITE)
                        )
        );
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        int energy = getEnergy(stack);
        return energy > 0 && energy < MAX_ENERGY;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0f * getEnergy(stack) / (float) MAX_ENERGY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xFFFFFF;
    }

    public static void ensureDepletedSwap(ItemStack stack, @Nullable LivingEntity holder) {
        if (getEnergy(stack) > 0) {
            return;
        }

        ItemStack replacement = makeDepletedReplacement(stack);

        if (holder instanceof Player player) {
            var inventory = player.getInventory();

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if (inventory.getItem(i) == stack) {
                    inventory.setItem(i, replacement);
                    return;
                }
            }
        }
    }

    private static ItemStack makeDepletedReplacement(ItemStack oldStack) {
        ItemStack replacement = new ItemStack(
                RSRifleItems.SINGULARITY_BATTERY_EMPTY.get()
        );

        if (oldStack.has(DataComponents.CUSTOM_NAME)) {
            replacement.set(
                    DataComponents.CUSTOM_NAME,
                    oldStack.get(DataComponents.CUSTOM_NAME)
            );
        }

        return replacement;
    }

    @Override
    public void inventoryTick(
            ItemStack stack,
            Level level,
            Entity entity,
            int slot,
            boolean selected
    ) {
        super.inventoryTick(stack, level, entity, slot, selected);

        if (level.isClientSide) {
            return;
        }

        if (getEnergy(stack) > 0) {
            return;
        }

        if (entity instanceof Player player) {
            player.getInventory().setItem(slot, makeDepletedReplacement(stack));
        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity itemEntity) {
        if (!itemEntity.level().isClientSide && getEnergy(stack) <= 0) {
            itemEntity.setItem(makeDepletedReplacement(stack));
        }

        return false;
    }
}