package com.mod.rsrifle;

import com.mod.rsrifle.api.FovModifyingItem;
import com.mod.rsrifle.api.HoldAttackKeyInteraction;
import com.mod.rsrifle.items.SingularityRifle;
import com.mod.rsrifle.network.RSRifleClientNetwork;
import com.mod.rsrifle.network.packet.ServerboundFirearmActionPacket;
import com.mod.rsrifle.network.packet.ServerboundSetAttackKeyPacket;
import com.mod.rsrifle.utils.FirearmDataUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class RSRifleClient {
    public static final KeyMapping RELOAD_RIFLE = createSafeKeyMapping(
            "key.rsrifle.reload",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R
    );

    public static final KeyMapping CHARGE_RIFLE = createSafeKeyMapping(
            "key.rsrifle.charge_firearm",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G
    );

    public static boolean chargeKeyIsPressed = false;

    private static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(
                ReinforcedSingularityRifle.MODID,
                path
        );
    }

    public static void onClientSetup() {
        ItemProperties.registerGeneric(resource("power_count"), (itemStack, level, entity, seed) -> 0.0f);

        ItemProperties.registerGeneric(resource("aiming"), (itemStack, level, entity, seed) ->
                entity instanceof Player player
                        ? player.isUsingItem() ? 1.0f : 0.0f
                        : FirearmDataUtils.isAiming(itemStack) ? 1.0f : 0.0f
        );

        ItemProperties.registerGeneric(resource("is_reloading"), (itemStack, level, entity, seed) ->
                itemStack.getItem() instanceof SingularityRifle firearm
                        && firearm.getCurrentAction(itemStack) == SingularityRifle.Action.RELOAD
                        ? 1.0f
                        : 0.0f
        );

        ItemProperties.registerGeneric(resource("is_charging"), (itemStack, level, entity, seed) ->
                itemStack.getItem() instanceof SingularityRifle firearm
                        && firearm.getCurrentAction(itemStack) == SingularityRifle.Action.CHARGE_START
                        ? 1.0f
                        : 0.0f
        );

        ItemProperties.registerGeneric(resource("is_firing"), (itemStack, level, entity, seed) ->
                itemStack.getItem() instanceof SingularityRifle firearm
                        && firearm.getCurrentAction(itemStack) == SingularityRifle.Action.FIRING
                        ? 1.0f
                        : 0.0f
        );
    }

    public static KeyMapping createSafeKeyMapping(
            String description,
            InputConstants.Type type,
            int key
    ) {
        return new KeyMapping(
                description,
                type,
                key,
                "key.categories.rsrifle"
        );
    }

    public static void onMouseInput(int button, int action, int modifiers) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return;
        }

        boolean attacking = minecraft.options.keyAttack.isDown();
        ItemStack mainHandItem = minecraft.player.getMainHandItem();

        if (mainHandItem.getItem() instanceof HoldAttackKeyInteraction holdAttackKeyInteraction) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && attacking) {
                RSRifleClientNetwork.sendToServer(new ServerboundSetAttackKeyPacket(false));
                holdAttackKeyInteraction.onReleaseAttackKey(mainHandItem, minecraft.player);
            }
        }
    }

    public static void onKeyInput(int key, int scanCode, int action, int modifiers) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.screen != null) {
            return;
        }

        ItemStack useStack = minecraft.player.getMainHandItem();

        if (useStack.getItem() instanceof SingularityRifle) {
            if (action == GLFW.GLFW_PRESS && RELOAD_RIFLE.matches(key, scanCode)) {
                RSRifleClientNetwork.sendToServer(
                        new ServerboundFirearmActionPacket(SingularityRifle.Action.RELOAD)
                );
            }

            if (CHARGE_RIFLE.isDown()) {
                if (!chargeKeyIsPressed) {
                    chargeKeyIsPressed = true;

                    RSRifleClientNetwork.sendToServer(
                            new ServerboundFirearmActionPacket(SingularityRifle.Action.CHARGE_START)
                    );
                }
            } else if (chargeKeyIsPressed) {
                chargeKeyIsPressed = false;

                RSRifleClientNetwork.sendToServer(
                        new ServerboundFirearmActionPacket(SingularityRifle.Action.CHARGE_END)
                );
            }
        } else if (chargeKeyIsPressed) {
            chargeKeyIsPressed = false;

            RSRifleClientNetwork.sendToServer(
                    new ServerboundFirearmActionPacket(SingularityRifle.Action.CHARGE_END)
            );
        }
    }

    public static void registerKeyMappings(Consumer<KeyMapping> consumer) {
        consumer.accept(RELOAD_RIFLE);
        consumer.accept(CHARGE_RIFLE);
    }

    public static void onRenderEntity(RenderLivingEvent.Pre<?, ?> event) {
    }

    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
    }

    public static float modifyFov(float currentFovModifier, Player player) {
        if (player == null) {
            return currentFovModifier;
        }

        Minecraft minecraft = Minecraft.getInstance();

        ItemStack itemStack = player.getMainHandItem();

        float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(false);

        return itemStack.getItem() instanceof FovModifyingItem fovModifier
                ? fovModifier.getFov(
                itemStack,
                player,
                currentFovModifier,
                partialTick
        )
                : currentFovModifier;
    }
}