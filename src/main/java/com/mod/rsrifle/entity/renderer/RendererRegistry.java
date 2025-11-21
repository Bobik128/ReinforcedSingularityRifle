package com.mod.rsrifle.entity.renderer;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import com.mod.rsrifle.entity.RSRifleEntityTypes;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ReinforcedSingularityRifle.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RendererRegistry {
        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(RSRifleEntityTypes.BLACK_HOLE_PROJECTILE2.get(), BlackHoleProjectileRenderer::new);
            event.registerEntityRenderer(RSRifleEntityTypes.RIFLE_ITEM.get(), ItemEntityRenderer::new);
        }
}
