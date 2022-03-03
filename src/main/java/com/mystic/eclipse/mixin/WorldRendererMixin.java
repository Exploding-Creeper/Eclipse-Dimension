package com.mystic.eclipse.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Environment(value = EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {}
	/*@Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
	private void eclipse$renderSky(MatrixStack matrices, Matrix4f matrix4f, float f, Runnable runnable, CallbackInfo ci) {
		if (client.world.getDimensionEffects() instanceof EclipseDimensionSky) {
			BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
			RenderSystem.setShaderTexture(0, new Identifier("eclipse:textures/sky/moon_phases.png"));
			int phase = this.client.world.getMoonPhase();

			ci.cancel();
		}
	}

}*/
