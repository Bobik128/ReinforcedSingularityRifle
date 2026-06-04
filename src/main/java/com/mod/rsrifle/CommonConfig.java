package com.mod.rsrifle;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

public class CommonConfig {

    public static final ModConfigSpec SPEC;

    private static final ModConfigSpec.BooleanValue DESTROY_BLOCKS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        DESTROY_BLOCKS = builder
                .comment("Black holes can destroy blocks")
                .define("destroy_blocks", true);

        SPEC = builder.build();
    }

    public static boolean destroyBlocks;

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(CommonConfig::onConfigLoad);
        modEventBus.addListener(CommonConfig::onConfigReload);
    }

    private static void onConfigLoad(final ModConfigEvent.Loading event) {
        bakeConfig(event.getConfig());
    }

    private static void onConfigReload(final ModConfigEvent.Reloading event) {
        bakeConfig(event.getConfig());
    }

    private static void bakeConfig(ModConfig config) {
        if (config.getSpec() == SPEC) {
            destroyBlocks = DESTROY_BLOCKS.get();
        }
    }
}