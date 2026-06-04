package com.mod.rsrifle.entity.renderer;

import com.mod.rsrifle.entity.RSRifleEntityTypes;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class RendererRegistry {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(RendererRegistry::registerRenderers);
    }

    private static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                RSRifleEntityTypes.BLACK_HOLE_PROJECTILE2.get(),
                BlackHoleProjectileRenderer::new
        );

        event.registerEntityRenderer(
                RSRifleEntityTypes.RIFLE_ITEM.get(),
                ItemEntityRenderer::new
        );
    }
}