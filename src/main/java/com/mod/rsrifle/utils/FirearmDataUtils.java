package com.mod.rsrifle.utils;

import com.mod.rsrifle.items.SingularityRifle;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

public class FirearmDataUtils {
    private static final String ACTION_TIME = "ActionTime";
    private static final String ACTION = "Action";

    private static final String FIREARM_HEAT = "FirearmHeat";
    private static final String COOLING_DELAY = "CoolingDelay";
    private static final String OVERHEATED = "Overheated";

    private static final String HOLDING_ATTACK_KEY = "HoldingAttackKey";
    private static final String EQUIPPED_LAST_TICK = "equippedLastTick";
    private static final String RUNNING_LAST_TICK = "runningLastTick";
    private static final String CHARGING_RIFLE = "chargingRifle";
    private static final String AIMING = "Aiming";
    private static final String AIMING_TIME = "AimingTime";

    private static final String BATTERY_1_ENERGY = "Battery1Energy";
    private static final String BATTERY_2_ENERGY = "Battery2Energy";

    private static final String EQ_TIME = "EQTime";
    private static final String RUN_TIME = "RunTime";
    private static final String CHARGE_LEVEL = "ChargeLevel";
    private static final String COLOR = "Color";

    private FirearmDataUtils() {
    }

    private static CompoundTag getTagCopy(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void updateTag(ItemStack stack, TagUpdater updater) {
        stack.update(
                DataComponents.CUSTOM_DATA,
                CustomData.EMPTY,
                customData -> customData.update(updater::update)
        );
    }

    @FunctionalInterface
    private interface TagUpdater {
        void update(CompoundTag tag);
    }

    private static int getInt(ItemStack stack, String key) {
        return getTagCopy(stack).getInt(key);
    }

    private static void setInt(ItemStack stack, String key, int value) {
        updateTag(stack, tag -> tag.putInt(key, value));
    }

    private static float getFloat(ItemStack stack, String key) {
        return getTagCopy(stack).getFloat(key);
    }

    private static void setFloat(ItemStack stack, String key, float value) {
        updateTag(stack, tag -> tag.putFloat(key, value));
    }

    private static boolean contains(ItemStack stack, String key) {
        return getTagCopy(stack).contains(key);
    }

    private static void setFlag(ItemStack stack, String key, boolean value) {
        updateTag(stack, tag -> {
            if (value) {
                tag.putBoolean(key, true);
            } else {
                tag.remove(key);
            }
        });
    }

    public static void setActionTime(ItemStack itemStack, int cooldown) {
        setInt(itemStack, ACTION_TIME, cooldown);
    }

    public static int getActionTime(ItemStack itemStack) {
        return getInt(itemStack, ACTION_TIME);
    }

    public static void setAction(ItemStack itemStack, @Nullable SingularityRifle.Action action) {
        updateTag(itemStack, tag -> {
            if (action != null) {
                tag.putString(ACTION, action.getSerializedName());
            } else {
                tag.remove(ACTION);
            }
        });
    }

    public static void cancelReload(ItemStack itemStack) {
        setAction(itemStack, null);
        setActionTime(itemStack, 0);
    }

    @Nullable
    public static SingularityRifle.Action getAction(ItemStack itemStack) {
        return SingularityRifle.Action.byId(getTagCopy(itemStack).getString(ACTION));
    }

    public static void setHeat(ItemStack itemStack, float heat) {
        setFloat(itemStack, FIREARM_HEAT, heat);
    }

    public static void setHeat(CompoundTag tag, float heat) {
        tag.putFloat(FIREARM_HEAT, heat);
    }

    public static float getHeat(ItemStack itemStack) {
        return getFloat(itemStack, FIREARM_HEAT);
    }

    public static float getHeat(CompoundTag tag) {
        return tag.getFloat(FIREARM_HEAT);
    }

    public static void addHeat(ItemStack itemStack, float addedHeat) {
        setHeat(itemStack, getHeat(itemStack) + addedHeat);
    }

    public static void addHeat(CompoundTag tag, float addedHeat) {
        setHeat(tag, getHeat(tag) + addedHeat);
    }

    public static void setCoolingDelay(ItemStack itemStack, int delay) {
        setInt(itemStack, COOLING_DELAY, delay);
    }

    public static void setCoolingDelay(CompoundTag tag, int delay) {
        tag.putInt(COOLING_DELAY, delay);
    }

    public static int getCoolingDelay(ItemStack itemStack) {
        return getInt(itemStack, COOLING_DELAY);
    }

    public static int getCoolingDelay(CompoundTag tag) {
        return tag.getInt(COOLING_DELAY);
    }

    public static void setOverheated(ItemStack itemStack, boolean overheated) {
        setFlag(itemStack, OVERHEATED, overheated);
    }

    public static void setOverheated(CompoundTag tag, boolean overheated) {
        if (overheated) {
            tag.putBoolean(OVERHEATED, true);
        } else {
            tag.remove(OVERHEATED);
        }
    }

    public static boolean isOverheated(ItemStack itemStack) {
        return contains(itemStack, OVERHEATED);
    }

    public static boolean isOverheated(CompoundTag tag) {
        return tag.contains(OVERHEATED);
    }

    public static void setHoldingAttackKey(ItemStack itemStack, boolean holdingAttackKey) {
        setFlag(itemStack, HOLDING_ATTACK_KEY, holdingAttackKey);
    }

    public static boolean isHoldingAttackKey(ItemStack itemStack) {
        return contains(itemStack, HOLDING_ATTACK_KEY);
    }

    public static void setEquipped(ItemStack itemStack, boolean equipped) {
        setFlag(itemStack, EQUIPPED_LAST_TICK, equipped);
    }

    public static boolean isEquipped(ItemStack itemStack) {
        return contains(itemStack, EQUIPPED_LAST_TICK);
    }

    public static void setRunning(ItemStack itemStack, boolean running) {
        setFlag(itemStack, RUNNING_LAST_TICK, running);
    }

    public static boolean isRunning(ItemStack itemStack) {
        return contains(itemStack, RUNNING_LAST_TICK);
    }

    public static void setCharging(ItemStack itemStack, boolean charging) {
        setFlag(itemStack, CHARGING_RIFLE, charging);
    }

    public static boolean isCharging(ItemStack itemStack) {
        return contains(itemStack, CHARGING_RIFLE);
    }

    public static void setAiming(ItemStack itemStack, boolean aiming) {
        setFlag(itemStack, AIMING, aiming);
    }

    public static boolean isAiming(ItemStack itemStack) {
        return contains(itemStack, AIMING);
    }

    public static void setAimingTime(ItemStack itemStack, int time) {
        setInt(itemStack, AIMING_TIME, time);
    }

    public static int getAimingTime(ItemStack itemStack) {
        return getInt(itemStack, AIMING_TIME);
    }

    public static void setBattery1Energy(ItemStack item, int energy) {
        setInt(item, BATTERY_1_ENERGY, energy);
    }

    public static int getBattery1Energy(ItemStack item) {
        return getInt(item, BATTERY_1_ENERGY);
    }

    public static void setBattery2Energy(ItemStack item, int energy) {
        setInt(item, BATTERY_2_ENERGY, energy);
    }

    public static int getBattery2Energy(ItemStack item) {
        return getInt(item, BATTERY_2_ENERGY);
    }

    public static void setEQTime(ItemStack itemStack, int time) {
        setInt(itemStack, EQ_TIME, time);
    }

    public static int getEQTime(ItemStack itemStack) {
        return getInt(itemStack, EQ_TIME);
    }

    public static void setRunTime(ItemStack itemStack, int time) {
        setInt(itemStack, RUN_TIME, time);
    }

    public static int getRunTime(ItemStack itemStack) {
        return getInt(itemStack, RUN_TIME);
    }

    public static void setChargeLevel(ItemStack itemStack, int level) {
        setInt(itemStack, CHARGE_LEVEL, level);
    }

    public static int getChargeLevel(ItemStack itemStack) {
        return getInt(itemStack, CHARGE_LEVEL);
    }

    public static void setColor(ItemStack itemStack, int color) {
        setInt(itemStack, COLOR, color);
    }

    public static int getColor(ItemStack itemStack) {
        CompoundTag tag = getTagCopy(itemStack);

        if (!tag.contains(COLOR)) {
            return SingularityRifle.BASE_COLOR;
        }

        return tag.getInt(COLOR);
    }
}