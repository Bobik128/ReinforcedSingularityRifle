package com.mod.rsrifle;

import com.mod.rsrifle.entity.RSRifleEntityTypes;
import com.mod.rsrifle.items.RSRifleCreativeModeTab;
import com.mod.rsrifle.items.RSRifleItems;
import com.mod.rsrifle.items.SingularityRifle;
import com.mod.rsrifle.network.RSRifleNetwork;
import com.mod.rsrifle.recipe.RSRifleRecipes;
import com.mod.rsrifle.sound.RBHSounds;
import com.mojang.logging.LogUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

@Mod(ReinforcedSingularityRifle.MODID)
public class ReinforcedSingularityRifle
{
    public static final String MODID = "rsrifle";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ReinforcedSingularityRifle(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::commonSetup);

        GeckoLib.initialize();

        RSRifleItems.register(modEventBus);
        RSRifleCreativeModeTab.register(modEventBus);
        RSRifleEntityTypes.register(modEventBus);
        RSRifleRecipes.register(modEventBus);
        RBHSounds.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RSRifleClientForge.init(modEventBus, forgeBus));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            RSRifleNetwork.init();
        });
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            RSRifleClient.onClientSetup();
        }

        @SubscribeEvent
        public void onRenderPlayer(RenderLivingEvent.Pre<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> event) {
            AbstractClientPlayer player = (AbstractClientPlayer) event.getEntity();
            ItemStack main = player.getMainHandItem();

            if (main.getItem() instanceof SingularityRifle) {
                PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();

                model.rightArm.xRot = -90.0F;
                model.leftArm.xRot = -90.0F;
                model.rightArm.yRot = -20.0F;
                model.leftArm.yRot = 20.0F;
            }
        }

    }
}
