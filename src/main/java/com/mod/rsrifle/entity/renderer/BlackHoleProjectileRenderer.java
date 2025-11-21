package com.mod.rsrifle.entity.renderer;

import com.mod.rbh.entity.renderer.BlackHoleRenderer;
import com.mod.rbh.shaders.PostEffectRegistry;
import com.mod.rsrifle.client.StarburstRenderer;
import com.mod.rsrifle.entity.BlackHoleProjectile2;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class BlackHoleProjectileRenderer<T extends BlackHoleProjectile2> extends EntityRenderer<T> {
    private static Logger LOGGER = LogUtils.getLogger();

    public BlackHoleProjectileRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(@NotNull T pEntity) {
        return null;
    }

    @Override
    public void render(@NotNull T entity, float pEntityYaw, float pPartialTick, @NotNull PoseStack poseStack, MultiBufferSource buffer, int pPackedLight) {
        if (entity.getEffectInstance() == null) return;
        int et = entity.getExplodingTime();
        if (et > -1) {
            poseStack.pushPose();

            poseStack.translate(0, entity.getSize()/2, 0);

            StarburstRenderer.renderStarburst(poseStack, buffer, ((float) (et + pPartialTick) / entity.maxExplodingTime), entity.getColor(), 432L, 30, 80, 0);

            poseStack.popPose();
        }
        BlackHoleRenderer.renderBlackHoleElliptical(poseStack, entity.getEffectInstance(), PostEffectRegistry.RenderPhase.AFTER_LEVEL, pPackedLight, entity.getEffectSize(), entity.getSize(), entity.shouldBeRainbow(), entity.getColor(), entity.getEffectExponent(), entity.getStretchDir(), entity.getStretchStrength());
    }
}
