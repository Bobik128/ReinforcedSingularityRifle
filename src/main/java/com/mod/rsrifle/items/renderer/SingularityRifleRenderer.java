package com.mod.rsrifle.items.renderer;

import com.mod.rbh.client.RBHCameraInfo;
import com.mod.rbh.compat.ShaderCompat;
import com.mod.rbh.entity.renderer.BlackHoleRenderer;
import com.mod.rbh.shaders.PostEffectRegistry;
import com.mod.rbh.shaders.RBHRenderTypes;
import com.mod.rbh.utils.LightningRenderUtil;
import com.mod.rsrifle.client.RifleIcons;
import com.mod.rsrifle.items.SingularityBattery;
import com.mod.rsrifle.items.SingularityRifle;
import com.mod.rsrifle.shaders.RifleHoleEffectInstanceHolder;
import com.mod.rsrifle.utils.FirearmDataUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.RenderUtil;

import java.awt.Color;
import java.util.Objects;
import java.util.Optional;

import static com.mod.rsrifle.items.renderer.SingularityRifleModel.shootTriggered;

public class SingularityRifleRenderer extends GeoItemRenderer<SingularityRifle> {
    private final Matrix4f handProj = new Matrix4f();
    private final Vector3f holeViewHand = new Vector3f();
    private final Vector3f holeWorld = new Vector3f();

    private int cachedColor = 0x000000;

    public SingularityRifleRenderer() {
        super(new SingularityRifleModel());
        ((SingularityRifleModel) this.model).renderer = this;

        this.addRenderLayer(new PlayerArmsRenderLayer(this));
    }

    @Override
    public RenderType getRenderType(
            SingularityRifle animatable,
            ResourceLocation texture,
            @Nullable MultiBufferSource bufferSource,
            float partialTick
    ) {
        return RenderType.entityCutout(texture);
    }

    @Override
    public void preRender(
            PoseStack poseStack,
            SingularityRifle animatable,
            BakedGeoModel model,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            boolean isReRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            int color
    ) {
        super.preRender(
                poseStack,
                animatable,
                model,
                bufferSource,
                buffer,
                isReRender,
                partialTick,
                packedLight,
                packedOverlay,
                color
        );

        model.getBone("L_ARM").ifPresent(bone -> bone.setHidden(true));
        model.getBone("R_ARM").ifPresent(bone -> bone.setHidden(true));

        this.cachedColor = FirearmDataUtils.getColor(this.currentItemStack);
    }

    public boolean shouldRenderHoleNormally() {
        return !ShaderCompat.shadersEnabled()
                || !(this.renderPerspective == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || this.renderPerspective == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
    }

    public ItemDisplayContext getRenderPerspective() {
        return this.renderPerspective;
    }

    private static int alpha(int argb) {
        int alpha = (argb >>> 24) & 0xFF;

        /*
         * Some callers may pass RGB-only color.
         * Treat missing alpha as fully opaque.
         */
        return alpha == 0 ? 0xFF : alpha;
    }

    private static int floatToChannel(float value) {
        return Math.clamp(Math.round(value * 255.0f), 0, 255);
    }

    private static int argb(int alpha, int red, int green, int blue) {
        return ((alpha & 0xFF) << 24)
                | ((red & 0xFF) << 16)
                | ((green & 0xFF) << 8)
                | (blue & 0xFF);
    }

    @Override
    public void renderRecursively(
            PoseStack poseStack,
            SingularityRifle animatable,
            GeoBone bone,
            RenderType renderType,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            boolean isReRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            int color
    ) {
        if (bone.isTrackingMatrices()) {
            Matrix4f poseState = new Matrix4f(poseStack.last().pose());

            bone.setModelSpaceMatrix(
                    RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations)
            );

            bone.setLocalSpaceMatrix(
                    RenderUtil.invertAndMultiplyMatrices(poseState, this.itemRenderTranslations)
            );
        }

        int renderColor = color;

        if (this.isGlowingPart(bone.getName())) {
            if (((SingularityRifle) this.currentItemStack.getItem()).shouldBeColorful(this.currentItemStack)) {
                float[] rgb = BlackHoleRenderer.glowColor(
                        System.currentTimeMillis(),
                        6.0f,
                        1.0f,
                        0.9f,
                        2.0f
                );

                renderColor = argb(
                        alpha(color),
                        floatToChannel(rgb[0]),
                        floatToChannel(rgb[1]),
                        floatToChannel(rgb[2])
                );
            } else {
                renderColor = argb(
                        alpha(color),
                        (this.cachedColor >> 16) & 0xFF,
                        (this.cachedColor >> 8) & 0xFF,
                        this.cachedColor & 0xFF
                );
            }
        }

        poseStack.pushPose();

        RenderUtil.prepMatrixForBone(poseStack, bone);

        ResourceLocation texture = this.getTextureLocation(this.animatable);

        RenderType renderTypeOverride = this.getRenderTypeOverrideForBone(
                bone,
                animatable,
                texture,
                bufferSource,
                partialTick
        );

        if (texture != null && renderTypeOverride == null) {
            renderTypeOverride = this.getRenderType(
                    this.animatable,
                    texture,
                    bufferSource,
                    partialTick
            );
        }

        if (renderTypeOverride != null) {
            buffer = bufferSource.getBuffer(renderTypeOverride);
        }

        super.renderCubesOfBone(
                poseStack,
                bone,
                buffer,
                this.isGlowingPart(bone.getName()) ? 0xF000F0 : packedLight,
                packedOverlay,
                renderColor
        );

        if (renderTypeOverride != null) {
            buffer = bufferSource.getBuffer(
                    Objects.requireNonNull(this.getRenderType(
                            this.animatable,
                            this.getTextureLocation(this.animatable),
                            bufferSource,
                            partialTick
                    ))
            );
        }

        if (!isReRender) {
            this.applyRenderLayersForBone(
                    poseStack,
                    animatable,
                    bone,
                    renderType,
                    bufferSource,
                    buffer,
                    partialTick,
                    packedLight,
                    packedOverlay
            );
        }

        this.renderChildBones(
                poseStack,
                animatable,
                bone,
                renderType,
                bufferSource,
                buffer,
                isReRender,
                partialTick,
                packedLight,
                packedOverlay,
                renderColor
        );

        if (bone.getName().equals("monitor")) {
            this.renderText(
                    poseStack,
                    bufferSource,
                    0xF000F0,
                    packedOverlay,
                    animatable
            );
        }

        poseStack.popPose();

        if (FirearmDataUtils.getChargeLevel(this.currentItemStack) <= 0) {
            return;
        }

        if (bone.getName().equals("blackHoleLocatorPre")) {
            this.renderBlackHoleLocator(
                    poseStack,
                    bone,
                    packedLight
            );
        }
    }

    private void renderBlackHoleLocator(
            PoseStack poseStack,
            GeoBone bone,
            int packedLight
    ) {
        if (!(this.renderPerspective == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || this.renderPerspective == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || this.renderPerspective == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                || this.renderPerspective == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                || this.renderPerspective == ItemDisplayContext.GROUND
                || this.renderPerspective == ItemDisplayContext.FIXED)) {
            return;
        }

        boolean isFirstPerson = this.renderPerspective == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || this.renderPerspective == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;

        float modifier = (float) FirearmDataUtils.getChargeLevel(this.currentItemStack)
                / SingularityRifle.MAX_CHARGE_LEVEL;

        poseStack.pushPose();

        poseStack.translate(0.0f, 0.3125f, -0.421f);
        poseStack.translate(
                bone.getPosX() / 16.0f,
                bone.getPosY() / 16.0f,
                bone.getPosZ() / 16.0f
        );

        if (this.shouldRenderHoleNormally()) {
            PostEffectRegistry.HoleEffectInstance holeEffectInstance =
                    RifleHoleEffectInstanceHolder.getUniqueEffect();

            if (holeEffectInstance != null) {
                BlackHoleRenderer.renderBlackHole(
                        poseStack,
                        holeEffectInstance,
                        isFirstPerson
                                ? PostEffectRegistry.RenderPhase.AFTER_ARM
                                : PostEffectRegistry.RenderPhase.AFTER_LEVEL,
                        packedLight,
                        SingularityRifle.MAX_EFFECT_SIZE * modifier,
                        SingularityRifle.MAX_SIZE * modifier,
                        ((SingularityRifle) this.currentItemStack.getItem()).shouldBeColorful(this.currentItemStack),
                        Color.YELLOW.getRGB(),
                        4.0f
                );
            }
        }

        this.holeViewHand.set(poseStack.last().pose().getTranslation(new Vector3f()));
        this.handProj.set(com.mojang.blaze3d.systems.RenderSystem.getProjectionMatrix());

        Minecraft minecraft = Minecraft.getInstance();
        var camera = minecraft.gameRenderer.getMainCamera();

        Quaternionf cameraRotation = new Quaternionf(camera.rotation());
        Vec3 cameraPosition = camera.getPosition();

        this.holeWorld
                .set(this.holeViewHand)
                .rotate(cameraRotation)
                .add(
                        (float) cameraPosition.x,
                        (float) cameraPosition.y,
                        (float) cameraPosition.z
                );

        poseStack.popPose();
    }

    private void renderText(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int overlay,
            SingularityRifle animatable
    ) {
        Font font = Minecraft.getInstance().font;

        poseStack.pushPose();
        poseStack.translate(0.0, 0.43, 0.2);

        poseStack.pushPose();
        poseStack.translate(-0.011, 0.0, 0.0);

        float fontScale = 0.0026f;
        poseStack.scale(fontScale, -fontScale, fontScale);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));

        font.drawInBatch(
                Integer.toString(FirearmDataUtils.getChargeLevel(this.getCurrentItemStack())),
                0,
                0,
                0xFFFFFF,
                false,
                poseStack.last().pose(),
                bufferSource,
                Font.DisplayMode.NORMAL,
                0,
                packedLight
        );

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-0.034, 0.0, 0.0);

        float fontScale2 = 0.001f;
        poseStack.scale(fontScale2, -fontScale2, fontScale2);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));

        font.drawInBatch(
                "eng.",
                0,
                0,
                0xFFFFFF,
                false,
                poseStack.last().pose(),
                bufferSource,
                Font.DisplayMode.NORMAL,
                0,
                packedLight
        );

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-0.02, 0.0, 0.04);

        float iconScale = 0.0022f;
        poseStack.scale(iconScale, -iconScale, iconScale);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));

        RifleIcons.drawColoredIcon(
                poseStack,
                bufferSource,
                packedLight,
                overlay,
                getIconForEnergy(
                        FirearmDataUtils.getBattery1Energy(this.currentItemStack),
                        SingularityBattery.MAX_ENERGY
                )
        );

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.005, 0.0, 0.04);

        poseStack.scale(iconScale, -iconScale, iconScale);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));

        RifleIcons.drawColoredIcon(
                poseStack,
                bufferSource,
                packedLight,
                overlay,
                getIconForEnergy(
                        FirearmDataUtils.getBattery2Energy(this.currentItemStack),
                        SingularityBattery.MAX_ENERGY
                )
        );

        poseStack.popPose();

        poseStack.popPose();
    }

    private static RifleIcons.Icons getIconForEnergy(int nowEnergy, int maxEnergy) {
        float k = (float) nowEnergy / maxEnergy;

        if (k == 0.0f) {
            return RifleIcons.Icons.EMPTY;
        } else if (k <= 0.25f) {
            return RifleIcons.Icons.QUARTER;
        } else if (k <= 0.5f) {
            return RifleIcons.Icons.HALF;
        } else if (k <= 0.75f) {
            return RifleIcons.Icons.THREE_QUARTERS;
        } else if (k <= 1.0f) {
            return RifleIcons.Icons.FULL;
        } else {
            return RifleIcons.Icons.WARNING;
        }
    }

    public static boolean isGlowingPartStatic(String name) {
        String upperName = name.toUpperCase();
        return upperName.endsWith("_GLOWING") || upperName.endsWith("_EMISSIVE");
    }

    public boolean isGlowingPart(String name) {
        return isGlowingPartStatic(name);
    }

    protected @Nullable RenderType getRenderTypeOverrideForBone(
            GeoBone bone,
            SingularityRifle animatable,
            ResourceLocation texturePath,
            MultiBufferSource bufferSource,
            float partialTick
    ) {
        if (bone != null && this.isGlowingPart(bone.getName())) {
            return RBHRenderTypes.getEmissiveRenderType(texturePath);
        }

        return null;
    }

    @Override
    public void postRender(
            PoseStack poseStack,
            SingularityRifle animatable,
            BakedGeoModel model,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            boolean isReRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            int color
    ) {
        super.postRender(
                poseStack,
                animatable,
                model,
                bufferSource,
                buffer,
                isReRender,
                partialTick,
                packedLight,
                packedOverlay,
                color
        );

        LightningRenderUtil.Params params = new LightningRenderUtil.Params();
        params.worldSpace = false;
        params.seed = System.nanoTime() >> 16;
        params.recursionDepth = 2;

        float k = (float) FirearmDataUtils.getChargeLevel(this.currentItemStack)
                / SingularityRifle.MAX_CHARGE_LEVEL;

        long stackId = GeoItem.getId(this.currentItemStack);

        if (shootTriggered.containsKey(stackId) && Minecraft.getInstance().level != null) {
            double startTick = shootTriggered.get(stackId).triggerTick();
            float modifier = shootTriggered.get(stackId).startModifier();

            double nowTick = Minecraft.getInstance().level.getGameTime()
                    + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);

            if (nowTick - startTick < 3.0) {
                k = modifier;
            }
        }

        if (k <= 0.0f) {
            return;
        }

        params.widthStart = 0.03f * k;
        params.widthEnd = 0.01f * k;

        poseStack.pushPose();

        Optional<GeoBone> rifleBone = model.getBone("rifle");

        rifleBone.ifPresent(bone -> {
            poseStack.translate(
                    (bone.getPivotX() + bone.getPosX()) / 16.0f,
                    (bone.getPivotY() + bone.getPosY()) / 16.0f,
                    (bone.getPivotZ() + bone.getPosZ()) / 16.0f
            );

            Quaternionf q = new Quaternionf();
            q.rotationYXZ(
                    bone.getRotY(),
                    bone.getRotX(),
                    bone.getRotZ()
            );

            poseStack.mulPose(q);

            poseStack.translate(
                    -bone.getPivotX() / 16.0f,
                    -bone.getPivotY() / 16.0f,
                    -bone.getPivotZ() / 16.0f
            );
        });

        Vector3f start = new Vector3f();

        model.getBone("holeInjector").ifPresent(bone ->
                start.set(
                        (bone.getPivotX() + bone.getPosX()) / 16.0f,
                        (bone.getPivotY() + bone.getPosY()) / 16.0f,
                        (bone.getPivotZ() + bone.getPosZ()) / 16.0f
                )
        );

        Vector3f end = new Vector3f();

        model.getBone("blackHoleLocatorPre").ifPresent(bone ->
                end.set(
                        (bone.getPivotX() + bone.getPosX()) / 16.0f,
                        (bone.getPivotY() + bone.getPosY()) / 16.0f,
                        (bone.getPivotZ() + bone.getPosZ()) / 16.0f
                )
        );

        LightningRenderUtil.renderLightning(
                poseStack,
                bufferSource,
                new Vec3(start),
                new Vec3(end),
                params
        );

        LightningRenderUtil.renderLightning(
                poseStack,
                bufferSource,
                new Vec3(start),
                new Vec3(end),
                params
        );

        poseStack.popPose();

        if (!this.shouldRenderHoleNormally()) {
            this.renderHandShaderHole(packedLight);
        }
    }

    private void renderHandShaderHole(int packedLight) {
        Minecraft minecraft = Minecraft.getInstance();
        var camera = minecraft.gameRenderer.getMainCamera();

        Vector3f relNow = new Vector3f(this.holeWorld)
                .sub(
                        (float) camera.getPosition().x,
                        (float) camera.getPosition().y,
                        (float) camera.getPosition().z
                )
                .rotate(new Quaternionf(camera.rotation()).conjugate());

        Matrix4f levelProj = minecraft.gameRenderer.getProjectionMatrix(
                RBHCameraInfo.getFov()
//                Math.toRadians(RBHCameraInfo.getFov()) // if it doesn't work :)
        );

        float m00h = this.handProj.m00();
        float m11h = this.handProj.m11();
        float m00l = levelProj.m00();
        float m11l = levelProj.m11();

        if (Float.isFinite(m00h) && Float.isFinite(m00l) && m00l != 0.0f) {
            relNow.x *= m00h / m00l;
        }

        if (Float.isFinite(m11h) && Float.isFinite(m11l) && m11l != 0.0f) {
            relNow.y *= m11h / m11l;
        }

        float radiusScale = 1.0f;

        if (Float.isFinite(m11h) && Float.isFinite(m11l) && m11l != 0.0f) {
            radiusScale = m11h / m11l;
        }

        PoseStack customStack = new PoseStack();
        customStack.translate(relNow.x, relNow.y, relNow.z);

        float charge = (float) FirearmDataUtils.getChargeLevel(this.currentItemStack)
                / SingularityRifle.MAX_CHARGE_LEVEL;

        float effectRadius = SingularityRifle.MAX_EFFECT_SIZE * charge * radiusScale;
        float holeRadius = SingularityRifle.MAX_SIZE * charge * radiusScale;

        PostEffectRegistry.HoleEffectInstance holeEffectInstance =
                RifleHoleEffectInstanceHolder.getUniqueEffect();

        if (holeEffectInstance != null) {
            BlackHoleRenderer.renderBlackHole(
                    customStack,
                    holeEffectInstance,
                    PostEffectRegistry.RenderPhase.AFTER_LEVEL,
                    packedLight,
                    effectRadius,
                    holeRadius,
                    ((SingularityRifle) this.currentItemStack.getItem()).shouldBeColorful(this.currentItemStack),
                    Color.YELLOW.getRGB(),
                    4.0f
            );
        }
    }
}