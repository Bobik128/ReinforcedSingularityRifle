package com.mod.rsrifle.items;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RSRifleCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ReinforcedSingularityRifle.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_MODE_TAB =
            CREATIVE_MODE_TABS.register("rsrifle_tab", () ->
                    CreativeModeTab.builder()
                            .icon(() -> new ItemStack(RSRifleItems.SINGULARITY_BATTERY.get()))
                            .title(Component.translatable("rsrifle.creativetab.tab"))
                            .displayItems((parameters, output) -> {
                                output.accept(RSRifleItems.SINGULARITY_RIFLE.get());
                                output.accept(RSRifleItems.SINGULARITY_BATTERY.get());
                                output.accept(RSRifleItems.SINGULARITY_BATTERY_EMPTY.get());

                                if (RSRifleItems.SINGULARITY_BATTERY_INCOMPLETE != null) {
                                    output.accept(RSRifleItems.SINGULARITY_BATTERY_INCOMPLETE.get());
                                }
                            })
                            .build()
            );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}