package com.mystic.eclipse.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mystic.eclipse.client.sky.EclipseDimensionSky;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

@Environment(value = EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
	private void eclipse$renderSky(MatrixStack matrices, Matrix4f matrix4f, float f, Runnable runnable, CallbackInfo ci) {
		if (client.world.getDimensionEffects() instanceof EclipseDimensionSky) {
			BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
			RenderSystem.setShaderTexture(0, new Identifier("eclipse:textures/sky/moon_phases.png"));
			int phase = this.client.world.getMoonPhase();

			ci.cancel();
		}
	}

}
