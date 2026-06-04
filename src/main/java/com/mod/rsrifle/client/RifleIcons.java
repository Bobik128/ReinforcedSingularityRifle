package com.mod.rsrifle.client;

import com.mod.rbh.shaders.RBHRenderTypes;
import com.mod.rsrifle.ReinforcedSingularityRifle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.awt.*;

public class RifleIcons {
    private static final int ATLAS_SIZE = 48;

    private static final ResourceLocation ICONS = ResourceLocation.fromNamespaceAndPath(
            ReinforcedSingularityRifle.MODID,
            "textures/gui/rifle_icons.png"
    );

    public static void drawColoredIcon(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int overlay,
            Icons icon
    ) {
        int color = switch (icon) {
            case FULL, THREE_QUARTERS, HALF -> Color.WHITE.getRGB();
            case QUARTER -> 0xFFFF8C00;
            case EMPTY, WARNING -> Color.RED.getRGB();
        };

        drawIcon(poseStack, bufferSource, packedLight, overlay, icon, color);
    }

    public static void drawIcon(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int overlay,
            Icons icon,
            int color
    ) {
        int x1;
        int x2;

        switch (icon) {
            case FULL, QUARTER -> {
                x1 = 0;
                x2 = 16;
            }
            case THREE_QUARTERS, EMPTY -> {
                x1 = 16;
                x2 = 32;
            }
            case HALF, WARNING -> {
                x1 = 32;
                x2 = 48;
            }
            default -> throw new IllegalStateException("Unexpected icon: " + icon);
        }

        int y1;
        int y2;

        switch (icon) {
            case FULL, THREE_QUARTERS, HALF -> {
                y1 = 0;
                y2 = 16;
            }
            case EMPTY, QUARTER, WARNING -> {
                y1 = 16;
                y2 = 32;
            }
            default -> throw new IllegalStateException("Unexpected icon: " + icon);
        }

        float u0 = (float) x1 / ATLAS_SIZE;
        float u1 = (float) x2 / ATLAS_SIZE;
        float v0 = (float) y1 / ATLAS_SIZE;
        float v1 = (float) y2 / ATLAS_SIZE;

        float xa = -8.0f;
        float ya = -8.0f;
        float xb = 8.0f;
        float yb = 8.0f;

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RBHRenderTypes.text(ICONS));

        int a = (color >>> 24) & 0xFF;
        int r = (color >>> 16) & 0xFF;
        int g = (color >>> 8) & 0xFF;
        int b = color & 0xFF;

        if (a == 0) {
            a = 255;
        }

        vertexConsumer
                .addVertex(matrix, xa, yb, 0.0f)
                .setColor(r, g, b, a)
                .setUv(u0, v1)
                .setOverlay(overlay)
                .setLight(packedLight)
                .setNormal(0.0f, 0.0f, 1.0f);

        vertexConsumer
                .addVertex(matrix, xb, yb, 0.0f)
                .setColor(r, g, b, a)
                .setUv(u1, v1)
                .setOverlay(overlay)
                .setLight(packedLight)
                .setNormal(0.0f, 0.0f, 1.0f);

        vertexConsumer
                .addVertex(matrix, xb, ya, 0.0f)
                .setColor(r, g, b, a)
                .setUv(u1, v0)
                .setOverlay(overlay)
                .setLight(packedLight)
                .setNormal(0.0f, 0.0f, 1.0f);

        vertexConsumer
                .addVertex(matrix, xa, ya, 0.0f)
                .setColor(r, g, b, a)
                .setUv(u0, v0)
                .setOverlay(overlay)
                .setLight(packedLight)
                .setNormal(0.0f, 0.0f, 1.0f);
    }

    public enum Icons {
        FULL,
        THREE_QUARTERS,
        HALF,
        QUARTER,
        EMPTY,
        WARNING
    }
}