package com.mod.rsrifle.items;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class RSRifleCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ReinforcedSingularityRifle.MODID);

    public static final RegistryObject<CreativeModeTab> CREATIVE_MODE_TAB = CREATIVE_MODE_TABS.register("rsrifle.tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(RSRifleItems.SINGULARITY_BATTERY.get()))
                    .title(Component.translatable("rsrifle.creativetab.tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(RSRifleItems.SINGULARITY_RIFLE.get());
                        pOutput.accept(RSRifleItems.SINGULARITY_BATTERY.get());
                        pOutput.accept(RSRifleItems.SINGULARITY_BATTERY_EMPTY.get());
                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
