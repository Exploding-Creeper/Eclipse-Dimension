package com.mystic.eclipse;

import com.mystic.eclipse.creativetab.EclipseGroup;
import com.mystic.eclipse.init.BlockInit;
import com.mystic.eclipse.init.FluidInit;
import com.mystic.eclipse.init.ItemInit;
import com.mystic.eclipse.init.ParticleInit;
import com.mystic.eclipse.init.SoundInit;
import com.mystic.eclipse.utils.Reference;
import com.mystic.eclipse.worldgen.dimension.EclipseDimension;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MODID)
public class EclipseMain {

    public static final Logger LOGGER = LogManager.getLogger(Reference.MODID);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Reference.MODID, path);
    }

    public EclipseMain(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();
        SoundInit.SOUNDS.register(bus);
        ParticleInit.PARTICLES.register(bus);
        FluidInit.FLUID_TYPES.register(bus);
        FluidInit.FLUIDS.register(bus);
        FluidInit.FLUID_BLOCKS.register(bus);
        BlockInit.BLOCKS.register(bus);
        BlockInit.BLOCK_ITEMS.register(bus);
        ItemInit.ITEMS.register(bus);
        FluidInit.BUCKETS.register(bus);
        EclipseGroup.TABS.register(bus);
        EclipseDimension.BIOME_SOURCES.register(bus);
    }
}
