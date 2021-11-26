package com.mystic.eclipse.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mystic.eclipse.client.sky.EclipseDimensionSky;
import com.mystic.eclipse.worldgen.dimension.EclipseDimension;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(DimensionEffects.class)
public class DimensionEffectsMixin {

	@Shadow
	@Final
	private static Object2ObjectMap<Identifier, DimensionEffects> BY_IDENTIFIER;

	static {
		BY_IDENTIFIER.put(EclipseDimension.ECLIPSE_DIMENSION_TYPE_KEY.getValue(), new EclipseDimensionSky());
	}

}
