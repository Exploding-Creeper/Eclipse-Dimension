package com.mystic.eclipse.init;

import com.mystic.eclipse.utils.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundInit {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Reference.MODID);

    public static final RegistryObject<SoundEvent> WHITEWASH = register("music_disc.whitewash");
    public static final RegistryObject<SoundEvent> DAWNLIGHT = register("music_disc.dawnlight");
    public static final RegistryObject<SoundEvent> DOWNTIME = register("music_disc.downtime");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MODID, name)));
    }
}
