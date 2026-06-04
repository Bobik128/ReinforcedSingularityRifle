package com.mod.rsrifle.items;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import com.mod.rsrifle.compat.CreateCompat;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RSRifleItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(ReinforcedSingularityRifle.MODID);

    public static final DeferredItem<SingularityBattery> SINGULARITY_BATTERY =
            ITEMS.registerItem(
                    "singularity_battery",
                    SingularityBattery::new,
                    new Item.Properties()
            );

    public static final DeferredItem<Item> SINGULARITY_BATTERY_EMPTY =
            ITEMS.registerSimpleItem(
                    "singularity_battery_empty",
                    new Item.Properties().stacksTo(16)
            );

    /*
     * This remains nullable because it is only registered when Create is installed.
     * Keep checking CreateCompat.isCreateLoaded() before using it anywhere.
     */
    public static DeferredItem<Item> SINGULARITY_BATTERY_INCOMPLETE;

    public static final DeferredItem<SingularityRifle> SINGULARITY_RIFLE =
            ITEMS.registerItem(
                    "singularity_rifle",
                    SingularityRifle::new,
                    new Item.Properties()
            );

    public static void register(IEventBus eventBus) {
        if (CreateCompat.isCreateLoaded()) {
            registerCreateItems();
        }

        ITEMS.register(eventBus);
    }

    private static void registerCreateItems() {
        try {
            Class<?> sequencedAssemblyItemClass = Class.forName(
                    "com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem"
            );

            SINGULARITY_BATTERY_INCOMPLETE = ITEMS.registerItem(
                    "singularity_battery_incomplete",
                    properties -> {
                        try {
                            return (Item) sequencedAssemblyItemClass
                                    .getConstructor(Item.Properties.class)
                                    .newInstance(properties);
                        } catch (Exception exception) {
                            ReinforcedSingularityRifle.LOGGER.warn(
                                    "Failed to create Create SequencedAssemblyItem, using fallback item instead.",
                                    exception
                            );

                            return new Item(properties);
                        }
                    },
                    new Item.Properties().stacksTo(1)
            );
        } catch (Exception exception) {
            ReinforcedSingularityRifle.LOGGER.warn(
                    "Create detected, but SequencedAssemblyItem could not be resolved. The incomplete battery item will not be registered.",
                    exception
            );
        }
    }
}