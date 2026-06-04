package com.mod.rsrifle.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.Random;

public class StarburstRenderer {

    public static void renderStarburst(
            PoseStack poseStack,
            MultiBufferSource buffers,
            float progress,
            int argbColor,
            long randomSeed,
            int minRays,
            int rays,
            float kSpin
    ) {
        float p = Math.min(1.0f, Math.max(0.0f, progress));
        float scale = p * p;

        Random random = new Random(randomSeed);
        int rayCount = (int) (minRays + rays * scale);

        boolean hasAlpha = (argbColor & 0xFF000000) != 0;
        int argb = hasAlpha ? argbColor : (argbColor | 0xFF000000);

        int baseA = (argb >>> 24) & 0xFF;
        int r = (argb >>> 16) & 0xFF;
        int g = (argb >>> 8) & 0xFF;
        int b = argb & 0xFF;

        float fade = (float) Math.sin(p * Math.PI);
        int a = Math.max(0, Math.min(255, Math.round(baseA * fade)));

        poseStack.pushPose();

        float spin = p * kSpin;
        poseStack.mulPose(Axis.YP.rotationDegrees(spin));

        VertexConsumer vertexConsumer = buffers.getBuffer(RenderType.lightning());
        PoseStack.Pose pose = poseStack.last();

        for (int i = 0; i < rayCount; i++) {
            float dx = (random.nextFloat() - 0.5f) * 2.0f;
            float dy = (random.nextFloat() - 0.5f) * 2.0f;
            float dz = (random.nextFloat() - 0.5f) * 2.0f;

            float baseLen = 2.0f + random.nextFloat() * 4.0f;
            float len = baseLen + scale * 10.0f;

            vertexConsumer
                    .addVertex(pose, 0.0f, 0.0f, 0.0f)
                    .setColor(r, g, b, a);

            vertexConsumer
                    .addVertex(pose, dx * len, dy * len, dz * len)
                    .setColor(r, g, b, 0);

            vertexConsumer
                    .addVertex(pose, dx * len * 0.7f, dy * len * 0.7f, dz * len * 0.7f)
                    .setColor(r, g, b, 0);
        }

        poseStack.popPose();
    }
}