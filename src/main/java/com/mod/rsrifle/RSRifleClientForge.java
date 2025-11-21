package com.mod.rsrifle;

import com.mod.rsrifle.client.RifleShootAnimHelper;
import com.mod.rsrifle.items.renderer.ExtendedRifleItemRenderer;
import com.mod.rsrifle.shaders.RifleHoleEffectInstanceHolder;
import com.mod.rsrifle.sound.ClientSoundHandler;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class RSRifleClientForge {

    public static void init(IEventBus modBus, IEventBus forgeBus) {
        modBus.addListener(RSRifleClientForge::onClientSetup);
        modBus.addListener(RSRifleClientForge::onRegisterKeyMappings);

        forgeBus.addListener(RSRifleClientForge::onClientTick);
        forgeBus.addListener(RSRifleClientForge::onMouseInput);
        forgeBus.addListener(RSRifleClientForge::onKeyInput);
        forgeBus.addListener(RSRifleClientForge::onComputeFov);
        forgeBus.addListener(RSRifleClientForge::onRenderEntity);
        forgeBus.addListener(RSRifleClientForge::onCameraAngles);
        forgeBus.addListener(RSRifleClientForge::onRenderGuiOverlay);
        forgeBus.addListener(RifleHoleEffectInstanceHolder::resetEffectCounter);
    }

    private static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(RSRifleClient::onClientSetup);
    }


    private static void onMouseInput(final InputEvent.MouseButton.Pre inputEvent) {
        RSRifleClient.onMouseInput(inputEvent.getButton(), inputEvent.getButton(), inputEvent.getModifiers());
    }

    private static void onKeyInput(final InputEvent.Key event) {
        RSRifleClient.onKeyInput(event.getKey(), event.getScanCode(), event.getAction(), event.getModifiers());
    }

    private static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
        RSRifleClient.registerKeyMappings(event::register);
    }

    private static void onComputeFov(final ComputeFovModifierEvent event) {
        event.setNewFovModifier(RSRifleClient.modifyFov(event.getNewFovModifier(), event.getPlayer()));
    }

    private static void onRenderGuiOverlay(final RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().overlay() == VanillaGuiOverlay.HOTBAR.type().overlay()) {
        }
        // TODO crosshair
    }

    private static void onRenderEntity(final RenderLivingEvent.Pre<?, ?> event) {
        RSRifleClient.onRenderEntity(event);
    }

    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        RSRifleClient.onCameraAngles(event);
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        RifleHoleEffectInstanceHolder.clientTick();
        ExtendedRifleItemRenderer.tick();
        if (event.phase == TickEvent.Phase.START)
            RifleShootAnimHelper.tick();
        ClientSoundHandler.onClientTick(event);
    }
}
