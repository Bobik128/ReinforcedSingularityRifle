package com.mod.rsrifle.sound;

import com.mod.rsrifle.items.RSRifleItems;
import com.mod.rsrifle.items.SingularityRifle;
import com.mod.rsrifle.utils.FirearmMode;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class ClientSoundHandler {
    private static ItemHoldLoopingSound currentLoopSound = null;

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            stopCurrentLoop();
            return;
        }

        ItemStack stack = minecraft.player.getMainHandItem();
        boolean holdingRifle = stack.getItem() instanceof SingularityRifle;

        if (holdingRifle) {
            float volume = FirearmMode.getVolume(stack);

            if (currentLoopSound == null || currentLoopSound.isStopped()) {
                currentLoopSound = new ItemHoldLoopingSound(
                        RSRifleSounds.ELECTRIC_BUZZ_STEREO.get(),
                        minecraft.player,
                        RSRifleItems.SINGULARITY_RIFLE.get(),
                        volume + 0.01f
                );

                minecraft.getSoundManager().play(currentLoopSound);
            }

            currentLoopSound.setVolume(volume);
        } else {
            stopCurrentLoop();
        }
    }

    private static void stopCurrentLoop() {
        if (currentLoopSound != null && !currentLoopSound.isStopped()) {
            currentLoopSound.remove();
        }

        currentLoopSound = null;
    }
}