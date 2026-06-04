package com.mod.rsrifle;

import com.mod.rsrifle.datagen.DataGenerators;
import com.mod.rsrifle.entity.RSRifleEntityTypes;
import com.mod.rsrifle.entity.renderer.RendererRegistry;
import com.mod.rsrifle.items.RSRifleCreativeModeTab;
import com.mod.rsrifle.items.RSRifleItems;
import com.mod.rsrifle.items.SingularityRifle;
import com.mod.rsrifle.network.RSRifleNetwork;
import com.mod.rsrifle.recipe.RSRifleRecipes;
import com.mod.rsrifle.sound.RSRifleSounds;
import com.mojang.logging.LogUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

@Mod(ReinforcedSingularityRifle.MODID)
public class ReinforcedSingularityRifle {
    public static final String MODID = "rsrifle";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ReinforcedSingularityRifle(IEventBus modEventBus, ModContainer modContainer) {
        IEventBus neoForgeBus = NeoForge.EVENT_BUS;

        modEventBus.addListener(this::commonSetup);

        /*
         * GeckoLib 4 still exposes this in common 1.21.1 setups.
         * If your selected GeckoLib artifact marks initialize() as missing,
         * remove this line; GeckoLib then initializes through its own mod bootstrap.
         */
//        GeckoLib.initialize();
        RendererRegistry.register(modEventBus);
        RegisterDamageTypes.register(modEventBus);
        DataGenerators.register(modEventBus);
        RSRifleItems.register(modEventBus);
        RSRifleCreativeModeTab.register(modEventBus);
        RSRifleEntityTypes.register(modEventBus);
        RSRifleRecipes.register(modEventBus);
        RSRifleSounds.register(modEventBus);
        CommonConfig.register(modEventBus);

        RSRifleNetwork.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);

        if (FMLLoader.getDist() == Dist.CLIENT) {
            modEventBus.addListener(ReinforcedSingularityRifle::onClientSetup);

            RSRifleClientNeoForge.init(modEventBus, neoForgeBus);

            neoForgeBus.addListener(ReinforcedSingularityRifle::onRenderPlayer);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private static void onClientSetup(final FMLClientSetupEvent event) {
        RSRifleClient.onClientSetup();
    }

    private static void onRenderPlayer(final RenderPlayerEvent.Pre event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) {
            return;
        }

        ItemStack mainHandItem = player.getMainHandItem();

        if (!(mainHandItem.getItem() instanceof SingularityRifle)) {
            return;
        }

        PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();

        /*
         * Your old code used -90.0F / ±20.0F directly.
         * Minecraft model rotations are in radians, so I converted them to radians here.
         * If your old visual pose depended on the huge raw values, change these back,
         * but this is the mathematically correct form.
         */
        model.rightArm.xRot = -Mth.HALF_PI;
        model.leftArm.xRot = -Mth.HALF_PI;

        model.rightArm.yRot = (float) Math.toRadians(-20.0);
        model.leftArm.yRot = (float) Math.toRadians(20.0);
        //TODO check if it's right
    }
}