package com.mod.rsrifle.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mod.rsrifle.items.SingularityRifle;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    @Final
    protected Minecraft minecraft;

    @WrapMethod(method = "renderCrosshair")
    private void rsrifle$renderCrosshair(
            GuiGraphics guiGraphics,
            DeltaTracker deltaTracker,
            Operation<Void> original
    ) {
        if (this.minecraft.player == null) {
            original.call(guiGraphics, deltaTracker);
            return;
        }

        ItemStack mainhandItem = this.minecraft.player.getMainHandItem();

        if (mainhandItem.getItem() instanceof SingularityRifle rifle
                && rifle.isAiming(mainhandItem, this.minecraft.player)) {
            return;
        }

        original.call(guiGraphics, deltaTracker);
    }
}