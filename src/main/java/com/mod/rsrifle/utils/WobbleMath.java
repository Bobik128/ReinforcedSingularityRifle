package com.mod.rsrifle.utils;

import net.minecraft.client.Minecraft;

public class WobbleMath {
    public static float getTilt(double partialTick) {
        // GameTime-based wobble (a bit nicer than raw System.currentTimeMillis, but either works)
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return 0f;

        double t = (mc.level.getGameTime() + partialTick) / 20.0D; // 20 tps -> seconds
        return (float) Math.sin(t) * 20.0F;
    }
}
