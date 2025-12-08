package com.mod.rsrifle;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = ReinforcedSingularityRifle.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonConfig {

    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.BooleanValue DESTROY_BLOCKS;

    static {
        final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        DESTROY_BLOCKS = BUILDER
                .comment("Black holes can destroy blocks")
                .define("destroy_blocks", true);

        SPEC = BUILDER.build();
    }

    public static boolean destroyBlocks;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (config.getSpec() == CommonConfig.SPEC) {
            destroyBlocks = DESTROY_BLOCKS.get();
        }
    }
}
