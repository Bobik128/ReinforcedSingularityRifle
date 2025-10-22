package com.mod.rsrifle.remix;

import com.mod.rsrifle.api.HoldAttackKeyInteraction;
import com.mod.rsrifle.network.RSRifleNetwork;
import com.mod.rsrifle.network.packet.ServerboundSetAttackKeyPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class RSRifleClientRemix {
    public static void handleAttackKeybinds() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack mainhandItem = mc.player.getMainHandItem();
        if (mainhandItem.getItem() instanceof HoldAttackKeyInteraction hold && !hold.isHoldingAttackKey(mainhandItem, mc.player)) {
            RSRifleNetwork.sendToServer(new ServerboundSetAttackKeyPacket(true));
            hold.onPressAttackKey(mainhandItem, mc.player);
        }
    }

    public static void interruptAttack() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack mainhandItem = mc.player.getMainHandItem();
        if (mainhandItem.getItem() instanceof HoldAttackKeyInteraction hold && hold.isHoldingAttackKey(mainhandItem, mc.player)) {
            RSRifleNetwork.sendToServer(new ServerboundSetAttackKeyPacket(false));
            hold.onReleaseAttackKey(mainhandItem, mc.player);
        }
    }
}
