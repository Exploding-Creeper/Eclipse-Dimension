package com.mystic.eclipse.client;

import com.mystic.eclipse.client.particle.VoidParticle;
import com.mystic.eclipse.client.sky.EclipseDimensionSky;
import com.mystic.eclipse.init.BlockInit;
import com.mystic.eclipse.init.ParticleInit;
import com.mystic.eclipse.utils.Reference;
import com.mystic.eclipse.worldgen.dimension.EclipseDimension;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EclipseClient {
    @SubscribeEvent
    public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(EclipseDimension.ECLIPSE_DIMENSION_TYPE_KEY.location(), new EclipseDimensionSky());
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleInit.VOID.get(), VoidParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tintIndex) -> 0xFFFFFF, BlockInit.NYX_HUMUS.get(), BlockInit.NYX_SERO.get());
        event.register((state, world, pos, tintIndex) -> 0xFFFFFF, BlockInit.SOL_HUMUS.get(), BlockInit.SOL_SERO.get());
        event.register((state, world, pos, tintIndex) -> 0x929292, BlockInit.LUX_HUMUS.get(), BlockInit.LUX_SERO.get());
    }
}
