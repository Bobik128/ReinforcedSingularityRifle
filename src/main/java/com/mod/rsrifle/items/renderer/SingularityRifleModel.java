package com.mod.rsrifle.items.renderer;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import com.mod.rsrifle.client.RifleShootAnimHelper;
import com.mod.rsrifle.items.SingularityRifle;
import com.mod.rsrifle.utils.FirearmDataUtils;
import com.mod.rsrifle.utils.math.ShootAnimFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

import java.util.HashMap;
import java.util.Map;

public class SingularityRifleModel extends DefaultedItemGeoModel<SingularityRifle> {
    private static final float MAX_ANGLE = 16.0f;
    private static final float SHOOT_ANIM_DURATION = 8.0f;

    private static final ShootAnimFunction.Compiled EASE =
            new ShootAnimFunction.Compiled(0.81f, 0.96f, 0.99f, 0.72f, 1.8f);

    public record ShootTrigger(double triggerTick, float startModifier) {
    }

    public static final Map<Long, ShootTrigger> shootTriggered = new HashMap<>();

    public SingularityRifleRenderer renderer;

    public SingularityRifleModel() {
        super(ResourceLocation.fromNamespaceAndPath(
                ReinforcedSingularityRifle.MODID,
                "singularity_rifle"
        ));
    }

    @Override
    public void setCustomAnimations(SingularityRifle animatable, long instanceId, AnimationState<SingularityRifle> animationState) {
        GeoBone lowerHinge = this.getAnimationProcessor().getBone("lowerHinge");
        GeoBone lowerHinge2 = this.getAnimationProcessor().getBone("lowerHinge2");
        GeoBone upperHinge = this.getAnimationProcessor().getBone("upperHinge");
        GeoBone upperHinge2 = this.getAnimationProcessor().getBone("upperHinge2");
        GeoBone holeInjector = this.getAnimationProcessor().getBone("holeInjector");

        if (lowerHinge == null ||
                lowerHinge2 == null ||
                upperHinge == null ||
                upperHinge2 == null ||
                holeInjector == null) {
            return;
        }

        if (this.renderer == null) {
            return;
        }

        ItemStack stack = this.renderer.getCurrentItemStack();

        if (stack.isEmpty() || !(stack.getItem() instanceof SingularityRifle rifle)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        long stackId = GeoItem.getId(stack);
        float modifier;

        if (RifleShootAnimHelper.rifleShooting(stack) && minecraft.level != null) {
            modifier = (float) RifleShootAnimHelper.getChargeLevel(stack) / SingularityRifle.MAX_CHARGE_LEVEL;

            double nowTick = minecraft.level.getGameTime()
                    + minecraft.getTimer().getGameTimeDeltaPartialTick(false);

            shootTriggered.put(stackId, new ShootTrigger(nowTick, modifier));

            RifleShootAnimHelper.remove(stack);

            modifier = modifier * modifier;

            if (minecraft.player != null) {
                rifle.triggerAnim(
                        minecraft.player,
                        stackId,
                        "shoot",
                        "shoot"
                );
            }
        } else {
            modifier = (float) FirearmDataUtils.getChargeLevel(stack) / SingularityRifle.MAX_CHARGE_LEVEL;
            modifier = modifier * modifier;
        }

        ShootTrigger trigger = shootTriggered.get(stackId);

        if (trigger != null && minecraft.level != null) {
            double nowTick = minecraft.level.getGameTime()
                    + minecraft.getTimer().getGameTimeDeltaPartialTick(false);

            double elapsed = nowTick - trigger.triggerTick();
            float linear = (float) (elapsed / SHOOT_ANIM_DURATION);

            float startModifierSquared = trigger.startModifier() * trigger.startModifier();
            float customModifier = (1.0f - EASE.value(linear)) * startModifierSquared;

            if (customModifier >= modifier || elapsed <= 2.0) {
                modifier = customModifier;
            } else {
                shootTriggered.remove(stackId);
            }
        }

        float angle = (float) Math.toRadians(modifier * MAX_ANGLE);

        lowerHinge.updateRotation(-angle, 0.0f, 0.0f);
        lowerHinge2.updateRotation(angle, 0.0f, 0.0f);

        upperHinge2.updateRotation(-angle, 0.0f, 0.0f);
        upperHinge.updateRotation(angle, 0.0f, 0.0f);

        holeInjector.updatePosition(0.0f, 0.0f, modifier);
    }
}