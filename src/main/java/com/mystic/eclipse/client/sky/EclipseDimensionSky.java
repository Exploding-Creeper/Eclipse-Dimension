package com.mystic.eclipse.client.sky;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mystic.eclipse.utils.Reference;
import com.mystic.eclipse.worldgen.biomes.EclipseZones;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class EclipseDimensionSky extends DimensionSpecialEffects {
    private static final ResourceLocation SUN_PHASES = ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/sky/sun_phases.png");
    private static final ResourceLocation MOON_PHASES = ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/sky/moon_phases.png");

    public EclipseDimensionSky() {
        super(192.0F, true, SkyType.NORMAL, false, false);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 color, float brightness) {
        return color.multiply(brightness * 0.94F + 0.06F, brightness * 0.94F + 0.06F, brightness * 0.91F + 0.09F);
    }

    @Override
    public boolean isFoggyAt(int x, int z) {
        return false;
    }

    @Override
    public float[] getSunriseColor(float timeOfDay, float partialTick) {
        float[] color = super.getSunriseColor(timeOfDay, partialTick);
        if (color == null) {
            return null;
        }
        float gray = (color[0] + color[1] + color[2]) / 3.0F;
        color[0] = gray;
        color[1] = gray;
        color[2] = gray;
        return color;
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        setupFog.run();
        if (isFoggy) {
            return true;
        }
        FogType fogType = camera.getFluidInCamera();
        if (fogType == FogType.POWDER_SNOW || fogType == FogType.LAVA || blocksSky(camera)) {
            return true;
        }

        Vec3 skyColor = level.getSkyColor(camera.getPosition(), partialTick);
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor((float) skyColor.x, (float) skyColor.y, (float) skyColor.z, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        PoseStack modelView = RenderSystem.getModelViewStack();
        modelView.pushPose();
        modelView.setIdentity();
        modelView.mulPoseMatrix(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();
        drawSkyDisc(16.0F);
        modelView.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableBlend();

        float[] sunrise = getSunriseColor(level.getTimeOfDay(partialTick), partialTick);
        if (sunrise != null) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.pushPose();
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F));
            float spin = Mth.sin(level.getSunAngle(partialTick)) < 0.0F ? 180.0F : 0.0F;
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(spin));
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
            Matrix4f matrix = poseStack.last().pose();
            BufferBuilder buffer = Tesselator.getInstance().getBuilder();
            buffer.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
            buffer.vertex(matrix, 0.0F, 100.0F, 0.0F).color(sunrise[0], sunrise[1], sunrise[2], sunrise[3]).endVertex();
            for (int step = 0; step <= 16; step++) {
                float angle = step * ((float) Math.PI * 2.0F) / 16.0F;
                float sine = Mth.sin(angle);
                float cosine = Mth.cos(angle);
                buffer.vertex(matrix, sine * 120.0F, cosine * 120.0F, -cosine * 40.0F * sunrise[3]).color(sunrise[0], sunrise[1], sunrise[2], 0.0F).endVertex();
            }
            BufferUploader.drawWithShader(buffer.end());
            poseStack.popPose();
        }

        float rainVisibility = 1.0F - level.getRainLevel(partialTick);
        poseStack.pushPose();
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-90.0F));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(level.getTimeOfDay(partialTick) * 360.0F));
        Matrix4f celestial = poseStack.last().pose();

        FogRenderer.setupNoFog();
        RenderSystem.disableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        float sun = sunStrength(camera);
        RenderSystem.setShaderColor(sun, sun, sun, rainVisibility);
        RenderSystem.setShaderTexture(0, SUN_PHASES);
        drawPhase(celestial, 30.0F, 100.0F, level.getMoonPhase());

        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, rainVisibility);
        RenderSystem.setShaderTexture(0, MOON_PHASES);
        drawPhase(celestial, 20.0F, -100.0F, level.getMoonPhase());
        RenderSystem.enableDepthTest();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        poseStack.popPose();
        setupFog.run();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        return true;
    }

    private static void drawSkyDisc(float y) {
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        buffer.vertex(0.0D, y, 0.0D).endVertex();
        for (int angle = -180; angle <= 180; angle += 45) {
            buffer.vertex(Mth.cos(angle * ((float) Math.PI / 180.0F)) * 512.0F, y, Mth.sin(angle * ((float) Math.PI / 180.0F)) * 512.0F).endVertex();
        }
        BufferUploader.drawWithShader(buffer.end());
    }

    private static void drawPhase(Matrix4f matrix, float size, float y, int phase) {
        int column = phase % 4;
        int row = phase / 4 % 2;
        float padU = 0.5F / 128.0F;
        float padV = 0.5F / 64.0F;
        float u0 = column / 4.0F + padU;
        float v0 = row / 2.0F + padV;
        float u1 = (column + 1) / 4.0F - padU;
        float v1 = (row + 1) / 2.0F - padV;
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        if (y > 0.0F) {
            buffer.vertex(matrix, -size, y, -size).uv(u0, v0).endVertex();
            buffer.vertex(matrix, size, y, -size).uv(u1, v0).endVertex();
            buffer.vertex(matrix, size, y, size).uv(u1, v1).endVertex();
            buffer.vertex(matrix, -size, y, size).uv(u0, v1).endVertex();
        } else {
            buffer.vertex(matrix, -size, y, size).uv(u1, v1).endVertex();
            buffer.vertex(matrix, size, y, size).uv(u0, v1).endVertex();
            buffer.vertex(matrix, size, y, -size).uv(u0, v0).endVertex();
            buffer.vertex(matrix, -size, y, -size).uv(u1, v0).endVertex();
        }
        BufferUploader.drawWithShader(buffer.end());
    }

    private static float sunStrength(Camera camera) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return 1.0F;
        }
        return EclipseZones.sunStrength(level, BlockPos.containing(camera.getPosition()));
    }

    private static boolean blocksSky(Camera camera) {
        if (!(camera.getEntity() instanceof LivingEntity living)) {
            return false;
        }
        return living.hasEffect(MobEffects.BLINDNESS) || living.hasEffect(MobEffects.DARKNESS);
    }
}
