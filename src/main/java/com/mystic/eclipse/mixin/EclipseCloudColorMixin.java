package com.mystic.eclipse.mixin;

import com.mystic.eclipse.worldgen.biomes.EclipseZones;
import com.mystic.eclipse.worldgen.dimension.EclipseDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public class EclipseCloudColorMixin {
    @Inject(method = "getCloudColor", at = @At("RETURN"), cancellable = true)
    private void eclipse$tintClouds(float partialTick, CallbackInfoReturnable<Vec3> cir) {
        ClientLevel level = (ClientLevel) (Object) this;
        if (!EclipseDimension.isEclipseDimension(level)) {
            return;
        }
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        float shade = EclipseZones.cloudShade(level, net.minecraft.core.BlockPos.containing(camera));
        Vec3 color = cir.getReturnValue();
        cir.setReturnValue(new Vec3(color.x * shade, color.y * shade, color.z * shade));
    }
}
