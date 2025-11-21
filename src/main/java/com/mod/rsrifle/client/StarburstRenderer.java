package com.mod.rsrifle.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.Random;

public class StarburstRenderer {
    public static void renderStarburst(PoseStack poseStack,
                                       MultiBufferSource buffers,
                                       float progress,
                                       int argbColor,
                                       long randomSeed,
                                       int minRays,
                                       int rays,
                                       float kSpin) {

        float p = Math.min(1.0F, Math.max(0.0F, progress));
        float scale = p * p;

        Random random = new Random(randomSeed);
        int rayCount = (int) (minRays + rays * scale);

        // Accept both ARGB and RGB
        boolean hasAlpha = (argbColor & 0xFF000000) != 0;
        int argb = hasAlpha ? argbColor : (argbColor | 0xFF000000);

        // Extract color
        float baseA = ((argb >> 24) & 0xFF) / 255.0F;
        float r = ((argb >> 16) & 0xFF) / 255.0F;
        float g = ((argb >> 8)  & 0xFF) / 255.0F;
        float b = (argb         & 0xFF) / 255.0F;

        // *** Fade in + fade out ***
        float fade = (float)Math.sin(p * Math.PI);  // 0→1→0
        float a = baseA * fade;

        VertexConsumer vc = buffers.getBuffer(RenderType.lightning());
        PoseStack.Pose pose = poseStack.last();

        float spin = p * kSpin;
        poseStack.mulPose(Axis.YP.rotationDegrees(spin));

        for (int i = 0; i < rayCount; i++) {
            float dx = (random.nextFloat() - 0.5F) * 2.0F;
            float dy = (random.nextFloat() - 0.5F) * 2.0F;
            float dz = (random.nextFloat() - 0.5F) * 2.0F;

            float baseLen = 2.0F + random.nextFloat() * 4.0F;
            float len = baseLen + scale * 10.0F;

            // center bright → transparent tips
            vc.vertex(pose.pose(), 0.0F, 0.0F, 0.0F)
                    .color(r, g, b, a)
                    .endVertex();

            vc.vertex(pose.pose(), dx * len, dy * len, dz * len)
                    .color(r, g, b, 0.0F)
                    .endVertex();

            vc.vertex(pose.pose(), dx * len * 0.7F, dy * len * 0.7F, dz * len * 0.7F)
                    .color(r, g, b, 0.0F)
                    .endVertex();
        }
    }
}