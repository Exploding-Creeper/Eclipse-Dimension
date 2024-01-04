package com.mystic.eclipse.client.sky;

import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.Vec3d;

public class EclipseDimensionSky extends DimensionEffects {

	public EclipseDimensionSky() {
		super(Float.NaN, false, SkyType.NONE, false, false);
	}

	@Override
	public Vec3d adjustFogColor(Vec3d var1, float var2) {
		return var1;
	}

	@Override
	public boolean useThickFog(int var1, int var2) {
		return false;
	}
}
