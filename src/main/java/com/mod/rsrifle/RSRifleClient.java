package com.mod.rsrifle;

import com.mod.rsrifle.api.FovModifyingItem;
import com.mod.rsrifle.api.HoldAttackKeyInteraction;
import com.mod.rsrifle.client.StarburstRenderer;
import com.mod.rsrifle.items.SingularityRifle;
import com.mod.rsrifle.network.RSRifleNetwork;
import com.mod.rsrifle.network.packet.ServerboundFirearmActionPacket;
import com.mod.rsrifle.network.packet.ServerboundSetAttackKeyPacket;
import com.mod.rsrifle.utils.FirearmDataUtils;
import com.mod.rsrifle.utils.WobbleMath;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ViewportEvent;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class RSRifleClient {
    public static final KeyMapping RELOAD_RIFLE = createSafeKeyMapping("key.reinforcedblackholes.reload_firearm", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R);
    public static final KeyMapping CHARGE_RIFLE = createSafeKeyMapping("key.reinforcedblackholes.charge_firearm", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G);
    public static boolean chargeKeyIsPressed = false;

    private static ResourceLocation resource(String string) {
        return ResourceLocation.fromNamespaceAndPath(ReinforcedSingularityRifle.MODID,string);
    }
    
    public static void onClientSetup() {
        ItemProperties.registerGeneric(resource("power_count"), (itemStack, level, entity, seed) -> {
            return 0;
        });
        ItemProperties.registerGeneric(resource("power_count"), (itemStack, level, entity, seed) -> {
            return 0;
        });
        ItemProperties.registerGeneric(resource("aiming"), (itemStack, level, entity, seed) -> {
            return (entity instanceof Player ? entity.isUsingItem() : FirearmDataUtils.isAiming(itemStack)) ? 1 : 0;
        });
        ItemProperties.registerGeneric(resource("is_reloading"), (itemStack, level, entity, seed) -> {
            return itemStack.getItem() instanceof SingularityRifle firearm
                    && firearm.getCurrentAction(itemStack) == SingularityRifle.Action.RELOAD ? 1 : 0;
        });
        ItemProperties.registerGeneric(resource("is_charging"), (itemStack, level, entity, seed) -> {
            return itemStack.getItem() instanceof SingularityRifle firearm
                    && firearm.getCurrentAction(itemStack) == SingularityRifle.Action.CHARGE_START ? 1 : 0;
        });
        ItemProperties.registerGeneric(resource("is_firing"), (itemStack, level, entity, seed) -> {
            return itemStack.getItem() instanceof SingularityRifle firearm
                    && firearm.getCurrentAction(itemStack) == SingularityRifle.Action.FIRING ? 1 : 0;
        });
    }

    public static KeyMapping createSafeKeyMapping(String description, InputConstants.Type type, int key) {
        return new KeyMapping(description, type, key, "key.reinforcedblackholes.category");
    }


    public static void onMouseInput(int button, int action, int modifiers) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        boolean attacking = mc.options.keyAttack.isDown();
        ItemStack mainhandItem = mc.player.getMainHandItem();
        if (mainhandItem.getItem() instanceof HoldAttackKeyInteraction holdAttackKeyInteraction) {
            if (button == 0 && attacking) {
                RSRifleNetwork.sendToServer(new ServerboundSetAttackKeyPacket(false));
                holdAttackKeyInteraction.onReleaseAttackKey(mainhandItem, mc.player);
            }
        }
    }

    public static void onKeyInput(int key, int scancode, int action, int mods) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null && mc.screen == null) {
            ItemStack useStack = mc.player.getMainHandItem();
            if (useStack.getItem() instanceof SingularityRifle) {
                if (RELOAD_RIFLE.isDown()) {
                    RSRifleNetwork.sendToServer(new ServerboundFirearmActionPacket(SingularityRifle.Action.RELOAD));
                }
                if (CHARGE_RIFLE.isDown()) {
                    if (!chargeKeyIsPressed) {
                        chargeKeyIsPressed = true;
                        RSRifleNetwork.sendToServer(new ServerboundFirearmActionPacket(SingularityRifle.Action.CHARGE_START));
                    }
                } else {
                    if (chargeKeyIsPressed) {
                        chargeKeyIsPressed = false;
                        RSRifleNetwork.sendToServer(new ServerboundFirearmActionPacket(SingularityRifle.Action.CHARGE_END));
                    }
                }
            } else if (chargeKeyIsPressed) {
                chargeKeyIsPressed = false;
                RSRifleNetwork.sendToServer(new ServerboundFirearmActionPacket(SingularityRifle.Action.CHARGE_END));
            }
        }
    }

    public static void registerKeyMappings(Consumer<KeyMapping> cons) {
        cons.accept(RELOAD_RIFLE);
        cons.accept(CHARGE_RIFLE);
    }

    public static void onRenderEntity(RenderLivingEvent.Pre<?, ?> event) {
//        PoseStack poseStack = event.getPoseStack();
//
//        float tiltDeg = WobbleMath.getTilt(event.getPartialTick());
//
////        poseStack.translate(0.0D, 1.0D, 0.0D);
////        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(tiltDeg));
////        poseStack.translate(0.0D, -1.0D, 0.0D);
//
//        poseStack.pushPose();
//        poseStack.translate(0, 1, 0);
//        StarburstRenderer.renderStarburst(poseStack, event.getMultiBufferSource(), (float) Math.abs(Math.sin((double) System.currentTimeMillis() / 500)), 0xCCAA00FF, 432L, 30, 80, 10.f);
//        poseStack.popPose();
    }

    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
//        Minecraft mc = Minecraft.getInstance();
//        if (mc.player == null) return;
//        if (event.getCamera().getEntity() != mc.player) return;
//
//        float tiltDeg = WobbleMath.getTilt(event.getPartialTick());
//
//        event.setRoll(event.getRoll() - tiltDeg);
//        event.setPitch(event.getPitch() + tiltDeg);
    }

    public static float modifyFov(float currentFovModifier, Player player) {
        Minecraft mc = Minecraft.getInstance();
        // TODO offhand modifier - take lowest fov modifier
        ItemStack itemStack = player.getMainHandItem();
        float partialTicks = mc.getPartialTick();
        return itemStack.getItem() instanceof FovModifyingItem fovModifier ? fovModifier.getFov(itemStack, player, currentFovModifier, partialTicks) : currentFovModifier;
    }
}
