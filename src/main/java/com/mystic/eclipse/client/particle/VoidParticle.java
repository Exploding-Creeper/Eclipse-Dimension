package com.mystic.eclipse.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class VoidParticle extends TextureSheetParticle {
    private VoidParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z, dx, dy, dz);
        this.pickSprite(sprites);
        this.lifetime = 18 + level.random.nextInt(14);
        this.gravity = -0.015F;
        this.xd *= 0.15D;
        this.yd = 0.012D + level.random.nextDouble() * 0.02D;
        this.zd *= 0.15D;
        this.quadSize = 0.08F + level.random.nextFloat() * 0.16F;
        float gray = 0.02F + level.random.nextFloat() * 0.16F;
        this.rCol = gray;
        this.gCol = gray;
        this.bCol = gray;
        this.alpha = 0.9F;
        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        super.tick();
        float life = 1.0F - ((float) this.age / (float) this.lifetime);
        this.alpha = 0.9F * life;
        this.quadSize *= 0.98F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new VoidParticle(level, x, y, z, dx, dy, dz, this.sprites);
        }
    }
}
