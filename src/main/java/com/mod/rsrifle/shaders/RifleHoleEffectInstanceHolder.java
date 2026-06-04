package com.mod.rsrifle.shaders;

import com.mod.rbh.shaders.PostEffectRegistry;
import com.mod.rsrifle.ReinforcedSingularityRifle;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RifleHoleEffectInstanceHolder {
    private static final Map<Integer, PostEffectRegistry.HoleEffectInstance> effects = new HashMap<>();
    private static final Map<Integer, Integer> timers = new HashMap<>();

    private static final List<Integer> toRemove = new ArrayList<>();

    private static int effectCounter = 0;

    public static void clientTick() {
        for (Map.Entry<Integer, PostEffectRegistry.HoleEffectInstance> entry : effects.entrySet()) {
            Integer timer = timers.get(entry.getKey());

            if (timer == null || timer <= 0) {
                toRemove.add(entry.getKey());
                continue;
            }

            timers.put(entry.getKey(), timer - 1);
        }

        for (Integer key : toRemove) {
            effects.remove(key);
            timers.remove(key);
        }

        toRemove.clear();
    }

    public static void resetEffectCounter(RenderFrameEvent.Post event) {
        effectCounter = 0;
    }

    public static @Nullable PostEffectRegistry.HoleEffectInstance getUniqueEffect() {
        if (effects.size() < 40) {
            effectCounter++;

            timers.put(effectCounter, 30);

            return effects.computeIfAbsent(
                    effectCounter,
                    itemId -> PostEffectRegistry.HoleEffectInstance.createEffectInstance()
            );
        }

        ReinforcedSingularityRifle.LOGGER.warn("Too many rifle effects registered, skipping!");
        return null;
    }
}