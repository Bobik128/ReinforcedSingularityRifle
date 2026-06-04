package com.mod.rsrifle.utils;

import com.mod.rsrifle.items.SingularityRifle;
import com.mod.rsrifle.sound.EntityBoundSound;
import com.mod.rsrifle.sound.RSRifleSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class FirearmModeClient {
    private static final Map<Long, EntityBoundSound> reloadSounds = new HashMap<>();

    public static void clientTick(
            FirearmMode mode,
            ItemStack itemStack,
            LivingEntity entity,
            boolean isSelected,
            long id,
            int actionTime
    ) {
        SingularityRifle.Action action = FirearmDataUtils.getAction(itemStack);

        EntityBoundSound reloadSound = reloadSounds.get(id);

        if (!isSelected && action == SingularityRifle.Action.RELOAD) {
            if (reloadSound != null) {
                reloadSound.remove();
                reloadSounds.remove(id);
            }
        }

        reloadSound = reloadSounds.get(id);

        if (reloadSound != null && (reloadSound.isStopped() || action != SingularityRifle.Action.RELOAD)) {
            reloadSound.remove();
            reloadSounds.remove(id);
        }

        reloadSound = reloadSounds.get(id);

        if (actionTime > 0 && action == SingularityRifle.Action.RELOAD && reloadSound != null) {
            reloadSound.enabled = true;
        }

        if (action == SingularityRifle.Action.RELOAD && !reloadSounds.containsKey(id)) {
            EntityBoundSound soundInstance = new EntityBoundSound(
                    RSRifleSounds.RIFLE_RELOAD.get(),
                    SoundSource.NEUTRAL,
                    entity,
                    1.0f
            );

            reloadSounds.put(id, soundInstance);
            Minecraft.getInstance().getSoundManager().play(soundInstance);
        }

        int runningTime = mode.getRunTime(itemStack, entity);

        if (runningTime > 0) {
            mode.setRTime(itemStack, entity, runningTime - 1);
        }

        boolean wasRunning = FirearmDataUtils.isRunning(itemStack);
        boolean isNowRunning = mode.isRunning(itemStack, entity);

        if (isNowRunning && !wasRunning) {
            mode.startRunning(itemStack, entity);
        }

        if (!isNowRunning && wasRunning) {
            mode.stopRunning(itemStack, entity);
        }
    }
}