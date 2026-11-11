package com.mystic.eclipse.fluids;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class EclipseFluidType extends FluidType {
    private final ResourceLocation still;
    private final ResourceLocation flowing;
    private final int tint;
    private final float fog;

    public EclipseFluidType(Properties properties, ResourceLocation still, ResourceLocation flowing, int tint) {
        super(properties);
        this.still = still;
        this.flowing = flowing;
        this.tint = tint;
        int red = (tint >> 16) & 255;
        int green = (tint >> 8) & 255;
        int blue = tint & 255;
        this.fog = (red + green + blue) / (3.0F * 255.0F);
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return still;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return flowing;
            }

            @Override
            public int getTintColor() {
                return tint;
            }

            @Override
            public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                return new Vector3f(fog, fog, fog);
            }

            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                RenderSystem.setShaderFogStart(0.0F);
                RenderSystem.setShaderFogEnd(14.0F);
            }
        });
    }
}
