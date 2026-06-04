package com.mod.rsrifle.items.renderer;

import com.mod.rsrifle.items.SingularityRifle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

public class PlayerArmsRenderLayer extends GeoRenderLayer<SingularityRifle> {

    public PlayerArmsRenderLayer(GeoRenderer<SingularityRifle> renderer) {
        super(renderer);
    }

    @Override
    public void render(
            PoseStack poseStack,
            SingularityRifle animatable,
            BakedGeoModel bakedModel,
            RenderType renderType,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            float partialTick,
            int packedLight,
            int packedOverlay
    ) {
        SingularityRifleRenderer renderer = (SingularityRifleRenderer) this.getRenderer();

        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }

        if (renderer.getRenderPerspective() != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            return;
        }

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        PlayerRenderer playerRenderer = (PlayerRenderer) dispatcher.getRenderer(player);
        PlayerModel<AbstractClientPlayer> playerModel = playerRenderer.getModel();

        this.getGeoModel().getBone("R_ARM").ifPresent(bone ->
                renderArm(
                        poseStack,
                        bufferSource,
                        player,
                        playerModel.rightArm,
                        playerModel.rightSleeve,
                        bone,
                        packedLight
                )
        );

        this.getGeoModel().getBone("L_ARM").ifPresent(bone ->
                renderArm(
                        poseStack,
                        bufferSource,
                        player,
                        playerModel.leftArm,
                        playerModel.leftSleeve,
                        bone,
                        packedLight
                )
        );
    }

    private static void renderArm(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            AbstractClientPlayer player,
            ModelPart arm,
            ModelPart sleeve,
            GeoBone bone,
            int packedLight
    ) {
        arm.resetPose();
        sleeve.resetPose();

        poseStack.pushPose();

        translateToBoneChain(poseStack, bone);

        poseStack.translate(
                -arm.x / 16.0f,
                -arm.y / 16.0f,
                -arm.z / 16.0f
        );

        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0f));

        arm.render(
                poseStack,
                bufferSource.getBuffer(RenderType.entitySolid(player.getSkin().texture())),
                packedLight,
                OverlayTexture.NO_OVERLAY
        );

        sleeve.render(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucent(player.getSkin().texture())),
                packedLight,
                OverlayTexture.NO_OVERLAY
        );

        poseStack.popPose();
    }

    private static void translateToBoneChain(PoseStack poseStack, GeoBone bone) {
        GeoBone parent = bone.getParent();

        if (parent != null) {
            translateToBoneChain(poseStack, parent);
        }

        translateToBone(poseStack, bone);
    }

    private static void translateToBone(PoseStack poseStack, GeoBone bone) {
        RenderUtil.translateMatrixToBone(poseStack, bone);
        RenderUtil.translateToPivotPoint(poseStack, bone);
        RenderUtil.rotateMatrixAroundBone(poseStack, bone);
        RenderUtil.scaleMatrixForBone(poseStack, bone);
        RenderUtil.translateAwayFromPivotPoint(poseStack, bone);
    }
}