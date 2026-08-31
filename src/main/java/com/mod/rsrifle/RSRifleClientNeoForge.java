package com.mod.rsrifle;

import com.mod.rsrifle.client.RifleShootAnimHelper;
import com.mod.rsrifle.items.renderer.ExtendedRifleItemRenderer;
import com.mod.rsrifle.shaders.RifleHoleEffectInstanceHolder;
import com.mod.rsrifle.sound.ClientSoundHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import com.mod.rsrifle.items.RSRifleItems;
import com.mod.rsrifle.items.renderer.SingularityRifleRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class RSRifleClientNeoForge {

    public static void init(IEventBus modBus, IEventBus neoForgeBus) {

        modBus.addListener(RSRifleClientNeoForge::onClientSetup);
        modBus.addListener(RSRifleClientNeoForge::onRegisterKeyMappings);
        modBus.addListener(RSRifleClientNeoForge::onRegisterClientExtensions);

        neoForgeBus.addListener(RSRifleClientNeoForge::onClientTickPre);
        neoForgeBus.addListener(RSRifleClientNeoForge::onClientTickPost);

        neoForgeBus.addListener(RSRifleClientNeoForge::onMouseInput);
        neoForgeBus.addListener(RSRifleClientNeoForge::onKeyInput);
        neoForgeBus.addListener(RSRifleClientNeoForge::onComputeFov);
        neoForgeBus.addListener(RSRifleClientNeoForge::onRenderEntity);
        neoForgeBus.addListener(RSRifleClientNeoForge::onCameraAngles);
        neoForgeBus.addListener(RSRifleClientNeoForge::onRenderGuiLayer);

        neoForgeBus.addListener(RifleHoleEffectInstanceHolder::resetEffectCounter);
    }

    private static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(RSRifleClient::onClientSetup);
    }

    private static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(
                new IClientItemExtensions() {
                    private SingularityRifleRenderer renderer;

                    @Override
                    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                        if (this.renderer == null) {
                            this.renderer = new SingularityRifleRenderer();
                        }

                        return this.renderer;
                    }
                },
                RSRifleItems.SINGULARITY_RIFLE.get()
        );
    }

    private static void onMouseInput(final InputEvent.MouseButton.Pre event) {
        RSRifleClient.onMouseInput(
                event.getButton(),
                event.getAction(),
                event.getModifiers()
        );
    }

    private static void onKeyInput(final InputEvent.Key event) {
        RSRifleClient.onKeyInput(
                event.getKey(),
                event.getScanCode(),
                event.getAction(),
                event.getModifiers()
        );
    }

    private static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
        RSRifleClient.registerKeyMappings(event);
    }

    private static void onComputeFov(final ComputeFovModifierEvent event) {
        event.setNewFovModifier(
                RSRifleClient.modifyFov(
                        event.getNewFovModifier(),
                        event.getPlayer()
                )
        );
    }

    private static void onRenderGuiLayer(final RenderGuiLayerEvent.Pre event) {
        if (VanillaGuiLayers.HOTBAR.equals(event.getName())) {
            // TODO crosshair / HUD integration
        }
    }

    private static void onRenderEntity(final RenderLivingEvent.Pre<?, ?> event) {
        RSRifleClient.onRenderEntity(event);
    }

    private static void onCameraAngles(final ViewportEvent.ComputeCameraAngles event) {
        RSRifleClient.onCameraAngles(event);
    }

    private static void onClientTickPre(final ClientTickEvent.Pre event) {
        RifleShootAnimHelper.tick();
    }

    private static void onClientTickPost(final ClientTickEvent.Post event) {
        RifleHoleEffectInstanceHolder.clientTick();
        ExtendedRifleItemRenderer.tick();

        ClientSoundHandler.onClientTick(event);
    }
}